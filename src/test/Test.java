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


        //这个latch是为了阻止程序结束,这样就可以打开 http://localhost:8082/ 查看jmx
        CountDownLatch latch = new CountDownLatch(1);
        latch.await();
    }
}
