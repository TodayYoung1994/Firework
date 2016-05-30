package cn.jing.core.connection;

import cn.jing.core.pool.Pool;

import java.sql.Connection;

/**
 * Created by dubby on 16/5/6.
 */
public abstract class PooledConnection implements Connection {
    /**
     * 记录借出的时间,用来判断是否回收
     */
    private long borrowTime;
    /**
     * 此ID用来追踪连接的生命周期,
     * 在回收连接时,也依据这个为标准
     */
    private String id;
    private ThreadLocal<Connection> connection;
    protected Pool pool;
    protected Connection connectionHolder;

    public PooledConnection(String id, Connection conn, Pool pool) {
        this.id = id;
        this.connection = new ThreadLocal<Connection>();
        this.connectionHolder = conn;
        this.pool = pool;
    }


    public long getBorrowTime() {
        return borrowTime;
    }

    public void setBorrowTime(long borrowTime) {
        this.borrowTime = borrowTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Connection getConnection() {
        return connection.get();
    }

    public void doBorrow() {
        connection.set(connectionHolder);
    }

    public void doReturn() {
        connection.set(null);
    }

    public Pool getPool() {
        return pool;
    }

    public void setPool(Pool pool) {
        this.pool = pool;
    }

    abstract public boolean isActive(String testSql);
}
