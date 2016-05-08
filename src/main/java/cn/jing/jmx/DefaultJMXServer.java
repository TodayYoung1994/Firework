package cn.jing.jmx;

import cn.jing.core.pool.jmx.JMXPoolMBean;
import com.sun.jdmk.comm.HtmlAdaptorServer;

import javax.management.*;
import java.util.Map;

/**
 * Created by dubby on 16/5/7.
 */
public class DefaultJMXServer implements JMXServer {

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
        System.out.println("jxm server starting ...");
    }
}
