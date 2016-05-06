package cn.jing.core.pool;

import cn.jing.core.connection.PooledConnection;

/**
 * Created by dubby on 16/5/6.
 */
public interface Pool {

    PooledConnection getConnection() throws InterruptedException;

    PooledConnection getConnection(long timeout) throws InterruptedException;

    void returnConnection(PooledConnection connection);
}
