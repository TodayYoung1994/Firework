package cn.jing.core.connection.factory;

import cn.jing.core.connection.PooledConnection;
import cn.jing.core.pool.Pool;

import java.sql.SQLException;

/**
 * Created by dubby on 16/5/6.
 */
public abstract class PooledConnectionFactory {
    protected Pool pool;
    abstract public PooledConnection createConnection() throws SQLException;

    public Pool getPool() {
        return pool;
    }

    public void setPool(Pool pool) {
        this.pool = pool;
    }
}
