package cn.jing.jmx;

import cn.jing.core.pool.jxm.JXMPoolMBean;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import java.util.Map;

/**
 * Created by dubby on 16/4/22.
 */
public interface JXMServer {
    void expose(Map<String, JXMPoolMBean> poolMBeanMap) throws MalformedObjectNameException, NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException;
}
