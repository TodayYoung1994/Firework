package cn.jing.manager;

import cn.jing.core.connection.factory.DefaultPooledConnectionFactory;
import cn.jing.core.connection.factory.PooledConnectionFactory;
import cn.jing.core.pool.DefaultPool;
import cn.jing.core.pool.jmx.JMXPool;
import cn.jing.core.pool.jmx.JMXPoolMBean;
import cn.jing.core.pool.Pool;
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
    private JMXServer jxmServer;
    private boolean JXMEnable = false;

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
            if (!defaultInited) {
                factory = new DefaultPooledConnectionFactory(p);

                if (p.containsKey("jmx") && Boolean.parseBoolean(p.get("jmx").toString())) {
                    JXMEnable = true;
                    defaultPool = new DefaultPool(p, factory);
                    poolMBeanMap.put(p.get("moduleName").toString(), new JMXPool(defaultPool));
                } else {
                    defaultPool = new DefaultPool(p, factory);
                }

                defaultInited = true;
                poolMap.put(p.get("moduleName").toString(), defaultPool);
            } else {
                factory = new DefaultPooledConnectionFactory(p);
                Pool pool = null;

                if (p.containsKey("jmx") && Boolean.parseBoolean(p.get("jmx").toString())) {
                    JXMEnable = true;
                    pool = new DefaultPool(p, factory);
                    poolMBeanMap.put(p.get("moduleName").toString(), new JMXPool(pool));
                } else {
                    pool = new DefaultPool(p, factory);
                }

                poolMap.put(p.get("moduleName").toString(), pool);
            }
        }
        //如果存在一个pool开启了JXM,那么开启JXM服务
        if (JXMEnable) {
            jxmServer = new DefaultJMXServer();
            try {
                jxmServer.expose(poolMBeanMap);
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

    public Connection getConnection() throws NoFreeConnectionException {
        return defaultPool.getConnection();
    }

    public Connection getConnection(long timeout) throws NoFreeConnectionException {
        return defaultPool.getConnection(timeout);
    }

    public Connection getConnection(String moduleName) throws ModuleNotFoundException, NoFreeConnectionException {
        if (!poolMap.containsKey(moduleName)) {
            throw new ModuleNotFoundException();
        }
        return poolMap.get(moduleName).getConnection();
    }

    public Connection getConnection(String moduleName, long timeout) throws ModuleNotFoundException, NoFreeConnectionException {
        if (!poolMap.containsKey(moduleName)) {
            throw new ModuleNotFoundException();
        }
        return poolMap.get(moduleName).getConnection(timeout);
    }

//==================Test=======================

    public static void main(String[] args) throws DocumentException, SQLException, ModuleNotFoundException, NoFreeConnectionException, InterruptedException {
        DefaultManager manager = new DefaultManager();
        List<Properties> list = manager.getPropertiesList();
        for (Properties p : list) {
            System.out.println(p.toString());
        }

        Connection c1 = manager.getConnection();
        Connection c2 = manager.getConnection();
        Connection c3 = manager.getConnection();
        Connection c4 = manager.getConnection();
        Connection c5 = manager.getConnection();
        Connection c6 = manager.getConnection();
        Connection c7 = manager.getConnection();
        Connection c8 = manager.getConnection();
        Connection c9 = manager.getConnection();
        Connection c10 = manager.getConnection();
        Connection c11 = manager.getConnection();
        Connection c12 = manager.getConnection();
        Connection c13 = manager.getConnection();
        Connection c14 = manager.getConnection();
        Connection c15 = manager.getConnection();
        Connection c16 = manager.getConnection();
        Connection c17 = manager.getConnection();
        Connection c18 = manager.getConnection();
        Connection c19 = manager.getConnection();
        Connection c20 = manager.getConnection();

        c1.close();

        Thread.sleep(1000 * 30);
        System.out.println("=========");
        c2.close();
        c3.close();
        c4.close();
        c5.close();
        c6.close();
        c7.close();
        c8.close();
        c9.close();
        c10.close();
        c11.close();
        c12.close();
        c13.close();
        c14.close();
        c15.close();
        c16.close();
        c17.close();
        c18.close();
        c19.close();
        c20.close();


        CountDownLatch latch = new CountDownLatch(1);
        latch.await();


    }
}
