package cn.jing.jmx;

import cn.jing.core.pool.jxm.JXMPoolMBean;
import com.sun.jdmk.comm.HtmlAdaptorServer;

import javax.management.*;
import java.util.Map;

/**
 * Created by dubby on 16/5/7.
 */
public class DefaultJXMServer implements JXMServer {

    public void expose(Map<String, JXMPoolMBean> poolMBeanMap) throws MalformedObjectNameException, NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException {
        MBeanServer server = MBeanServerFactory.createMBeanServer();

        for (Map.Entry<String, JXMPoolMBean> entry : poolMBeanMap.entrySet()) {
            ObjectName poolMBeanName = new ObjectName("dubby:name=" + entry.getKey());
            server.registerMBean(entry.getValue(), poolMBeanName);
        }

        ObjectName adapterName = new ObjectName("agent:name=htmlAdapter,port=8082");
        HtmlAdaptorServer adaptorServer = new HtmlAdaptorServer();
        server.registerMBean(adaptorServer, adapterName);

        adaptorServer.start();
        System.out.println("jxm server starting ...");
    }
}
