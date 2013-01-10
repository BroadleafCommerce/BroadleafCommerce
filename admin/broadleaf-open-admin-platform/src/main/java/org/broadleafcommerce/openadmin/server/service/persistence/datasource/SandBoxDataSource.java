/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.server.service.persistence.datasource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.hsqldb.Server;
import org.hsqldb.persist.HsqlProperties;
import org.hsqldb.server.ServerAcl.AclFormatException;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;

public class SandBoxDataSource implements DataSource {
    
    private static final Log LOG = LogFactory.getLog(SandBoxDataSource.class);
    
    public static final String DRIVERNAME = "org.hsqldb.jdbcDriver";
    public static final int DEFAULTPORT = 40025;
    public static final String DEFAULTADDRESS = "localhost";
    public static Server server;
    
    protected PrintWriter logWriter;
    protected int loginTimeout = 5;
    protected GenericObjectPool sandboxDataBasePool;
    protected int port = DEFAULTPORT;
    protected String address = DEFAULTADDRESS;
    protected String uuid;
    
    public SandBoxDataSource() {
        synchronized (this) {
            if (server == null) {
                try {
                    Class.forName(DRIVERNAME);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                try {
                    HsqlProperties p = new HsqlProperties();
                    p.setProperty("server.remote_open",true);
                    server = new Server();
                    server.setAddress(address);
                    server.setPort(port);
                    server.setProperties(p);
                    server.setLogWriter(logWriter==null?new PrintWriter(System.out):logWriter);
                    server.setErrWriter(logWriter==null?new PrintWriter(System.out):logWriter);
                    server.start();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (AclFormatException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        uuid = UUID.randomUUID().toString();
        sandboxDataBasePool = new GenericObjectPool(new PoolableSandBoxDataBaseFactory());
    }
    
    public void close() {
        try {
            sandboxDataBasePool.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    
    //GenericKeyedObjectPool methods

    public void returnObject(Object obj) throws Exception {
        sandboxDataBasePool.returnObject(obj);
    }

    public int getMaxActive() {
        return sandboxDataBasePool.getMaxActive();
    }

    public void setMaxActive(int maxActive) {
        sandboxDataBasePool.setMaxActive(maxActive);
    }

    public byte getWhenExhaustedAction() {
        return sandboxDataBasePool.getWhenExhaustedAction();
    }

    public void setWhenExhaustedAction(byte whenExhaustedAction) {
        sandboxDataBasePool.setWhenExhaustedAction(whenExhaustedAction);
    }

    public long getMaxWait() {
        return sandboxDataBasePool.getMaxWait();
    }

    public void setMaxWait(long maxWait) {
        sandboxDataBasePool.setMaxWait(maxWait);
    }

    public int getMaxIdle() {
        return sandboxDataBasePool.getMaxIdle();
    }

    public void setMaxIdle(int maxIdle) {
        sandboxDataBasePool.setMaxIdle(maxIdle);
    }

    public void setMinIdle(int poolSize) {
        sandboxDataBasePool.setMinIdle(poolSize);
    }

    public int getMinIdle() {
        return sandboxDataBasePool.getMinIdle();
    }

    public long getTimeBetweenEvictionRunsMillis() {
        return sandboxDataBasePool.getTimeBetweenEvictionRunsMillis();
    }

    public void setTimeBetweenEvictionRunsMillis(
            long timeBetweenEvictionRunsMillis) {
        sandboxDataBasePool
                .setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
    }

    public long getMinEvictableIdleTimeMillis() {
        return sandboxDataBasePool.getMinEvictableIdleTimeMillis();
    }

    public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
        sandboxDataBasePool
                .setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
    }
    
    //DataSource methods

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return logWriter;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        this.logWriter = out;
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        this.loginTimeout = seconds;
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return loginTimeout;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public Connection getConnection() throws SQLException {
        try {
            return (Connection) sandboxDataBasePool.borrowObject();
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        throw new SQLException("Not Supported");
    }

    private class PoolableSandBoxDataBaseFactory implements PoolableObjectFactory {

        @Override
        public Object makeObject() throws Exception {
            String jdbcUrl = getJDBCUrl();
            Connection connection = DriverManager.getConnection(jdbcUrl, "SA", "");
            SandBoxConnection blcConnection = new SandBoxConnection(connection, sandboxDataBasePool);
            
            LOG.info("Opening sandbox connection at: " + jdbcUrl);
            
            return blcConnection;
        }

        @Override
        public void destroyObject(Object obj) throws Exception {
            Connection c = (Connection) obj;
            try {
                c.prepareStatement("SHUTDOWN").execute();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                LOG.info("Closing sandbox database at: " + getJDBCUrl());
            }
        }

        @Override
        public boolean validateObject(Object obj) {
            //TODO add a generic connection validation
            return true;
        }

        @Override
        public void activateObject(Object obj) throws Exception {
            //do nothing
        }

        @Override
        public void passivateObject(Object obj) throws Exception {
            //do nothing
        }
        
    }

    public String getJDBCUrl() {
        String jdbcUrl = "jdbc:hsqldb:hsql://localhost:40025/broadleaf_"+uuid+";mem:broadleaf_"+uuid;
        return jdbcUrl;
    }
}
