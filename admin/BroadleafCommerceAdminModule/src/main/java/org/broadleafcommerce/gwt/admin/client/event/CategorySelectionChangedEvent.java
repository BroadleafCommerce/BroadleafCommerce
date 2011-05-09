package org.broadleafcommerce.gwt.admin.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.widgets.grid.ListGridRecord;

public class CategorySelectionChangedEvent extends GwtEvent<CategorySelectionChangedEventHandler> {

	public static Type<CategorySelectionChangedEventHandler> TYPE = new Type<CategorySelectionChangedEventHandler>();

	private ListGridRecord record;
	private DataSource dataSource;

	public CategorySelectionChangedEvent(ListGridRecord record, DataSource dataSource) {
		this.record = record;
		this.dataSource = dataSource;
	}

	@Override
	public Type<CategorySelectionChangedEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(CategorySelectionChangedEventHandler handler) {
		handler.onChangeSelection(this);
	}

	public ListGridRecord getRecord() {
		return record;
	}
	
	public DataSource getDataSource() {
		return dataSource;
	}

}
