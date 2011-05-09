package org.broadleafcommerce.gwt.client.view.dynamic.form;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.broadleafcommerce.gwt.client.datasource.dynamic.DynamicEntityDataSource;
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
import com.smartgwt.client.widgets.form.fields.HiddenItem;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.form.fields.SectionItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;

public class FormBuilder {
	
	public static void buildForm(final DataSource dataSource, DynamicForm form, Boolean showId) {
		buildForm(dataSource, form, null, null, showId);
	}
	
	public static void buildForm(final DataSource dataSource, DynamicForm form, Boolean showDisabledState, Boolean canEdit, Boolean showId) {
		form.setDataSource(dataSource);
		Map<String, List<FormItem>> sections = new HashMap<String, List<FormItem>>();
		List<String> sectionNames = new ArrayList<String>();
		DataSourceField[] fields = dataSource.getFields();
        for (DataSourceField field : fields) {
        	String fieldType = field.getAttribute("fieldType");
        	if (fieldType != null && !field.getHidden()) {
	    		String group = field.getAttribute("formGroup");
	        	if (group == null) {
	        		if (fieldType.equals(SupportedFieldType.ID.toString())) {
	        			group = "Primary Key";
	        		} else {
	        			group = "General";
	        		}
	        	}
	        	if (!fieldType.equals(SupportedFieldType.ID.toString()) || (fieldType.equals(SupportedFieldType.ID.toString()) && showId)) {
		        	Boolean largeEntry = field.getAttributeAsBoolean("largeEntry");
		        	if (largeEntry == null) {
		        		largeEntry = false;
		        	}
		        	final FormItem formItem = buildField(dataSource, field, fieldType, largeEntry);
		        	final FormItem displayFormItem = buildDisplayField(field, fieldType);
		        	setupField(showDisabledState, canEdit, sections, sectionNames, field, group, formItem, displayFormItem);
	        	}
        	}
        }
        
        groupFields(form, sections, sectionNames);
	}
	
	public static void buildMapForm(DataSource dataSource, DynamicForm form, MapStructure mapStructure, LinkedHashMap<String, String> mapKeys, Boolean showId) {
		form.setDataSource(dataSource);
		Map<String, List<FormItem>> sections = new HashMap<String, List<FormItem>>();
		List<String> sectionNames = new ArrayList<String>();
		DataSourceField[] fields = dataSource.getFields();
        for (DataSourceField field : fields) {
        	String fieldType = field.getAttribute("fieldType");
        	if (fieldType != null && !field.getHidden()) {
	    		String group = field.getAttribute("formGroup");
	        	if (group == null) {
	        		if (fieldType.equals(SupportedFieldType.ID.toString())) {
	        			group = "Primary Key";
	        		} else {
	        			group = "General";
	        		}
	        	}
	        	FormItem formItem;
	        	FormItem displayFormItem = null;
	        	if (mapStructure != null && mapStructure.getKeyPropertyName().equals(field.getName())) {
	        		formItem = new SelectItem();
	        		((SelectItem) formItem).setValueMap(mapKeys);
	        		((SelectItem) formItem).setMultiple(false);
	        		((SelectItem) formItem).setDefaultToFirstOption(true);
	        	} else {
	        		if (!fieldType.equals(SupportedFieldType.ID.toString()) || (fieldType.equals(SupportedFieldType.ID.toString()) && showId)) {
			        	Boolean largeEntry = field.getAttributeAsBoolean("largeEntry");
			        	if (largeEntry == null) {
			        		largeEntry = false;
			        	}
			        	formItem = buildField(dataSource, field, fieldType, largeEntry);
			        	displayFormItem = buildDisplayField(field, fieldType);
			        	setupField(null, null, sections, sectionNames, field, group, formItem, displayFormItem);
	        		}
	        	}
        	}
        }
        
        groupFields(form, sections, sectionNames);
	}

	protected static void groupFields(DynamicForm form, Map<String, List<FormItem>> sections, List<String> sectionNames) {
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
					} else if (o1.equals("Primary Key")) {
						return -1;
					} else if (o2.equals("Primary Key")) {
						return 1;
					} else {
						return 0;
					}
				}
        	});
        	for (String group : groups) {
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
        	form.setItems(allFormItems);
        } else {
        	List<FormItem> formItems = sections.values().iterator().next();
        	FormItem[] allFormItems = new FormItem[formItems.size()];
        	allFormItems = formItems.toArray(allFormItems);
        	form.setItems(allFormItems);
        }
	}

	protected static void setupField(Boolean showDisabledState, Boolean canEdit, Map<String, List<FormItem>> sections, List<String> sectionNames, DataSourceField field, String group, final FormItem formItem, final FormItem displayFormItem) {
		formItem.setName(field.getName());
		formItem.setTitle(field.getTitle());
		//formItem.setWrapTitle(false);
		formItem.setRequired(field.getRequired());
		if (!sections.containsKey(group)) {
			List<FormItem> temp = new ArrayList<FormItem>();
			sections.put(group, temp);  
		}
		if (!sectionNames.contains(group)) {
			sectionNames.add(group);
		}
		List<FormItem> temp = sections.get(group);
		if (showDisabledState != null) {
			formItem.setShowDisabled(showDisabledState);
		}
		if (canEdit != null) {
			formItem.setDisabled(!canEdit);
		}
		temp.add(formItem);
		if (displayFormItem != null) {
			temp.add(displayFormItem);
		}
	}
	
	protected static FormItem buildDisplayField(DataSourceField field, String fieldType) {
		FormItem displayFormItem = null;
		switch(SupportedFieldType.valueOf(fieldType)){
		case FOREIGN_KEY:
			displayFormItem = new HiddenItem();
			displayFormItem.setName("__display_"+field.getName());
			break;
		case ADDITIONAL_FOREIGN_KEY:
			displayFormItem = new HiddenItem();
			displayFormItem.setName("__display_"+field.getName());
			break;
		}
		return displayFormItem;
	}

	protected static FormItem buildField(final DataSource dataSource, DataSourceField field, String fieldType, Boolean largeEntry) {
		final FormItem formItem;
		switch(SupportedFieldType.valueOf(fieldType)){
		case BOOLEAN:
			formItem = new BooleanItem();
			formItem.setValueFormatter(new FormItemValueFormatter() {
				public String formatValue(Object value, Record record, DynamicForm form, FormItem item) {
					if (value == null) {
						item.setValue(false);
						return "false";
					}
					return String.valueOf(value);
				}
			});
			//formItem.setWidth("250");
			break;
		case DATE:
			formItem = new DateTimeItem();
			//formItem.setWidth("250");
			break;
		case DECIMAL:
			formItem = new FloatItem();
			//formItem.setWidth("250");
			break;
		case EMAIL:
			formItem = new TextItem();
			((TextItem)formItem).setLength(field.getLength());
			//formItem.setWidth("250");
			break;
		case INTEGER:
			formItem = new IntegerItem();
			//formItem.setWidth("250");
			break;
		case MONEY:
			formItem = new FloatItem();
			formItem.setEditorValueFormatter(new FormItemValueFormatter() {
				public String formatValue(Object value, Record record, DynamicForm form, FormItem item) {
					return value==null?"":NumberFormat.getFormat("0.00").format(NumberFormat.getFormat("0.00").parse(String.valueOf(value)));
				}
			});
			//formItem.setWidth("250");
			break;
		case FOREIGN_KEY:
			formItem = new SearchFormItem();
			formItem.setEditorValueFormatter(new FormItemValueFormatter() {
				public String formatValue(Object value, Record record, DynamicForm form, FormItem item) {
					String response;
					if (value == null) {
						response = "";
					} else {
						response = (String) form.getField("__display_"+item.getName()).getValue();
					}
					return response;
				}
			});
			//formItem.setWidth("250");
			break;
		case ADDITIONAL_FOREIGN_KEY:
			formItem = new SearchFormItem();
			formItem.setEditorValueFormatter(new FormItemValueFormatter() {
				public String formatValue(Object value, Record record, DynamicForm form, FormItem item) {
					String response;
					if (value == null) {
						response = "";
					} else {
						response = (String) form.getField("__display_"+item.getName()).getValue();
					}
					return response;
				}
			});
			//formItem.setWidth("250");
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
			//formItem.setWidth("250");
			break;
		case EMPTY_ENUMERATION:
			formItem = new SelectItem();
			//formItem.setWidth("250");
			break;
		case ID:
			formItem = new TextItem();
			((TextItem)formItem).setLength(field.getLength());
			formItem.setValueFormatter(new FormItemValueFormatter() {
				public String formatValue(Object value, Record record, DynamicForm form, FormItem item) {
					return value==null?"":((DynamicEntityDataSource) dataSource).stripDuplicateAllowSpecialCharacters(String.valueOf(value));
				}
			});
			break;
		default:
			if (!largeEntry) {
				formItem = new TextItem();
				((TextItem)formItem).setLength(field.getLength());
				//formItem.setWidth("250");
			} else {
				formItem = new TextAreaItem();
				((TextAreaItem)formItem).setLength(field.getLength());
				formItem.setHeight(70);
				formItem.setColSpan(3);
				formItem.setWidth("400");
			}
			break;
		}
		return formItem;
	}

}
