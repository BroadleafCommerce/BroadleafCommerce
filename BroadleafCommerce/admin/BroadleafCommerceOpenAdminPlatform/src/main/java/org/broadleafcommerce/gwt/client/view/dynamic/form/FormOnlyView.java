package org.broadleafcommerce.gwt.client.view.dynamic.form;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.layout.VLayout;

public class FormOnlyView extends VLayout implements FormOnlyDisplay {
	
	protected DynamicForm form;
	
	public FormOnlyView() {
		this(null);
	}
	
	public FormOnlyView(DataSource dataSource) {
		super();
		
		setHeight100();
        setWidth100();
        setBackgroundColor("#eaeaea");
        form = new DynamicForm(); 
        form.setHeight(175);
        form.setWidth100();
        form.setNumCols(4);
        form.setPadding(10);
        form.disable();
        form.setBackgroundColor("#eaeaea");
        if (dataSource != null) {
        	buildFields(dataSource, true, false, true);
        }
        addMember(form);
        setOverflow(Overflow.AUTO);
	}
	
	public void buildFields(final DataSource dataSource, Boolean showDisabedState, Boolean canEdit, Boolean showId) {
		FormBuilder.buildForm(dataSource, form, showDisabedState, canEdit, showId);
	}

	public DynamicForm getForm() {
		return form;
	}
	
}
