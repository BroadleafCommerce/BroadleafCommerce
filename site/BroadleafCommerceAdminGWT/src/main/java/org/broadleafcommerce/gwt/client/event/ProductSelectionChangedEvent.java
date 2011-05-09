package org.broadleafcommerce.gwt.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.widgets.grid.ListGridRecord;

public class ProductSelectionChangedEvent extends GwtEvent<ProductSelectionChangedEventHandler> {

	public static Type<ProductSelectionChangedEventHandler> TYPE = new Type<ProductSelectionChangedEventHandler>();

	private ListGridRecord record;
	private DataSource dataSource;

	public ProductSelectionChangedEvent(ListGridRecord record, DataSource dataSource) {
		this.record = record;
		this.dataSource = dataSource;
	}

	@Override
	public Type<ProductSelectionChangedEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ProductSelectionChangedEventHandler handler) {
		handler.onChangeSelection(this);
	}

	public ListGridRecord getRecord() {
		return record;
	}
	
	public DataSource getDataSource() {
		return dataSource;
	}

}
