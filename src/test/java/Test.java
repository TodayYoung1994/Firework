import cn.jing.core.connection.factory.DefaultPooledConnectionFactory;
import cn.jing.core.connection.factory.PooledConnectionFactory;
import cn.jing.core.pool.DefaultPool;
import cn.jing.core.pool.Pool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by dubby on 16/5/6.
 */
public class Test {

    @org.junit.Test
    public void test() {
        Properties properties = new Properties();
        properties.setProperty("user", "root");
        properties.setProperty("password", "nington");
        properties.setProperty("driverName", "com.mysql.jdbc.Driver");
        properties.setProperty("url", "jdbc:mysql://127.0.0.1/test");

        PooledConnectionFactory factory = new DefaultPooledConnectionFactory(properties);

        properties.clear();
        properties.setProperty("coreNum", "10");
        properties.setProperty("maxNum", "20");
        properties.setProperty("maxIdleNum", "10");

        Pool pool = new DefaultPool(properties, factory);


        Connection connection = pool.getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from test");
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getString("value"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from test");
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getString("value"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
