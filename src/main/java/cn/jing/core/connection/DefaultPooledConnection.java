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
        return this.connection.createStatement();
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return this.connection.prepareStatement(sql);
    }

    public CallableStatement prepareCall(String sql) throws SQLException {
        return this.connection.prepareCall(sql);
    }

    public String nativeSQL(String sql) throws SQLException {
        return this.connection.nativeSQL(sql);
    }

    public void setAutoCommit(boolean autoCommit) throws SQLException {
        this.connection.setAutoCommit(autoCommit);
    }

    public boolean getAutoCommit() throws SQLException {
        return false;
    }

    public void commit() throws SQLException {
        this.connection.commit();
    }

    public void rollback() throws SQLException {
        this.connection.rollback();
    }

    /**
     * 不能真的关闭连接,不然连接池就没意义了
     * 所以,先把connection的所有属性重置
     * 再从busyPoll中移除,加入到freePool中
     *
     * @throws SQLException
     */
    public void close() throws SQLException {
        this.connection.clearWarnings();
        this.connection.setAutoCommit(false);
        this.connection.setReadOnly(false);

        pool.returnConnection(this);
    }

    public boolean isClosed() throws SQLException {
        return this.connection.isClosed();
    }

    public DatabaseMetaData getMetaData() throws SQLException {
        return this.connection.getMetaData();
    }

    public void setReadOnly(boolean readOnly) throws SQLException {
        this.connection.setReadOnly(readOnly);
    }

    public boolean isReadOnly() throws SQLException {
        return this.connection.isReadOnly();
    }

    public void setCatalog(String catalog) throws SQLException {
        this.connection.setCatalog(catalog);
    }

    public String getCatalog() throws SQLException {
        return this.connection.getCatalog();
    }

    public void setTransactionIsolation(int level) throws SQLException {
        this.connection.setTransactionIsolation(level);
    }

    public int getTransactionIsolation() throws SQLException {
        return this.connection.getTransactionIsolation();
    }

    public SQLWarning getWarnings() throws SQLException {
        return this.connection.getWarnings();
    }

    public void clearWarnings() throws SQLException {
        this.connection.clearWarnings();
    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return this.connection.createStatement(resultSetType, resultSetConcurrency);
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return this.connection.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return this.connection.prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return this.connection.getTypeMap();
    }

    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        this.connection.setTypeMap(map);
    }

    public void setHoldability(int holdability) throws SQLException {
        this.connection.setHoldability(holdability);
    }

    public int getHoldability() throws SQLException {
        return this.connection.getHoldability();
    }

    public Savepoint setSavepoint() throws SQLException {
        return this.connection.setSavepoint();
    }

    public Savepoint setSavepoint(String name) throws SQLException {
        return this.connection.setSavepoint(name);
    }

    public void rollback(Savepoint savepoint) throws SQLException {
        this.connection.rollback(savepoint);
    }

    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        this.connection.releaseSavepoint(savepoint);
    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return this.connection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return this.connection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return this.connection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return this.connection.prepareStatement(sql, autoGeneratedKeys);
    }

    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return this.connection.prepareStatement(sql, columnIndexes);
    }

    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return this.connection.prepareStatement(sql, columnNames);
    }

    public Clob createClob() throws SQLException {
        return this.connection.createClob();
    }

    public Blob createBlob() throws SQLException {
        return this.connection.createBlob();
    }

    public NClob createNClob() throws SQLException {
        return this.connection.createNClob();
    }

    public SQLXML createSQLXML() throws SQLException {
        return this.connection.createSQLXML();
    }

    public boolean isValid(int timeout) throws SQLException {
        return this.connection.isValid(timeout);
    }

    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        this.connection.setClientInfo(name, value);
    }

    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        this.connection.setClientInfo(properties);
    }

    public String getClientInfo(String name) throws SQLException {
        return this.connection.getClientInfo(name);
    }

    public Properties getClientInfo() throws SQLException {
        return this.connection.getClientInfo();
    }

    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return this.connection.createArrayOf(typeName, elements);
    }

    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return this.connection.createStruct(typeName, attributes);
    }

    public void setSchema(String schema) throws SQLException {
        this.connection.setSchema(schema);
    }

    public String getSchema() throws SQLException {
        return this.connection.getSchema();
    }

    public void abort(Executor executor) throws SQLException {
        this.connection.abort(executor);
    }

    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        this.connection.setNetworkTimeout(executor, milliseconds);
    }

    public int getNetworkTimeout() throws SQLException {
        return this.connection.getNetworkTimeout();
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return this.connection.unwrap(iface);
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return this.connection.isWrapperFor(iface);
    }
}
