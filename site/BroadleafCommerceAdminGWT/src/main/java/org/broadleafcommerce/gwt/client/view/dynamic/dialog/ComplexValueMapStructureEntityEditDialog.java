package org.broadleafcommerce.gwt.client.view.dynamic.dialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.broadleafcommerce.gwt.client.datasource.relations.MapStructure;
import org.broadleafcommerce.presentation.SupportedFieldType;

import com.google.gwt.i18n.client.NumberFormat;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.FormItemValueFormatter;
import com.smartgwt.client.widgets.form.fields.BooleanItem;
import com.smartgwt.client.widgets.form.fields.DateTimeItem;
import com.smartgwt.client.widgets.form.fields.FloatItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.form.fields.SectionItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;

public class ComplexValueMapStructureEntityEditDialog extends EntityEditDialog {
	
	protected MapStructure mapStructure;
	protected LinkedHashMap<String, String> mapKeys;

	public ComplexValueMapStructureEntityEditDialog(MapStructure mapStructure, LinkedHashMap<String, String> mapKeys) {
		super();
		this.mapStructure = mapStructure;
		this.mapKeys = mapKeys;
		this.setHeight("300");
	}
	
	@Override
	public void buildFields(DataSource dataSource, DynamicForm dynamicForm) {
		dynamicForm.setDataSource(dataSource);
		Map<String, List<FormItem>> sections = new HashMap<String, List<FormItem>>();
		List<String> sectionNames = new ArrayList<String>();
		DataSourceField[] fields = dataSource.getFields();
        for (DataSourceField field : fields) {
        	String fieldType = field.getAttribute("fieldType");
        	if (fieldType != null && !field.getHidden()) {
	    		String group = field.getAttribute("formGroup");
	        	if (group == null) {
	        		group = "General";
	        	}
	        	FormItem formItem;
	        	if (mapStructure.getKeyPropertyName().equals(field.getName())) {
	        		formItem = new SelectItem();
	        		((SelectItem) formItem).setValueMap(mapKeys);
	        		((SelectItem) formItem).setMultiple(false);
	        		((SelectItem) formItem).setDefaultToFirstOption(true);
	        	} else {
		        	Boolean largeEntry = field.getAttributeAsBoolean("largeEntry");
		        	if (largeEntry == null) {
		        		largeEntry = false;
		        	}
		        	switch(SupportedFieldType.valueOf(fieldType)){
		        	case BOOLEAN:
		        		formItem = new BooleanItem();
		        		formItem.setValueFormatter(new FormItemValueFormatter() {
							public String formatValue(Object value, Record record, DynamicForm form, FormItem item) {
								if (value == null) {
									item.setValue(false);
									return "false";
								}
								return (String) value;
							}
		        		});
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
		        		formItem.setValueFormatter(new FormItemValueFormatter() {
							public String formatValue(Object value, Record record, DynamicForm form, FormItem item) {
								return value==null?"":NumberFormat.getFormat("0.00").format(NumberFormat.getFormat("0.00").parse((String) value));
							}
		        		});
		        		break;
		        	case BROADLEAF_ENUMERATION:
		        		formItem = new SelectItem();
		        		LinkedHashMap<String,String> valueMap = new LinkedHashMap<String,String>();
		        		String[] enumerationValues = field.getAttributeAsStringArray("enumerationValues");
		        		for (String value : enumerationValues) {
		        			String shortName;
		        			String longName;
		        			int pos = value.indexOf(",");
		        			if (pos >= 0) {
		        				shortName = value.substring(0, pos);
		        				longName = value.substring(pos + 1, value.length());
		        			} else {
		        				shortName = value;
		        				longName = value;
		        			}
		        			valueMap.put(shortName, longName);
		        		}
		        		formItem.setValueMap(valueMap);
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
	        	}
	        	formItem.setWidth("100%");
	        	formItem.setName(field.getName());
	        	formItem.setTitle(field.getTitle());
	        	formItem.setWrapTitle(false);
	        	formItem.setRequired(field.getRequired());
	        	if (!sections.containsKey(group)) {
	        		List<FormItem> temp = new ArrayList<FormItem>();
	        		sections.put(group, temp);  
	        	}
	        	if (!sectionNames.contains(group)) {
	        		sectionNames.add(group);
	        	}
	        	List<FormItem> temp = sections.get(group);
	        	temp.add(formItem);
        	}
        }
        
        if (sections.size() > 1) {
        	int j=0;
        	List<FormItem> allItems = new ArrayList<FormItem>();
        	String[] groups = new String[sectionNames.size()];
        	groups = sectionNames.toArray(groups);
        	Arrays.sort(groups, new Comparator<String>() {
				public int compare(String o1, String o2) {
					if (o1.equals(o2)) {
						return 0;
					} else if (o1.equals("General")) {
						return 1;
					} else if (o2.equals("General")) {
						return -1;
					} else {
						return 0;
					}
				}
        	});
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
