package cn.jing.core.pool;

import cn.jing.core.connection.PooledConnection;
import cn.jing.core.connection.factory.PooledConnectionFactory;
import cn.jing.exception.NoFreeConnectionException;
import cn.jing.exception.PropertyException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.*;

/**
 * Created by dubby on 16/5/3.
 */
public class DefaultPool extends Pool {

    private ConnectionGC connectionGC;

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
        if (properties.containsKey("testWhileIdle")) {
            testWhileIdle = Boolean.parseBoolean(properties.get("testWhileIdle").toString());
        }
        if (properties.containsKey("testSql")) {
            testSql = properties.get("testSql").toString();
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
    }


    /**
     * 当数据库连接池,初始化时,或者失效时,调用此方法
     * 重新创建所有连接
     */
    synchronized void reset() {
        try {
            freePool.clear();
            for (int i = 0; i < currentNum; ++i) {
                this.freePool.offer(factory.createConnection());
            }
        } catch (SQLException e) {
            throw new RuntimeException("error occurs while creating connection");
        }
    }

    public PooledConnection getConnection() throws NoFreeConnectionException {
        PooledConnection connection = getConnection(1000 * 60);
        connection.doBorrow();
        return connection;
    }

    public PooledConnection getConnection(long timeout) throws NoFreeConnectionException {
        try {
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

    }


    class ConnectionGC extends Thread {

        int i = 0;

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
                    if (freePool.size() < maxIdleNum) {
                        isStop = true;
                    } else {
                        for (PooledConnection c : freePool) {
                            long currentTime = System.currentTimeMillis();
                            if (currentTime - c.getBorrowTime() > maxIdleTime) {

                                freePool.poll();

                                System.out.println("回收一条连接" + i);
                                System.out.println("freePool.size() " + freePool.size());
                                System.out.println("maxIdleNum " + maxIdleNum);
                                i++;

                                if (freePool.size() < maxIdleNum) {
                                    isStop = true;
                                    break;
                                }

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
