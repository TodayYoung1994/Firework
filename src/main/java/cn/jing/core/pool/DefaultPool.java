package cn.jing.core.pool;

import cn.jing.core.connection.PooledConnection;
import cn.jing.core.connection.factory.PooledConnectionFactory;
import cn.jing.exception.NoFreeConnectionException;
import cn.jing.exception.PropertyException;

import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.*;

/**
 * Created by dubby on 16/5/3.
 */
public class DefaultPool extends Pool {


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
        PooledConnection connection = getConnection(100);
        connection.doBorrow();
        return connection;
    }

    public PooledConnection getConnection(long timeout) throws NoFreeConnectionException {
        try {
            PooledConnection connection = freePool.poll(timeout, TimeUnit.MILLISECONDS);
            if (connection == null) {
                throw new NoFreeConnectionException();
            }
            busyPool.put(connection.getId(), connection);
            return connection;
        } catch (InterruptedException e) {
            throw new NoFreeConnectionException();
        }
    }

    public void returnConnection(PooledConnection connection) {
        busyPool.remove(connection.getId());
        if (!connection.isActive(testSql)) {
            //重新初始化这个连接
            try {
                connection = factory.createConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        freePool.offer(connection);
    }





}
