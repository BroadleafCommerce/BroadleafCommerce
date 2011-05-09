package org.broadleafcommerce.gwt.client.view.dynamic.form;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.widgets.form.DynamicForm;

public interface FormOnlyDisplay {

	public DynamicForm getForm();
	
	public void buildFields(final DataSource dataSource, Boolean showDisabedState, Boolean canEdit, Boolean showId);
	
}
