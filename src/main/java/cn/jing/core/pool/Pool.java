package cn.jing.core.pool;

import cn.jing.core.connection.PooledConnection;

/**
 * Created by dubby on 16/5/6.
 */
public interface Pool {

    PooledConnection getConnection();

    PooledConnection getConnection(long timeout);

    void returnConnection(PooledConnection connection);
}
