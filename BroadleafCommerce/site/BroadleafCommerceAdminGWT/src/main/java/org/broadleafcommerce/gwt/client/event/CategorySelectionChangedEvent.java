package org.broadleafcommerce.gwt.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.smartgwt.client.widgets.grid.ListGridRecord;

public class CategorySelectionChangedEvent extends GwtEvent<CategorySelectionChangedEventHandler> {

	public static Type<CategorySelectionChangedEventHandler> TYPE = new Type<CategorySelectionChangedEventHandler>();

	private ListGridRecord record;

	public CategorySelectionChangedEvent(ListGridRecord record) {
		this.record = record;
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

}
