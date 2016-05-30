package cn.jing.manager;

import cn.jing.core.connection.factory.DefaultPooledConnectionFactory;
import cn.jing.core.connection.factory.PooledConnectionFactory;
import cn.jing.core.pool.DefaultPool;
import cn.jing.core.pool.jmx.JMXPool;
import cn.jing.core.pool.jmx.JMXPoolMBean;
import cn.jing.core.pool.Pool;
import cn.jing.exception.MaxConnectionException;
import cn.jing.exception.ModuleNotFoundException;
import cn.jing.exception.NoFreeConnectionException;
import cn.jing.jmx.DefaultJMXServer;
import cn.jing.jmx.JMXServer;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * Created by dubby on 16/5/7.
 */
public class DefaultManager implements Manager {
    private List<Properties> propertiesList;
    private Pool defaultPool;
    private LinkedHashMap<String, Pool> poolMap;

    private Map<String, JMXPoolMBean> poolMBeanMap;
    private JMXServer JMXServer;
    private boolean JMXEnable = false;

    public DefaultManager() throws DocumentException {
        //获取配置文件
        String classpath = DefaultManager.class.getClassLoader().getResource("").getPath();

        SAXReader reader = new SAXReader();
        Document document = reader.read(new File(classpath + "/firework.xml"));
        Element root = document.getRootElement();

        //生成Properties
        propertiesList = new ArrayList<Properties>();
        List<Element> elements = root.elements("module");
        List<Element> temp = null;
        for (Element e : elements) {
            Properties p = new Properties();
            temp = e.elements();
            for (Element t : temp) {
                p.setProperty(t.getName(), t.getText());
            }

            propertiesList.add(p);
        }
        //根据Properties生成Factory和Pool
        poolMBeanMap = new HashMap<String, JMXPoolMBean>();
        poolMap = new LinkedHashMap<String, Pool>();
        PooledConnectionFactory factory = null;
        boolean defaultInited = false;
        for (Properties p : propertiesList) {
            factory = new DefaultPooledConnectionFactory(p);
            Pool pool = null;

            if (p.containsKey("jmx") && Boolean.parseBoolean(p.get("jmx").toString())) {
                JMXEnable = true;
                pool = new DefaultPool(p, factory);
                poolMBeanMap.put(p.get("moduleName").toString(), new JMXPool(pool));
            } else {
                pool = new DefaultPool(p, factory);
            }
            poolMap.put(p.get("moduleName").toString(), pool);

            if (!defaultInited) {
                defaultPool = pool;
                defaultInited = true;
            }
        }
        //如果存在一个pool开启了JMX,那么开启JMX服务
        if (JMXEnable) {
            JMXServer = new DefaultJMXServer();
            try {
                JMXServer.expose(poolMBeanMap);
            } catch (MalformedObjectNameException e) {
                e.printStackTrace();
            } catch (NotCompliantMBeanException e) {
                e.printStackTrace();
            } catch (InstanceAlreadyExistsException e) {
                e.printStackTrace();
            } catch (MBeanRegistrationException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Properties> getPropertiesList() {
        return propertiesList;
    }

    public void setPropertiesList(List<Properties> propertiesList) {
        this.propertiesList = propertiesList;
    }

    public Connection getConnection() throws NoFreeConnectionException, MaxConnectionException {
        return defaultPool.getConnection();
    }

    public Connection getConnection(long timeout) throws NoFreeConnectionException, MaxConnectionException {
        return defaultPool.getConnection(timeout);
    }

    public Connection getConnection(String moduleName) throws ModuleNotFoundException, NoFreeConnectionException, MaxConnectionException {
        if (!poolMap.containsKey(moduleName)) {
            throw new ModuleNotFoundException();
        }
        return poolMap.get(moduleName).getConnection();
    }

    public Connection getConnection(String moduleName, long timeout) throws ModuleNotFoundException, NoFreeConnectionException, MaxConnectionException {
        if (!poolMap.containsKey(moduleName)) {
            throw new ModuleNotFoundException();
        }
        return poolMap.get(moduleName).getConnection(timeout);
    }
}
