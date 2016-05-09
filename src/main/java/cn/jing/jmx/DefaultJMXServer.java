package cn.jing.jmx;

import cn.jing.core.pool.jmx.JMXPoolMBean;
import com.sun.jdmk.comm.HtmlAdaptorServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.*;
import java.util.Map;

/**
 * Created by dubby on 16/5/7.
 */
public class DefaultJMXServer implements JMXServer {

    private Logger logger = LoggerFactory.getLogger(DefaultJMXServer.class);

    public void expose(Map<String, JMXPoolMBean> poolMBeanMap) throws MalformedObjectNameException, NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException {
        MBeanServer server = MBeanServerFactory.createMBeanServer();

        for (Map.Entry<String, JMXPoolMBean> entry : poolMBeanMap.entrySet()) {
            ObjectName poolMBeanName = new ObjectName("firework:name=" + entry.getKey());
            server.registerMBean(entry.getValue(), poolMBeanName);
        }

        ObjectName adapterName = new ObjectName("agent:name=htmlAdapter,port=8082");
        HtmlAdaptorServer adaptorServer = new HtmlAdaptorServer();
        server.registerMBean(adaptorServer, adapterName);

        adaptorServer.start();
        logger.debug("jmx server starting ...");
        logger.debug("you can open http://localhost:8082 with browser");
    }
}
