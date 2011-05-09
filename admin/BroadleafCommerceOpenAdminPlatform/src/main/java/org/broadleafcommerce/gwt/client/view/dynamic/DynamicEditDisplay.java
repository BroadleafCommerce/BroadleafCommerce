package org.broadleafcommerce.gwt.client.view.dynamic;

import org.broadleafcommerce.gwt.client.view.Display;
import org.broadleafcommerce.gwt.client.view.dynamic.form.DynamicFormDisplay;

import com.smartgwt.client.data.DataSource;

public interface DynamicEditDisplay extends Display {

	public void build(DataSource entityDataSource, DataSource... additionalDataSources);
	
	public DynamicFormDisplay getDynamicFormDisplay();
	
	public DynamicEntityListDisplay getListDisplay();
	
}
