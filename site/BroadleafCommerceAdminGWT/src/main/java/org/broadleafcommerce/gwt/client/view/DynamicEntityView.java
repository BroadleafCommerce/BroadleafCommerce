package org.broadleafcommerce.gwt.client.view;

import java.util.Map;

import org.broadleafcommerce.gwt.client.datasource.DynamicEntityDataSource;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.layout.HLayout;

public class DynamicEntityView extends Window {
	
	private DynamicForm dynamicForm;
	
	public DynamicEntityView() {
		this.setIsModal(true);
		this.setShowModalMask(true);
		this.setShowMinimizeButton(false);
		this.setWidth(600);
		this.setHeight(300);
		this.setCanDragResize(true);
		this.setOverflow(Overflow.AUTO);
		dynamicForm = new DynamicForm(); 
		dynamicForm.setNumCols(4);
        dynamicForm.setPadding(10);
        addItem(dynamicForm);
        
        IButton saveButton = new IButton("Save");  
        saveButton.addClickHandler(new ClickHandler() {  
            public void onClick(ClickEvent event) {  
            	if (dynamicForm.validate()) {
            		dynamicForm.saveData();  
            		hide();
            	}
            }  
        });  

        IButton cancelButton = new IButton("Cancel");  
        cancelButton.addClickHandler(new ClickHandler() {  
            public void onClick(ClickEvent event) {  
            	hide();
            }  
        });
        
        HLayout hLayout = new HLayout(10);  
        hLayout.setAlign(Alignment.CENTER);  
        hLayout.addMember(saveButton);  
        hLayout.addMember(cancelButton);
        hLayout.setLayoutTopMargin(40);
        addItem(hLayout);
	}
	
	public void editNewRecord(DynamicEntityDataSource dataSource, Map<String, String> initialValues) {
		dataSource.resetFieldVisibility();
		this.setTitle("Add new entity: " + dataSource.getPolymorphicEntities().get(dataSource.getDefaultNewEntityFullyQualifiedClassname()));
		dynamicForm.setDataSource(dataSource); 
        dynamicForm.editNewRecord(initialValues);
        centerInPage();
		show();
	}

}
