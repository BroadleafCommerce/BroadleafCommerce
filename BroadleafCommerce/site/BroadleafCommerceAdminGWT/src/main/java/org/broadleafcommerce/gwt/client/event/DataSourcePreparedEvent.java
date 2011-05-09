package org.broadleafcommerce.gwt.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.smartgwt.client.data.DataSource;

public class DataSourcePreparedEvent extends GwtEvent<DataSourcePreparedEventHandler> {

	public static Type<DataSourcePreparedEventHandler> TYPE = new Type<DataSourcePreparedEventHandler>();

	private DataSource dataSource;
	private String token;
	
	public DataSourcePreparedEvent(DataSource dataSource, String token) {
		this.dataSource = dataSource;
		this.token = token;
	}
	
	@Override
	public Type<DataSourcePreparedEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(DataSourcePreparedEventHandler handler) {
		handler.onDataSourcePrepared(this);
	}
	
	public DataSource getDataSource() {
		return dataSource;
	}

	public String getToken() {
		return token;
	}

}
