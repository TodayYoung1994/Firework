import cn.jing.exception.NoFreeConnectionException;
import cn.jing.manager.DefaultManager;
import org.dom4j.DocumentException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

/**
 * Created by dubby on 16/5/9.
 */
public class Test {
    @org.junit.Test
    public void test() throws DocumentException, NoFreeConnectionException, SQLException, InterruptedException {
        DefaultManager manager = new DefaultManager();
        List<Properties> list = manager.getPropertiesList();
        for (Properties p : list) {
            System.out.println(p.toString());
        }

        Connection c1 = manager.getConnection();
        Connection c2 = manager.getConnection();
        Connection c3 = manager.getConnection();


        Thread.sleep(1000*30);

        Connection a1 = manager.getConnection();
        Connection a2 = manager.getConnection();
        Connection a3 = manager.getConnection();
        Connection a4 = manager.getConnection();
        Connection a5 = manager.getConnection();
        Connection a6 = manager.getConnection();
        Connection a7 = manager.getConnection();
        Connection a8 = manager.getConnection();
        Connection a9 = manager.getConnection();

        //这个latch是为了阻止程序结束,这样就可以打开 http://localhost:8082/ 查看jmx
        CountDownLatch latch = new CountDownLatch(1);
        latch.await();
    }
}
