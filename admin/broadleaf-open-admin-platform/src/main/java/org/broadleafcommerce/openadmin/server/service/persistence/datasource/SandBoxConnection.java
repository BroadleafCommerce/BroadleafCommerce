/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.server.service.persistence.datasource;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.springframework.util.Assert;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

public class SandBoxConnection implements Connection {

    private Connection delegate;
    private GenericObjectPool connectionPool;

    public SandBoxConnection(Connection delegate, GenericObjectPool connectionPool) {
        this.delegate = delegate;
        this.connectionPool = connectionPool;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        Assert.notNull(iface, "Interface argument must not be null");
        if (!Connection.class.equals(iface)) {
            throw new SQLException("Connection of type [" + getClass().getName() +
                    "] can only be unwrapped as [java.sql.Connection], not as [" + iface.getName());
        }
        return (T) delegate;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return Connection.class.equals(iface);
    }

    @Override
    public Statement createStatement() throws SQLException {
        return delegate.createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return delegate.prepareStatement(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return delegate.prepareCall(sql);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        return delegate.nativeSQL(sql);
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        delegate.setAutoCommit(autoCommit);
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return delegate.getAutoCommit();
    }

    @Override
    public void commit() throws SQLException {
        delegate.commit();
    }

    @Override
    public void rollback() throws SQLException {
        delegate.rollback();
    }

    @Override
    public void close() throws SQLException {
        try {
            connectionPool.returnObject(this);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public boolean isClosed() throws SQLException {
        return delegate.isClosed();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return delegate.getMetaData();
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        delegate.setReadOnly(readOnly);
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return delegate.isReadOnly();
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        delegate.setCatalog(catalog);
    }

    @Override
    public String getCatalog() throws SQLException {
        return delegate.getCatalog();
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        delegate.setTransactionIsolation(level);
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return delegate.getTransactionIsolation();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return delegate.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        delegate.clearWarnings();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency)
            throws SQLException {
        return delegate.createStatement(resultSetType, resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType,
            int resultSetConcurrency) throws SQLException {
        return delegate.prepareStatement(sql, resultSetType,
                resultSetConcurrency);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType,
            int resultSetConcurrency) throws SQLException {
        return delegate.prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return delegate.getTypeMap();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        delegate.setTypeMap(map);
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        delegate.setHoldability(holdability);
    }

    @Override
    public int getHoldability() throws SQLException {
        return delegate.getHoldability();
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        return delegate.setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        return delegate.setSavepoint(name);
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        delegate.rollback(savepoint);
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        delegate.releaseSavepoint(savepoint);
    }

    @Override
    public Statement createStatement(int resultSetType,
            int resultSetConcurrency, int resultSetHoldability)
            throws SQLException {
        return delegate.createStatement(resultSetType, resultSetConcurrency,
                resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType,
            int resultSetConcurrency, int resultSetHoldability)
            throws SQLException {
        return delegate.prepareStatement(sql, resultSetType,
                resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType,
            int resultSetConcurrency, int resultSetHoldability)
            throws SQLException {
        return delegate.prepareCall(sql, resultSetType, resultSetConcurrency,
                resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
            throws SQLException {
        return delegate.prepareStatement(sql, autoGeneratedKeys);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes)
            throws SQLException {
        return delegate.prepareStatement(sql, columnIndexes);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames)
            throws SQLException {
        return delegate.prepareStatement(sql, columnNames);
    }

    @Override
    public Clob createClob() throws SQLException {
        return delegate.createClob();
    }

    @Override
    public Blob createBlob() throws SQLException {
        return delegate.createBlob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        return delegate.createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return delegate.createSQLXML();
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return delegate.isValid(timeout);
    }

    @Override
    public void setClientInfo(String name, String value)
            throws SQLClientInfoException {
        delegate.setClientInfo(name, value);
    }

    @Override
    public void setClientInfo(Properties properties)
            throws SQLClientInfoException {
        delegate.setClientInfo(properties);
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return delegate.getClientInfo(name);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return delegate.getClientInfo();
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements)
            throws SQLException {
        return delegate.createArrayOf(typeName, elements);
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes)
            throws SQLException {
        return delegate.createStruct(typeName, attributes);
    }

    public void setSchema(String schema) throws SQLException {
        try {
            Class<? extends Connection> delegateClass = delegate.getClass();
            Class partypes[] = new Class[1];
            partypes[0] = String.class;
            Object args[] = new Object[1];
            args[0] = schema;
            Method method;
            method = delegateClass.getMethod("setSchema", partypes);
            method.invoke(delegate, args);
        } catch (SecurityException e) {
            // ignore exceptions
        } catch (NoSuchMethodException e) {
            // ignore exceptions
        } catch (IllegalArgumentException e) {
            // ignore exceptions
        } catch (IllegalAccessException e) {
            // ignore exceptions
        } catch (InvocationTargetException e) {
            // ignore exceptions
        }
    }

    public String getSchema() throws SQLException {
        String returnValue = null;
        try {
            Class<? extends Connection> delegateClass = delegate.getClass();
            Method method = delegateClass.getMethod("getSchema");
            returnValue = method.invoke(delegate).toString();
        } catch (SecurityException e) {
            // ignore exceptions
        } catch (NoSuchMethodException e) {
            // ignore exceptions
        } catch (IllegalArgumentException e) {
            // ignore exceptions
        } catch (IllegalAccessException e) {
            // ignore exceptions
        } catch (InvocationTargetException e) {
            // ignore exceptions
        }
        return returnValue;
    }

    public void abort(Executor executor) throws SQLException {
        try {
            Class<? extends Connection> delegateClass = delegate.getClass();
            Class partypes[] = new Class[1];
            partypes[0] = Executor.class;
            Object args[] = new Object[1];
            args[0] = executor;
            Method method = delegateClass.getMethod("abort", partypes);
            method.invoke(delegate, args);
        } catch (SecurityException e) {
            // ignore exceptions
        } catch (NoSuchMethodException e) {
            // ignore exceptions
        } catch (IllegalArgumentException e) {
            // ignore exceptions
        } catch (IllegalAccessException e) {
            // ignore exceptions
        } catch (InvocationTargetException e) {
            // ignore exceptions
        }
    }

    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        try {
            Class<? extends Connection> delegateClass = delegate.getClass();
            Class partypes[] = new Class[2];
            partypes[0] = Executor.class;
            partypes[1] = int.class;
            Object args[] = new Object[2];
            args[0] = executor;
            args[1] = milliseconds;
            Method method = delegateClass.getMethod("setNetworkTimeout", partypes);
            method.invoke(delegate, args);
        } catch (SecurityException e) {
            // ignore exceptions
        } catch (NoSuchMethodException e) {
            // ignore exceptions
        } catch (IllegalArgumentException e) {
            // ignore exceptions
        } catch (IllegalAccessException e) {
            // ignore exceptions
        } catch (InvocationTargetException e) {
            // ignore exceptions
        }

    }

    public int getNetworkTimeout() throws SQLException {
        int returnValue = 0;
        try {
            Class<? extends Connection> delegateClass = delegate.getClass();
            Method method = delegateClass.getMethod("getNetworkTimeout");
            returnValue = Integer.parseInt(method.invoke(delegate).toString());
        } catch (SecurityException e) {
            // ignore exceptions
        } catch (NoSuchMethodException e) {
            // ignore exceptions
        } catch (IllegalArgumentException e) {
            // ignore exceptions
        } catch (IllegalAccessException e) {
            // ignore exceptions
        } catch (InvocationTargetException e) {
            // ignore exceptions
        }
        return returnValue;
    }
}
