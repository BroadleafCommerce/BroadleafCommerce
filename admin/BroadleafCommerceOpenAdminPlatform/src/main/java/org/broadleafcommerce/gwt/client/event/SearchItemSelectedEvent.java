package org.broadleafcommerce.gwt.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.widgets.grid.ListGridRecord;

public class SearchItemSelectedEvent extends GwtEvent<SearchItemSelectedEventHandler> {

	public static Type<SearchItemSelectedEventHandler> TYPE = new Type<SearchItemSelectedEventHandler>();

	private ListGridRecord record;
	private DataSource dataSource;

	public SearchItemSelectedEvent(ListGridRecord record, DataSource dataSource) {
		this.record = record;
		this.dataSource = dataSource;
	}

	@Override
	public Type<SearchItemSelectedEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SearchItemSelectedEventHandler handler) {
		handler.onSearchItemSelected(this);
	}

	public ListGridRecord getRecord() {
		return record;
	}
	
	public DataSource getDataSource() {
		return dataSource;
	}

}
