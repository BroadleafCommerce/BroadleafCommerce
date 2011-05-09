package org.broadleafcommerce.gwt.client.view.dynamic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.broadleafcommerce.gwt.client.AppController;
import org.broadleafcommerce.gwt.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.gwt.client.event.NewItemCreatedEvent;
import org.broadleafcommerce.gwt.client.event.NewItemCreatedEventHandler;
import org.broadleafcommerce.presentation.SupportedFieldType;

import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.BooleanItem;
import com.smartgwt.client.widgets.form.fields.DateTimeItem;
import com.smartgwt.client.widgets.form.fields.FloatItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.form.fields.SectionItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VStack;
import com.smartgwt.client.widgets.tree.TreeNode;

public class DynamicEntityView extends Window {
	
	private DynamicForm dynamicForm;
	private NewItemCreatedEventHandler handler;
	
	public DynamicEntityView() {
		this.setIsModal(true);
		this.setShowModalMask(true);
		this.setShowMinimizeButton(false);
		this.setWidth(600);
		this.setHeight(300);
		this.setCanDragResize(true);
		this.setOverflow(Overflow.AUTO);
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
					            	AppController.getInstance().getEventBus().fireEvent(new NewItemCreatedEvent((ListGridRecord) record, dynamicForm.getDataSource()));
					            	AppController.getInstance().getEventBus().removeHandler(NewItemCreatedEvent.TYPE, handler);
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
        
        HLayout hLayout = new HLayout(10);  
        hLayout.setAlign(Alignment.CENTER);  
        hLayout.addMember(saveButton);  
        hLayout.addMember(cancelButton);
        hLayout.setLayoutTopMargin(40);
        hLayout.setLayoutBottomMargin(40);
        addItem(hLayout);
	}
	
	@SuppressWarnings("rawtypes")
	public void editNewRecord(DynamicEntityDataSource dataSource, Map initialValues, NewItemCreatedEventHandler handler, String... fieldNames) {
		editNewRecord(null, dataSource, initialValues, handler, fieldNames);
	}
	
	@SuppressWarnings("rawtypes")
	public void editNewRecord(String title, DynamicEntityDataSource dataSource, Map initialValues, NewItemCreatedEventHandler handler, String... fieldNames) {
		this.handler = handler;
		if (fieldNames != null && fieldNames.length > 0) {
			dataSource.resetFieldVisibility(fieldNames);
		} else {
			dataSource.resetFieldVisibility();
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

	public void buildFields(DataSource dataSource, DynamicForm dynamicForm) {
		dynamicForm.setDataSource(dataSource);
		Map<String, List<FormItem>> sections = new HashMap<String, List<FormItem>>();
		DataSourceField[] fields = dataSource.getFields();
        for (DataSourceField field : fields) {
        	String fieldType = field.getAttribute("fieldType");
        	if (fieldType != null && !field.getHidden()) {
	    		String group = field.getAttribute("formGroup");
	        	if (group == null) {
	        		group = "General";
	        	}
	        	Boolean largeEntry = field.getAttributeAsBoolean("largeEntry");
	        	if (largeEntry == null) {
	        		largeEntry = false;
	        	}
	        	FormItem formItem;
	        	switch(SupportedFieldType.valueOf(fieldType)){
	        	case BOOLEAN:
	        		formItem = new BooleanItem();
	        		break;
	        	case DATE:
	        		formItem = new DateTimeItem();
	        		break;
	        	case DECIMAL:
	        		formItem = new FloatItem();
	        		break;
	        	case EMAIL:
	        		formItem = new TextItem();
	        		((TextItem)formItem).setLength(field.getLength());
	        		break;
	        	case INTEGER:
	        		formItem = new IntegerItem();
	        		break;
	        	case MONEY:
	        		formItem = new FloatItem();
	        		break;
	        	default:
	        		if (!largeEntry) {
	        			formItem = new TextItem();
	            		((TextItem)formItem).setLength(field.getLength());
	        		} else {
	        			formItem = new TextAreaItem();
	            		((TextAreaItem)formItem).setLength(field.getLength());
	            		formItem.setHeight(40);
	            		formItem.setColSpan(3);
	        		}
	        		break;
	        	}
	        	formItem.setWidth("100%");
	        	formItem.setName(field.getName());
	        	formItem.setTitle(field.getTitle());
	        	formItem.setRequired(field.getRequired());
	        	if (!sections.containsKey(group)) {
	        		List<FormItem> temp = new ArrayList<FormItem>();
	        		sections.put(group, temp);  
	        	}
	        	List<FormItem> temp = sections.get(group);
	        	temp.add(formItem);
        	}
        }
        
        if (sections.size() > 1) {
        	int j=0;
        	List<FormItem> allItems = new ArrayList<FormItem>();
        	for (String group : sections.keySet()) {
        		SectionItem section = new SectionItem();  
                section.setDefaultValue(group);  
                section.setSectionExpanded(true); 
                List<FormItem> formItems = sections.get(group);
                String[] ids = new String[formItems.size()];
                int x=0;
                for (FormItem formItem : formItems) {
                	ids[x] = formItem.getName();
                	x++;
                }
                section.setItemIds(ids);
                allItems.add(section);
                allItems.addAll(formItems);
                j++;
        	}
        	FormItem[] allFormItems = new FormItem[allItems.size()];
        	allFormItems = allItems.toArray(allFormItems);
        	dynamicForm.setItems(allFormItems);
        } else {
        	List<FormItem> formItems = sections.values().iterator().next();
        	FormItem[] allFormItems = new FormItem[formItems.size()];
        	allFormItems = formItems.toArray(allFormItems);
        	dynamicForm.setItems(allFormItems);
        }
	}
}
