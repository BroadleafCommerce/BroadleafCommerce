package org.broadleafcommerce.gwt.client.view.dynamic.dialog;

import java.util.Map;

import org.broadleafcommerce.gwt.client.AppController;
import org.broadleafcommerce.gwt.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.gwt.client.event.NewItemCreatedEvent;
import org.broadleafcommerce.gwt.client.event.NewItemCreatedEventHandler;
import org.broadleafcommerce.gwt.client.view.dynamic.form.FormBuilder;

import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.layout.VStack;
import com.smartgwt.client.widgets.tree.TreeNode;

public class EntityEditDialog extends Window {
	
	private DynamicForm dynamicForm;
	private NewItemCreatedEventHandler handler;
	
	public EntityEditDialog() {
		this.setIsModal(true);
		this.setShowModalMask(true);
		this.setShowMinimizeButton(false);
		this.setWidth(600);
		this.setAutoHeight();
		this.setHeight(300);
		this.setCanDragResize(true);
		this.setOverflow(Overflow.VISIBLE);
		//this.setLayoutRightMargin(20);
		
		VStack stack = new VStack();
		stack.setWidth100();
		stack.setLayoutRightMargin(20);
        
		dynamicForm = new DynamicForm(); 
		dynamicForm.setNumCols(4);
        dynamicForm.setPadding(10);
        stack.addMember(dynamicForm);
        addItem(stack);
        
        IButton saveButton = new IButton("Save");  
        saveButton.addClickHandler(new ClickHandler() {  
            public void onClick(ClickEvent event) {  
            	if (dynamicForm.validate()) {
            		dynamicForm.saveData(new DSCallback() {
						public void execute(DSResponse response, Object rawData, DSRequest request) {
							if (response.getErrors().isEmpty()) {
								TreeNode record = new TreeNode(request.getData());
								if (handler != null) {
									AppController.getInstance().getEventBus().addHandler(NewItemCreatedEvent.TYPE, handler);
									try {
										AppController.getInstance().getEventBus().fireEvent(new NewItemCreatedEvent((ListGridRecord) record, dynamicForm.getDataSource()));
									} finally {
										AppController.getInstance().getEventBus().removeHandler(NewItemCreatedEvent.TYPE, handler);
									}
								}
							}
						}
            		});  
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
        
        VLayout vLayout = new VLayout();
        vLayout.setHeight100();
        vLayout.setAlign(VerticalAlignment.BOTTOM);
        HLayout hLayout = new HLayout(10); 
        hLayout.setAlign(Alignment.CENTER);  
        hLayout.addMember(saveButton);  
        hLayout.addMember(cancelButton);
        hLayout.setLayoutTopMargin(40);
        hLayout.setLayoutBottomMargin(40);
        vLayout.addMember(hLayout);
        addItem(vLayout);
	}
	
	@SuppressWarnings("rawtypes")
	public void editNewRecord(DynamicEntityDataSource dataSource, Map initialValues, NewItemCreatedEventHandler handler, String[] fieldNames) {
		editNewRecord(null, dataSource, initialValues, handler, null, fieldNames, null);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void editNewRecord(String title, DynamicEntityDataSource dataSource, Map initialValues, NewItemCreatedEventHandler handler, String heightOverride, String[] fieldNames, String[] ignoreFields) {
		initialValues.put(dataSource.getPrimaryKeyFieldName(), "");
		this.handler = handler;
		if (heightOverride != null) {
			setHeight(heightOverride);
		}
		if (fieldNames != null && fieldNames.length > 0) {
			dataSource.resetPermanentFieldVisibility(fieldNames);
		} else {
			dataSource.resetPermanentFieldVisibility();
		}
		if (ignoreFields != null) {
			for (String fieldName : ignoreFields) {
				dataSource.getField(fieldName).setHidden(true);
			}
		}
		if (title != null) {
			this.setTitle(title);
		} else {
			this.setTitle("Add new entity: " + dataSource.getPolymorphicEntities().get(dataSource.getDefaultNewEntityFullyQualifiedClassname()));
		}
		//dynamicForm.setDataSource(dataSource); 
		buildFields(dataSource, dynamicForm);
        dynamicForm.editNewRecord(initialValues);
        dynamicForm.setWrapItemTitles(false);
        centerInPage();
		show();
	}
	
	protected void buildFields(DataSource dataSource, DynamicForm dynamicForm) {
		FormBuilder.buildForm(dataSource, dynamicForm, false);
	}
}
