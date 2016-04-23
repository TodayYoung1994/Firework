package cn.jing.manager;

import java.util.Properties;

/**
 * Created by dubby on 16/4/23.
 */
public class DbProperties {
    private String dbName;
    private String url;
    private String driverName;
    private String username;
    private String password;
    private boolean init;

    public DbProperties(Properties properties) {
        if(properties.isEmpty()){
            init = false;
            return;
        }
        dbName = properties.getProperty("dbName");
        url = properties.getProperty("url");
        driverName = properties.getProperty("driverName");
        username = properties.getProperty("username");
        password = properties.getProperty("password");
        init = true;
    }

    public boolean isInit() {
        return init;
    }

    public void setInit(boolean init) {
        this.init = init;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
