package cn.jing.core.pool;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Created by dubby on 16/5/3.
 */
public abstract class Pool implements DataSource {



    /**
     * 核心连接数
     */
    private int coreNum;
    /**
     * 最大连接数
     */
    private int maxNum;
    /**
     * 最大空闲连接数
     * 当空闲数量超过此值,将会触发连接回收器
     */
    private int maxIdleNum;
    /**
     * 此属性在连接回收器工作时生效
     * 空闲时间超过此值得连接将会被销毁
     */
    private long maxIdleTime;

    /**
     * 在借出时,测试连接的有效性
     */
    private boolean testOnBorrow;
    /**
     * 在归还时,测试连接的有效性
     */
    private boolean testOnReturn;
    /**
     * 在空闲时,测试连接的有效性
     */
    private boolean testWhileIdle;
    /**
     * 测试有效性时,执行的语句
     * 建议语句执行代价尽量的小,降低测试给数据库带来的压力
     */
    private String testSql;

    /**
     * 当数据库连接池,初始化时,或者失效时,调用此方法
     * 重新创建所有连接
     */
    abstract void reload();
}
