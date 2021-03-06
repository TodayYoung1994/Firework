package cn.jing.core.connection.factory;

import cn.jing.core.connection.DefaultPooledConnection;
import cn.jing.core.connection.PooledConnection;
import cn.jing.exception.PropertyException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.UUID;

/**
 * Created by dubby on 16/5/5.
 */
public class DefaultPooledConnectionFactory extends PooledConnectionFactory {

    private String user;
    private String password;
    private String driverName;
    private String url;


    public DefaultPooledConnectionFactory(Properties properties) {
        if (properties.containsKey("user")
                && properties.containsKey("password")
                && properties.containsKey("driverName")
                && properties.containsKey("url")) {
            user = properties.get("user").toString();
            password = properties.get("password").toString();
            driverName = properties.get("driverName").toString();
            url = properties.get("url").toString();
        } else {
            throw new PropertyException();
        }
        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("jdbc driver cannot found.");
        }
    }


    public PooledConnection createConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(url, user, password);
        PooledConnection pooledConnection = new DefaultPooledConnection(UUID.randomUUID().toString(), connection, pool);
        return pooledConnection;
    }
}
