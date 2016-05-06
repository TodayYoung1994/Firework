package cn.jing.core.pool;

import cn.jing.core.connection.PooledConnection;
import cn.jing.core.connection.PooledConnectionFactory;
import cn.jing.exception.PropertyException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.*;

/**
 * Created by dubby on 16/5/3.
 */
public class DefaultPool implements Pool {

    private PooledConnectionFactory factory;
    private BlockingQueue<PooledConnection> freePool;
    /**
     * 用map是因为要根据id来判断那个需要被回收
     */
    private ConcurrentHashMap<String, PooledConnection> busyPool;

    public DefaultPool(Properties properties) throws SQLException {
        if (properties == null || properties.isEmpty()) {
            throw new PropertyException();
        }
        //init properties
        if (properties.containsKey("coreNum")) {
            coreNum = (Integer) properties.get("coreNum");
        }
        if (properties.containsKey("maxNum")) {
            maxNum = (Integer) properties.get("maxNum");
        }
        if (properties.containsKey("maxIdleNum")) {
            maxIdleNum = (Integer) properties.get("maxIdleNum");
        }
        if (properties.containsKey("maxIdleTime")) {
            maxIdleTime = (Long) properties.get("maxIdleTime");
        }
        if (properties.containsKey("testOnBorrow")) {
            testOnBorrow = (Boolean) properties.get("testOnBorrow");
        }
        if (properties.containsKey("testOnReturn")) {
            testOnReturn = (Boolean) properties.get("testOnReturn");
        }
        if (properties.containsKey("testWhileIdle")) {
            testWhileIdle = (Boolean) properties.get("testWhileIdle");
        }
        if (properties.containsKey("testSql")) {
            testSql = (String) properties.get("testSql");
        }
        currentNum = coreNum;
        //init pool
        freePool = new LinkedBlockingQueue<PooledConnection>(maxNum);
        busyPool = new ConcurrentHashMap<String, PooledConnection>(maxIdleNum);

        factory.setPool(this);

        //fill the free pool
        reset();
    }

    public void init(PooledConnectionFactory factory) {
        this.factory = factory;
    }


    private int currentNum = 10;
    /**
     * 核心连接数
     */
    private int coreNum = 10;
    /**
     * 最大连接数
     */
    private int maxNum = 20;
    /**
     * 最大空闲连接数
     * 当空闲数量超过此值,将会触发连接回收器
     */
    private int maxIdleNum = 10;
    /**
     * 此属性在连接回收器工作时生效
     * 空闲时间超过此值得连接将会被销毁
     * 单位:ms
     */
    private long maxIdleTime = 1000;

    /**
     * 在借出时,测试连接的有效性
     */
    private boolean testOnBorrow = false;
    /**
     * 在归还时,测试连接的有效性
     */
    private boolean testOnReturn = true;
    /**
     * 在空闲时,测试连接的有效性
     */
    private boolean testWhileIdle = false;
    /**
     * 测试有效性时,执行的语句
     * 建议语句执行代价尽量的小,降低测试给数据库带来的压力
     */
    private String testSql = "select 1";

    /**
     * 当数据库连接池,初始化时,或者失效时,调用此方法
     * 重新创建所有连接
     */
    synchronized void reset() throws SQLException {
        freePool.clear();
        for (int i = 0; i < currentNum; ++i) {
            this.freePool.offer(factory.createConnection());
        }
    }

    public PooledConnection getConnection() throws InterruptedException {
        return getConnection(100);
    }

    public PooledConnection getConnection(long timeout) throws InterruptedException {
        PooledConnection connection = freePool.poll(timeout, TimeUnit.MILLISECONDS);
        busyPool.put(connection.getId(), connection);
        return connection;
    }

    public void returnConnection(PooledConnection connection) {
        busyPool.remove(connection.getId());
        freePool.offer(connection);
    }
}
