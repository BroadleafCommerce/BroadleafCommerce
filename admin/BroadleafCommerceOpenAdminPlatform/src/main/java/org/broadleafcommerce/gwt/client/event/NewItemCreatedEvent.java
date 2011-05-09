package org.broadleafcommerce.gwt.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.widgets.grid.ListGridRecord;

public class NewItemCreatedEvent extends GwtEvent<NewItemCreatedEventHandler> {

	public static Type<NewItemCreatedEventHandler> TYPE = new Type<NewItemCreatedEventHandler>();

	private ListGridRecord record;
	private DataSource dataSource;

	public NewItemCreatedEvent(ListGridRecord record, DataSource dataSource) {
		this.record = record;
		this.dataSource = dataSource;
	}

	@Override
	public Type<NewItemCreatedEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(NewItemCreatedEventHandler handler) {
		handler.onNewItemCreated(this);
	}

	public ListGridRecord getRecord() {
		return record;
	}
	
	public DataSource getDataSource() {
		return dataSource;
	}

}
