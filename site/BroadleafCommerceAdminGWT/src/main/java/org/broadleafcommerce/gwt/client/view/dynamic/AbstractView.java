package org.broadleafcommerce.gwt.client.view.dynamic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.broadleafcommerce.presentation.SupportedFieldType;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.BooleanItem;
import com.smartgwt.client.widgets.form.fields.DateTimeItem;
import com.smartgwt.client.widgets.form.fields.FloatItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.form.fields.SectionItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.tree.TreeGrid;

public abstract class AbstractView extends HLayout implements DynamicListDisplay<TreeGrid> {
	
	public AbstractView() {
		setHeight100();
		setWidth100();
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
