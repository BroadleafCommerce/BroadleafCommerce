package org.broadleafcommerce.gwt.client.presenter.entity;

import org.broadleafcommerce.gwt.client.datasource.dynamic.AbstractDynamicDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.ListGridDataSource;

import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.Record;

public interface SubPresentable {

	public void setStartState();
	
	public void enable();
	
	public void disable();
	
	public void load(Record associatedRecord, AbstractDynamicDataSource associatedDataSource, final DSCallback cb);
	
	public void bind();

	public void setReadOnly(Boolean readOnly);
	
	public void setDataSource(ListGridDataSource dataSource, String[] gridFields, Boolean[] editable);
	
}
