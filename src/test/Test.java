import cn.jing.exception.MaxConnectionException;
import cn.jing.exception.ModuleNotFoundException;
import cn.jing.exception.NoFreeConnectionException;
import cn.jing.manager.DefaultManager;
import org.dom4j.DocumentException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

/**
 * Created by dubby on 16/5/9.
 */
public class Test {
    @org.junit.Test
    public void testGcAndGenerate() throws DocumentException, NoFreeConnectionException, SQLException, InterruptedException, MaxConnectionException {
        DefaultManager manager = new DefaultManager();
        List<Properties> list = manager.getPropertiesList();
        for (Properties p : list) {
            System.out.println(p.toString());
        }

        Connection c1 = manager.getConnection();
        Connection c2 = manager.getConnection();
        Connection c3 = manager.getConnection();


        Thread.sleep(1000 * 30);

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
        //因为一旦程序结束,数据库连接池就会被销毁,jmx server也会关闭
        CountDownLatch latch = new CountDownLatch(1);
        latch.await();
    }

    @org.junit.Test
    public void testConnection() throws DocumentException, NoFreeConnectionException, ModuleNotFoundException, SQLException, InterruptedException, MaxConnectionException {
        DefaultManager manager = new DefaultManager();
        //测试    module a
        Connection a1 = manager.getConnection();
        PreparedStatement p1 = a1.prepareStatement("select * from test");
        ResultSet rs1 = p1.executeQuery();
        while (rs1.next()) {
            System.out.println(rs1.getString("value"));
        }

        System.out.println("=====================");
        //测试    module b
        Connection b1 = manager.getConnection("b");
        PreparedStatement p2 = b1.prepareStatement("select * from test");
        ResultSet rs2 = p1.executeQuery();
        while (rs2.next()) {
            System.out.println(rs2.getString("value"));
        }

        Thread.sleep(1000);
        a1.close();
        b1.close();

        //这个latch是为了阻止程序结束,这样就可以打开 http://localhost:8082/ 查看jmx
        CountDownLatch latch = new CountDownLatch(1);
        latch.await();
    }
}
