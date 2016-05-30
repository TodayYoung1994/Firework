package cn.jing.core.pool.jmx;

import cn.jing.core.pool.DefaultPool;
import cn.jing.core.pool.Pool;

/**
 * Created by dubby on 16/5/3.
 */
public class JMXPool implements JMXPoolMBean {

    private Pool pool;

    public JMXPool() {
    }

    private void startWorkerThread(){
        ((DefaultPool)pool).connectionGC.startGC();
        ((DefaultPool)pool).connectionGenerator.startGenerate();
    }

    public JMXPool(Pool pool) {
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
        startWorkerThread();
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
        startWorkerThread();
        pool.setMaxIdleNum(maxIdleNum);
    }

    public int getMinIdleNum() {
        return pool.getMinIdleNum();
    }

    public void setMinIdleNum(int minIdleNum) {
        startWorkerThread();
        pool.setMinIdleNum(minIdleNum);
    }
}
