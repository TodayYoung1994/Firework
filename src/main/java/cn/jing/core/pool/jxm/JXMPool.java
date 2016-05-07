package cn.jing.core.pool.jxm;

import cn.jing.core.pool.Pool;

/**
 * Created by dubby on 16/5/3.
 */
public class JXMPool implements JXMPoolMBean {

    private Pool pool;

    public JXMPool(){}

    public JXMPool(Pool pool) {
        this.pool = pool;
    }

    public int getFreeSize() {
        return pool.getFreePool().size();
    }

    public int getBusySize() {
        return pool.getBusyPool().size();
    }

    public int getMax() {
        return pool.getMaxNum();
    }

    public void setMax(int max) {
        pool.setMaxNum(max);
    }

    public int getCore() {
        return pool.getCoreNum();
    }

    public void setCore(int core) {
        pool.setCoreNum(core);
    }

    public int getMaxIdleNum() {
        return pool.getMaxIdleNum();
    }

    public void setMaxIdleNum(int maxIdleNum) {
        pool.setMaxIdleNum(maxIdleNum);
    }

    public long getMaxIdleTime() {
        return pool.getMaxIdleTime();
    }

    public void setMaxIdleTime(long maxIdleTime) {
        pool.setMaxIdleTime(maxIdleTime);
    }
}
