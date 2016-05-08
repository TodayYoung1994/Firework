package cn.jing.core.pool.jmx;

/**
 * Created by dubby on 16/5/7.
 */
public interface JMXPoolMBean {
    /**
     * 空闲状态的连接
     *
     * @return
     */
    int getFreeSize();

    /**
     * 已被分配的连接
     *
     * @return
     */
    int getBusySize();

    /**
     * 连接池最大连接数
     *
     * @return
     */
    int getMax();

    void setMax(int max);

    /**
     * 连接池最小连接数
     *
     * @return
     */
    int getCore();

    void setCore(int core);

    /**
     * 连接池最大空闲连接数
     *
     * @return
     */
    int getMaxIdleNum();

    void setMaxIdleNum(int maxIdleNum);

    /**
     * 连接池最大空闲时间
     *
     * @return
     */
    long getMaxIdleTime();

    void setMaxIdleTime(long maxIdleTime);
}
