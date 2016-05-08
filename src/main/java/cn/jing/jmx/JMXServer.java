package cn.jing.jmx;

import cn.jing.core.pool.jmx.JMXPoolMBean;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import java.util.Map;

/**
 * Created by dubby on 16/4/22.
 */
public interface JMXServer {
    void expose(Map<String, JMXPoolMBean> poolMBeanMap) throws MalformedObjectNameException, NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException;
}
