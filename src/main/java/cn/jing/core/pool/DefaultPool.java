package cn.jing.core.pool;

import cn.jing.core.connection.PooledConnection;
import cn.jing.core.connection.factory.PooledConnectionFactory;
import cn.jing.exception.MaxConnectionException;
import cn.jing.exception.NoFreeConnectionException;
import cn.jing.exception.PropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * Created by dubby on 16/5/3.
 */
public class DefaultPool extends Pool {

    private Logger logger = LoggerFactory.getLogger(DefaultPool.class);

    private String poolId = UUID.randomUUID().toString();
    public ConnectionGC connectionGC;
    public ConnectionGenerator connectionGenerator;

    public DefaultPool(Properties properties, PooledConnectionFactory factory) {
        if (properties == null || properties.isEmpty()) {
            throw new PropertyException();
        }
        //init properties
        if (properties.containsKey("coreNum")) {
            coreNum = Integer.parseInt(properties.get("coreNum").toString());
        }
        if (properties.containsKey("maxNum")) {
            maxNum = Integer.parseInt(properties.get("maxNum").toString());
        }
        if (properties.containsKey("maxIdleNum")) {
            maxIdleNum = Integer.parseInt(properties.get("maxIdleNum").toString());
        }
        if (properties.containsKey("maxIdleTime")) {
            maxIdleTime = Long.parseLong(properties.get("maxIdleTime").toString());
        }
        if (properties.containsKey("testOnBorrow")) {
            testOnBorrow = Boolean.parseBoolean(properties.get("testOnBorrow").toString());
        }
        if (properties.containsKey("testOnReturn")) {
            testOnReturn = Boolean.parseBoolean(properties.get("testOnReturn").toString());
        }

        if (properties.containsKey("testSql")) {
            testSql = properties.get("testSql").toString();
        }
        if (properties.containsKey("minIdleNum")) {
            minIdleNum = Integer.parseInt(properties.get("minIdleNum").toString());
        }
        currentNum = coreNum;
        //init pool
        freePool = new LinkedBlockingQueue<PooledConnection>(maxNum);
        busyPool = new ConcurrentHashMap<String, PooledConnection>(maxIdleNum);
        this.factory = factory;
        factory.setPool(this);

        //fill the free pool
        reset();

        //init gc thread
        connectionGC = new ConnectionGC();
        connectionGC.start();
        connectionGC.startGC();
        connectionGenerator = new ConnectionGenerator();
        connectionGenerator.start();
        connectionGenerator.startGenerate();
    }


    /**
     * 当数据库连接池,初始化时,或者失效时,调用此方法
     * 重新创建所有连接
     */
    private synchronized void reset() {
        logger.debug("reset pool " + poolId);
        try {
            freePool.clear();
            for (int i = 0; i < currentNum; ++i) {
                this.freePool.offer(factory.createConnection());
            }
        } catch (SQLException e) {
            throw new RuntimeException("error occurs while creating connection");
        }
    }

    public PooledConnection getConnection() throws NoFreeConnectionException, MaxConnectionException {
        PooledConnection connection = getConnection(1000 * 60);

        return connection;
    }

    public PooledConnection getConnection(long timeout) throws NoFreeConnectionException, MaxConnectionException {
        try {
            if (freePool.size() + busyPool.size() >= maxNum) {
                throw new MaxConnectionException();
            }
            PooledConnection connection = freePool.poll(timeout, TimeUnit.MILLISECONDS);
            if (connection == null) {
                throw new NoFreeConnectionException();
            }

            //测试连接的有效性
            if (testOnBorrow && !connection.isActive(testSql)) {
                //重新初始化这个连接
                try {
                    connection = factory.createConnection();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            busyPool.put(connection.getId(), connection);
            logger.debug("borrow a connection " + connection == null ? "null" : connection.getId() + " from pool " + poolId);

            //判断当前空闲连接是否 < 最小连接数
            connectionGenerator.startGenerate();

            connection.doBorrow();
            return connection;
        } catch (InterruptedException e) {
            throw new NoFreeConnectionException();
        }
    }

    public void returnConnection(PooledConnection connection) {
        busyPool.remove(connection.getId());

        //测试连接的有效性
        if (testOnReturn && !connection.isActive(testSql)) {
            //重新初始化这个连接
            try {
                connection = factory.createConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        freePool.offer(connection);

        //判断是否满足maxIdleNum的要求,如果满足了,就开启连接回收线程
        int idleNum = freePool.size();
        if (idleNum >= maxIdleNum) {
            connectionGC.startGC();
        }
        logger.debug("return a connection " + connection == null ? "null" : connection.getId() + " to pool " + poolId);
    }


    //回收连接的线程
    public class ConnectionGC extends Thread {

        private boolean isStop = true;

        public void stopGC() {
            isStop = true;
        }

        public void startGC() {
            isStop = false;
        }

        @Override
        public void run() {
            while (true) {
                if (!isStop) {
                    if (freePool.size() <= maxIdleNum && freePool.size() + busyPool.size() < maxNum) {
                        isStop = true;
                    } else {
                        for (PooledConnection c : freePool) {
                            PooledConnection conn = freePool.poll();

                            logger.debug("ConnectionGC thread destroy a connection " + conn.getId() + " from pool " + poolId);
                            logger.debug("freePool.size = " + freePool.size() + " in pool " + poolId);

                            if (freePool.size() <= maxIdleNum && freePool.size() + busyPool.size() < maxNum) {
                                isStop = true;
                                break;
                            }
                        }
                    }
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    //当连接不足时,创建连接的线程
    public class ConnectionGenerator extends Thread {

        private boolean isStop = true;

        public void startGenerate() {
            isStop = false;
        }

        public void stopGenerate() {
            isStop = true;
        }

        @Override
        public void run() {
            while (true) {
                if (!isStop) {
                    if (freePool.size() >= minIdleNum
                            || (freePool.size() + busyPool.size() >= maxNum)) {
                        isStop = true;
                    } else {
                        while (true) {
                            try {
                                PooledConnection newConnection = factory.createConnection();
                                freePool.offer(newConnection);
                                logger.debug("ConnectionGenerator thread create a connection " + newConnection.getId() + " add to pool " + poolId);
                                logger.debug("freePool.size = " + freePool.size() + " in pool " + poolId);

                                if (freePool.size() >= minIdleNum
                                        || (freePool.size() + busyPool.size() >= maxNum)) {
                                    isStop = true;
                                    break;
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
