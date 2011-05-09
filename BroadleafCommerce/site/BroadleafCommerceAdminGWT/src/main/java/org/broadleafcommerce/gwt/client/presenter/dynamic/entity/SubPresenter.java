package org.broadleafcommerce.gwt.client.presenter.dynamic.entity;

import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.Record;

public interface SubPresenter {

	public void setStartState();
	
	public void enable();
	
	public void disable();
	
	public void load(Record associatedRecord, final DSCallback cb);
	
	public void bind();
	
}
