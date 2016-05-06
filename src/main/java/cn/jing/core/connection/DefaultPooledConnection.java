package cn.jing.core.connection;

import cn.jing.core.pool.Pool;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * Created by dubby on 16/5/3.
 */
public class DefaultPooledConnection extends PooledConnection implements Connection {

    public DefaultPooledConnection(String id, Connection connection, Pool pool) {
        super(id, connection, pool);
    }

    public Statement createStatement() throws SQLException {
        return getConnection().createStatement();
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return getConnection().prepareStatement(sql);
    }

    public CallableStatement prepareCall(String sql) throws SQLException {
        return getConnection().prepareCall(sql);
    }

    public String nativeSQL(String sql) throws SQLException {
        return getConnection().nativeSQL(sql);
    }

    public void setAutoCommit(boolean autoCommit) throws SQLException {
        getConnection().setAutoCommit(autoCommit);
    }

    public boolean getAutoCommit() throws SQLException {
        return false;
    }

    public void commit() throws SQLException {
        getConnection().commit();
    }

    public void rollback() throws SQLException {
        getConnection().rollback();
    }

    /**
     * 不能真的关闭连接,不然连接池就没意义了
     * 所以,先把connection的所有属性重置
     * 再从busyPoll中移除,加入到freePool中
     *
     * @throws SQLException
     */
    public void close() throws SQLException {
        getConnection().clearWarnings();
        getConnection().setAutoCommit(false);
        getConnection().setReadOnly(false);
        doReturn();
        pool.returnConnection(this);
    }

    public boolean isClosed() throws SQLException {
        return getConnection().isClosed();
    }

    public DatabaseMetaData getMetaData() throws SQLException {
        return getConnection().getMetaData();
    }

    public void setReadOnly(boolean readOnly) throws SQLException {
        getConnection().setReadOnly(readOnly);
    }

    public boolean isReadOnly() throws SQLException {
        return getConnection().isReadOnly();
    }

    public void setCatalog(String catalog) throws SQLException {
        getConnection().setCatalog(catalog);
    }

    public String getCatalog() throws SQLException {
        return getConnection().getCatalog();
    }

    public void setTransactionIsolation(int level) throws SQLException {
        getConnection().setTransactionIsolation(level);
    }

    public int getTransactionIsolation() throws SQLException {
        return getConnection().getTransactionIsolation();
    }

    public SQLWarning getWarnings() throws SQLException {
        return getConnection().getWarnings();
    }

    public void clearWarnings() throws SQLException {
        getConnection().clearWarnings();
    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return getConnection().createStatement(resultSetType, resultSetConcurrency);
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return getConnection().prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return getConnection().prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return getConnection().getTypeMap();
    }

    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        getConnection().setTypeMap(map);
    }

    public void setHoldability(int holdability) throws SQLException {
        getConnection().setHoldability(holdability);
    }

    public int getHoldability() throws SQLException {
        return getConnection().getHoldability();
    }

    public Savepoint setSavepoint() throws SQLException {
        return getConnection().setSavepoint();
    }

    public Savepoint setSavepoint(String name) throws SQLException {
        return getConnection().setSavepoint(name);
    }

    public void rollback(Savepoint savepoint) throws SQLException {
        getConnection().rollback(savepoint);
    }

    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        getConnection().releaseSavepoint(savepoint);
    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return getConnection().createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return getConnection().prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return getConnection().prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return getConnection().prepareStatement(sql, autoGeneratedKeys);
    }

    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return getConnection().prepareStatement(sql, columnIndexes);
    }

    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return getConnection().prepareStatement(sql, columnNames);
    }

    public Clob createClob() throws SQLException {
        return getConnection().createClob();
    }

    public Blob createBlob() throws SQLException {
        return getConnection().createBlob();
    }

    public NClob createNClob() throws SQLException {
        return getConnection().createNClob();
    }

    public SQLXML createSQLXML() throws SQLException {
        return getConnection().createSQLXML();
    }

    public boolean isValid(int timeout) throws SQLException {
        return getConnection().isValid(timeout);
    }

    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        getConnection().setClientInfo(name, value);
    }

    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        getConnection().setClientInfo(properties);
    }

    public String getClientInfo(String name) throws SQLException {
        return getConnection().getClientInfo(name);
    }

    public Properties getClientInfo() throws SQLException {
        return getConnection().getClientInfo();
    }

    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return getConnection().createArrayOf(typeName, elements);
    }

    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return getConnection().createStruct(typeName, attributes);
    }

    public void setSchema(String schema) throws SQLException {
        getConnection().setSchema(schema);
    }

    public String getSchema() throws SQLException {
        return getConnection().getSchema();
    }

    public void abort(Executor executor) throws SQLException {
        getConnection().abort(executor);
    }

    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        getConnection().setNetworkTimeout(executor, milliseconds);
    }

    public int getNetworkTimeout() throws SQLException {
        return getConnection().getNetworkTimeout();
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return getConnection().unwrap(iface);
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return getConnection().isWrapperFor(iface);
    }


    /**
     * 这个方法用来检测连接是否失效
     *
     * @param testSql
     * @return
     */
    public boolean isActive(String testSql) {
        try {
            if (connectionHolder == null || connectionHolder.isClosed())
                return false;
            PreparedStatement stmt = connectionHolder.prepareCall(testSql);
            stmt.executeQuery();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
