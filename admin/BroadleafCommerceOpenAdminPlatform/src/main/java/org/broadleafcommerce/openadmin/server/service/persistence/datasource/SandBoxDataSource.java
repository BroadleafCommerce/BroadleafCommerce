package org.broadleafcommerce.openadmin.server.service.persistence.datasource;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.pool.KeyedPoolableObjectFactory;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.broadleafcommerce.openadmin.server.service.SandBoxContext;
import org.hsqldb.Server;
import org.hsqldb.persist.HsqlProperties;

public class SandBoxDataSource implements DataSource {
	
	public static final String DRIVERNAME = "org.hsqldb.jdbcDriver";
	
	protected PrintWriter logWriter;
	protected int loginTimeout = 5;
	protected String tempFileDirectory;
	protected GenericKeyedObjectPool sandboxDataBasePool;
	protected Server server;
	
	public SandBoxDataSource() {
		try {
			Class.forName(DRIVERNAME);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		HsqlProperties p = new HsqlProperties();
	    p.setProperty("server.remote_open",true);
		server = new Server();
	    server.setProperties(p);
	    server.setLogWriter(null);
	    server.setErrWriter(null);
	    server.start();
		sandboxDataBasePool = new GenericKeyedObjectPool(new PoolableSandBoxDataBaseFactory());
	}
	
	public void close() {
		try {
			sandboxDataBasePool.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			Connection connection = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/broadleaf;mem:broadleaf", "SA", "");
			connection.prepareStatement("SHUTDOWN").execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	//GenericKeyedObjectPool methods

	public int getMaxActive() {
		return sandboxDataBasePool.getMaxActive();
	}

	public void setMaxActive(int maxActive) {
		sandboxDataBasePool.setMaxActive(maxActive);
	}

	public int getMaxTotal() {
		return sandboxDataBasePool.getMaxTotal();
	}

	public void setMaxTotal(int maxTotal) {
		sandboxDataBasePool.setMaxTotal(maxTotal);
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

	public boolean getLifo() {
		return sandboxDataBasePool.getLifo();
	}

	public void setLifo(boolean lifo) {
		sandboxDataBasePool.setLifo(lifo);
	}

	public int getNumActive() {
		return sandboxDataBasePool.getNumActive();
	}

	public int getNumIdle() {
		return sandboxDataBasePool.getNumIdle();
	}

	public int getNumActive(Object key) {
		return sandboxDataBasePool.getNumActive(key);
	}

	public int getNumIdle(Object key) {
		return sandboxDataBasePool.getNumIdle(key);
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
			return (Connection) sandboxDataBasePool.borrowObject(SandBoxContext.getSandBoxContext().getSandBoxName());
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		throw new SQLException("Not Supported");
	}

	private class PoolableSandBoxDataBaseFactory implements KeyedPoolableObjectFactory {

		@Override
		public Object makeObject(Object key) throws Exception {
			Connection connection = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/broadleaf_"+key+";mem:broadleaf_"+key, "SA", "");
			SandBoxConnection blcConnection = new SandBoxConnection(connection, sandboxDataBasePool, (String) key);
			return blcConnection;
		}

		@Override
		public void destroyObject(Object key, Object obj) throws Exception {
			Connection c = (Connection) obj;
			c.prepareStatement("DROP SCHEMA " + key + " CASCADE").execute();
		}

		@Override
		public boolean validateObject(Object key, Object obj) {
			//TODO add a generic connection validation
			return true;
		}

		@Override
		public void activateObject(Object key, Object obj) throws Exception {
			//do nothing
		}

		@Override
		public void passivateObject(Object key, Object obj) throws Exception {
			//do nothing
		}
		
	}

}
