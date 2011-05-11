package org.broadleafcommerce.gwt.client.view.dynamic.form;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;

import org.broadleafcommerce.gwt.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.gwt.client.datasource.relations.MapStructure;
import org.broadleafcommerce.gwt.client.presentation.SupportedFieldType;
import org.broadleafcommerce.gwt.client.security.SecurityManager;
import org.broadleafcommerce.gwt.client.validation.ValidationFactoryManager;

import com.google.gwt.i18n.client.ConstantsWithLookup;
import com.google.gwt.i18n.client.NumberFormat;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.events.FetchDataEvent;
import com.smartgwt.client.widgets.events.FetchDataHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.FormItemValueFormatter;
import com.smartgwt.client.widgets.form.fields.BooleanItem;
import com.smartgwt.client.widgets.form.fields.DateTimeItem;
import com.smartgwt.client.widgets.form.fields.FloatItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.HiddenItem;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.SectionItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.validator.Validator;

public class FormBuilder {
	
	public static void buildForm(final DataSource dataSource, DynamicForm form, Boolean showId) {
		buildForm(dataSource, form, null, null, showId);
	}
	
	public static void buildForm(final DataSource dataSource, DynamicForm form, Boolean showDisabledState, Boolean canEdit, Boolean showId) {
		form.setDataSource(dataSource);
		Map<String, List<FormItem>> sections = new HashMap<String, List<FormItem>>();
		Map<String, Integer> sectionNames = new HashMap<String, Integer>();
		DataSourceField[] fields = dataSource.getFields();
		Boolean originalEdit = canEdit;
        for (DataSourceField field : fields) {
        	
        	if (field.getAttribute("securityLevel") != null && field.getAttribute("uniqueID") != null && !SecurityManager.getInstance().isUserAuthorizedToEditField(field.getAttribute("uniqueID"))){
        		canEdit = false;
        	}
        	
        	String fieldType = field.getAttribute("fieldType");
        	if (fieldType != null && !field.getHidden()) {
	    		String group = field.getAttribute("formGroup");
	    		String temp = field.getAttribute("formGroupOrder");
	    		Integer groupOrder = null;
	    		if (temp != null) {
	    			groupOrder = Integer.valueOf(temp);
	    		}
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
		        	if (fieldType.equals(SupportedFieldType.ID.toString())) {
		        		canEdit = false;
		        		showDisabledState = false;
		        	}
		        	setupField(showDisabledState, canEdit, sections, sectionNames, field, group, groupOrder, formItem, displayFormItem);
		        	checkForPasswordField(showDisabledState, canEdit, sections, sectionNames, field, group, groupOrder, formItem, displayFormItem, form);
	        	}
        	}
        	
        	canEdit = originalEdit;
        }
        
        groupFields(form, sections, sectionNames);
	}
	
	public static void buildMapForm(DataSource dataSource, DynamicForm form, MapStructure mapStructure, LinkedHashMap<String, String> mapKeys, Boolean showId) {
		form.setDataSource(dataSource);
		Map<String, List<FormItem>> sections = new HashMap<String, List<FormItem>>();
		Map<String, Integer> sectionNames = new HashMap<String, Integer>();
		DataSourceField[] fields = dataSource.getFields();
        for (DataSourceField field : fields) {
        	String fieldType = field.getAttribute("fieldType");
        	if (fieldType != null && !field.getHidden()) {
	    		String group = field.getAttribute("formGroup");
	    		String temp = field.getAttribute("formGroupOrder");
	    		Integer groupOrder = null;
	    		if (temp != null) {
	    			groupOrder = Integer.valueOf(temp);
	    		}
	        	if (group == null) {
	        		group = "General";
	        	}
	        	FormItem formItem;
	        	FormItem displayFormItem = null;
	        	String fieldName = field.getName();
	        	if (mapStructure != null && mapStructure.getKeyPropertyName().equals(fieldName)) {
	        		formItem = new SelectItem();
	        		((SelectItem) formItem).setValueMap(mapKeys);
	        		((SelectItem) formItem).setMultiple(false);
	        		((SelectItem) formItem).setDefaultToFirstOption(true);
	        		setupField(null, null, sections, sectionNames, field, group, groupOrder, formItem, displayFormItem);
	        	} else {
	        		if (!fieldType.equals(SupportedFieldType.ID.toString()) || (fieldType.equals(SupportedFieldType.ID.toString()) && showId)) {
			        	Boolean largeEntry = field.getAttributeAsBoolean("largeEntry");
			        	if (largeEntry == null) {
			        		largeEntry = false;
			        	}
			        	formItem = buildField(dataSource, field, fieldType, largeEntry);
			        	displayFormItem = buildDisplayField(field, fieldType);
			        	setupField(null, null, sections, sectionNames, field, group, groupOrder, formItem, displayFormItem);
	        		}
	        	}
        	}
        }
        
        groupFields(form, sections, sectionNames);
	}

	protected static void groupFields(DynamicForm form, Map<String, List<FormItem>> sections, final Map<String, Integer> sectionNames) {
		if (sections.size() > 1) {
        	int j=0;
        	List<FormItem> allItems = new ArrayList<FormItem>();
        	String[] groups = new String[sectionNames.size()];
        	groups = sectionNames.keySet().toArray(groups);
        	Arrays.sort(groups, new Comparator<String>() {
				public int compare(String o1, String o2) {
					if (o1.equals(o2)) {
						return 0;
					} else if (o1.equals("General")) {
						return 1;
					} else if (o2.equals("General")) {
						return -1;
					} else {
						Integer groupOrder1 = sectionNames.get(o1);
						Integer groupOrder2 = sectionNames.get(o2);
						if (groupOrder1 == null && groupOrder2 == null) {
							return 0;
						}
						if (groupOrder1 == null) {
							return -1;
						}
						if (groupOrder2 == null) {
							return 1;
						}
						return groupOrder1.compareTo(groupOrder2);
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

	protected static void setupField(Boolean showDisabledState, Boolean canEdit, Map<String, List<FormItem>> sections, Map<String, Integer> sectionNames, DataSourceField field, String group, Integer groupOrder, final FormItem formItem, final FormItem displayFormItem) {
		formItem.setName(field.getName());
		formItem.setTitle(field.getTitle());
		formItem.setRequired(field.getRequired());
		if (!sections.containsKey(group)) {
			List<FormItem> temp = new ArrayList<FormItem>();
			sections.put(group, temp);  
		}
		if (!sectionNames.containsKey(group)) {
			sectionNames.put(group, groupOrder);
		}
		List<FormItem> temp = sections.get(group);
		if (showDisabledState != null) {
			formItem.setShowDisabled(showDisabledState);
		}
		if (canEdit != null) {
			formItem.setDisabled(!canEdit);
		}
		if (!field.getCanEdit()) {
			formItem.setDisabled(true);
		}
		temp.add(formItem);
		if (displayFormItem != null) {
			temp.add(displayFormItem);
		}
	}
	
	protected static void checkForPasswordField(Boolean showDisabledState, Boolean canEdit, Map<String, List<FormItem>> sections, Map<String, Integer> sectionNames, DataSourceField field, String group, Integer groupOrder, final FormItem formItem, final FormItem displayFormItem, DynamicForm form) {
		if (formItem.getClass().getName().equals(PasswordItem.class.getName())) {
			if (field.getValidators() != null && field.getValidators().length > 0) {
				for (Validator validator : field.getValidators()) {
					if (validator.getAttribute("type").equals("matchesField") && validator.getAttribute("otherField") != null) {
						String otherFieldName = validator.getAttribute("otherField");
						final FormItem otherItem = new PasswordItem();
						form.addFetchDataHandler(new FetchDataHandler() {
							public void onFilterData(FetchDataEvent event) {
								otherItem.setValue(formItem.getValue());
							}
						});
						((PasswordItem) otherItem).setLength(((PasswordItem) formItem).getLength());
						otherItem.setName(otherFieldName);
						String title = field.getAttribute("friendlyName") +" Repeat";
						//check to see if we have an i18N version of the new title
						List<ConstantsWithLookup> constants = ValidationFactoryManager.getInstance().getConstants();
						for (ConstantsWithLookup constant : constants) {
							try {
								String val = constant.getString(title);
								if (val != null) {
									title = val;
									break;
								}
							} catch (MissingResourceException e) {
								//do nothing
							}
						}
						otherItem.setTitle(title);
						otherItem.setRequired(field.getRequired());
						if (!sections.containsKey(group)) {
							List<FormItem> temp = new ArrayList<FormItem>();
							sections.put(group, temp);  
						}
						if (!sectionNames.containsKey(group)) {
							sectionNames.put(group, groupOrder);
						}
						List<FormItem> temp = sections.get(group);
						if (showDisabledState != null) {
							otherItem.setShowDisabled(showDisabledState);
						}
						if (canEdit != null) {
							otherItem.setDisabled(!canEdit);
						}
						if (!field.getCanEdit()) {
							otherItem.setDisabled(true);
						}
						temp.add(otherItem);
						if (displayFormItem != null) {
							temp.add(displayFormItem);
						}
						break;
					}
				}
			}
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

	protected static FormItem buildField(final DataSource dataSource, final DataSourceField field, String fieldType, Boolean largeEntry) {
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
			formItem.setEditorValueFormatter(new FormItemValueFormatter() {
				public String formatValue(Object value, Record record, DynamicForm form, FormItem item) {
					return value==null?"":NumberFormat.getFormat("0.00").format(NumberFormat.getFormat("0.00").parse(String.valueOf(value)));
				}
			});
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
			break;
		case BROADLEAF_ENUMERATION:
			formItem = new SelectItem();
			LinkedHashMap<String,String> valueMap = new LinkedHashMap<String,String>();
			String[][] enumerationValues = (String[][]) field.getAttributeAsObject("enumerationValues");
			for (int j=0; j<enumerationValues.length; j++) {
				valueMap.put(enumerationValues[j][0], enumerationValues[j][1]);
			}
			formItem.setValueMap(valueMap);
			break;
		case EMPTY_ENUMERATION:
			formItem = new SelectItem();
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
		case PASSWORD:
			formItem = new PasswordItem();
			((PasswordItem) formItem).setLength(field.getLength());
			break;
		default:
			if (!largeEntry) {
				formItem = new TextItem();
				((TextItem)formItem).setLength(field.getLength());
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
