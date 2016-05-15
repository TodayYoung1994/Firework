package cn.jing.core.pool;

import cn.jing.core.connection.PooledConnection;
import cn.jing.core.connection.factory.PooledConnectionFactory;
import cn.jing.exception.NoFreeConnectionException;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dubby on 16/5/6.
 */
public abstract class Pool {

    protected PooledConnectionFactory factory;
    protected BlockingQueue<PooledConnection> freePool;
    /**
     * 用map是因为要根据id来判断那个需要被回收
     */
    protected ConcurrentHashMap<String, PooledConnection> busyPool;


    protected int currentNum = 10;
    /**
     * 核心连接数
     */
    protected int coreNum = 10;
    /**
     * 最大连接数
     */
    protected int maxNum = 20;
    /**
     * 最大空闲连接数
     * 当空闲数量超过此值,将会触发连接回收器
     */
    protected int maxIdleNum = 10;

    protected int minIdleNum = 10;
    /**
     * 此属性在连接回收器工作时生效
     * 空闲时间超过此值得连接将会被销毁
     * 单位:ms
     */
    protected long maxIdleTime = 1000;

    /**
     * 在借出时,测试连接的有效性
     */
    protected boolean testOnBorrow = false;
    /**
     * 在归还时,测试连接的有效性
     */
    protected boolean testOnReturn = true;

    /**
     * 测试有效性时,执行的语句
     * 建议语句执行代价尽量的小,降低测试给数据库带来的压力
     */
    protected String testSql = "select 1";

    abstract public PooledConnection getConnection() throws NoFreeConnectionException;

    abstract public PooledConnection getConnection(long timeout) throws NoFreeConnectionException;

    abstract public void returnConnection(PooledConnection connection);

    public BlockingQueue<PooledConnection> getFreePool() {
        return freePool;
    }

    public void setFreePool(BlockingQueue<PooledConnection> freePool) {
        this.freePool = freePool;
    }

    public ConcurrentHashMap<String, PooledConnection> getBusyPool() {
        return busyPool;
    }

    public void setBusyPool(ConcurrentHashMap<String, PooledConnection> busyPool) {
        this.busyPool = busyPool;
    }

    public int getCurrentNum() {
        return currentNum;
    }

    public void setCurrentNum(int currentNum) {
        this.currentNum = currentNum;
    }

    public int getCoreNum() {
        return coreNum;
    }

    public void setCoreNum(int coreNum) {
        this.coreNum = coreNum;
    }

    public int getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(int maxNum) {
        this.maxNum = maxNum;
    }

    public int getMaxIdleNum() {
        return maxIdleNum;
    }

    public void setMaxIdleNum(int maxIdleNum) {
        this.maxIdleNum = maxIdleNum;
    }

    public long getMaxIdleTime() {
        return maxIdleTime;
    }

    public void setMaxIdleTime(long maxIdleTime) {
        this.maxIdleTime = maxIdleTime;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public boolean isTestOnReturn() {
        return testOnReturn;
    }

    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public String getTestSql() {
        return testSql;
    }

    public void setTestSql(String testSql) {
        this.testSql = testSql;
    }

    public int getMinIdleNum() {
        return minIdleNum;
    }

    public void setMinIdleNum(int minIdleNum) {
        this.minIdleNum = minIdleNum;
    }
}
