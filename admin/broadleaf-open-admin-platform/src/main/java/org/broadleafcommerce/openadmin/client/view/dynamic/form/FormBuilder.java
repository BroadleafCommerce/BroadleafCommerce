/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.client.view.dynamic.form;

import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.openadmin.client.dto.MapStructure;
import org.broadleafcommerce.openadmin.client.security.SecurityManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.events.FetchDataEvent;
import com.smartgwt.client.widgets.events.FetchDataHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.FormItemValueFormatter;
import com.smartgwt.client.widgets.form.fields.BooleanItem;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.DateTimeItem;
import com.smartgwt.client.widgets.form.fields.FloatItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.HeaderItem;
import com.smartgwt.client.widgets.form.fields.HiddenItem;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.UploadItem;
import com.smartgwt.client.widgets.form.validator.Validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;

/**
 * 
 * @author jfischer
 *
 */
public class FormBuilder {
    
    public static void buildForm(final DataSource dataSource, DynamicForm form, Boolean showId, Record currentRecord) {
        buildForm(dataSource, form, null, null, showId, currentRecord);
    }
    
    public static void buildForm(final DataSource dataSource, DynamicForm form, Boolean showDisabledState, Boolean canEdit, Boolean showId, Record currentRecord) {
        String[] recordAttributes = currentRecord == null?null:currentRecord.getAttributes();
        if (recordAttributes != null) {
            Arrays.sort(recordAttributes);
        }
        form.setDataSource(dataSource);
        form.setCellPadding(8);
        Map<String, List<FormItem>> sections = new HashMap<String, List<FormItem>>();
        Map<String, Boolean> sectionCollapsed = new HashMap<String, Boolean>();
        Map<String, Integer> sectionNames = new HashMap<String, Integer>();
        DataSourceField[] fields = dataSource.getFields();
        Boolean originalEdit = canEdit;
        for (DataSourceField field : fields) {

            if (field.getAttribute("securityLevel") != null && field.getAttribute("uniqueID") != null && !SecurityManager.getInstance().isUserAuthorizedToEditField(field.getAttribute("uniqueID"))){
                canEdit = false;
            }
            String name = field.getName();
            String fieldType = field.getAttribute("fieldType");
            FormHiddenEnum enumVal = (FormHiddenEnum) field.getAttributeAsObject("formHidden");
            /*
                Check to make sure this field exists for this record. This could be a polymorphic type
                for this record that does not have this field. This is normally taken care of during inspection
                for a datasource ceiling entity. However, occasion can arise where there is a ManyToOne
                relationship on an entity to another entity with a polymorphic type (e.g. an OrderItem
                with a relationship to polymorphic product). In this instance, fields exposed for this
                related entity should be hidden or shown appropriately in the form based on the the
                type of this related entity. This only comes into play when the populateToOne property of
                the @AdminPresentationClass annotation has been set to true.
             */
            boolean isFieldAvailableForRecord = false;
            if (currentRecord != null) {
                int pos = Arrays.binarySearch(recordAttributes, name);
                isFieldAvailableForRecord = pos >= 0;
            } else {
                isFieldAvailableForRecord = true;
            }
            if (fieldType != null && (!field.getHidden() || enumVal == FormHiddenEnum.VISIBLE) && enumVal != FormHiddenEnum.HIDDEN && isFieldAvailableForRecord) {
                String group = field.getAttribute("formGroup");
                String temp = field.getAttribute("formGroupOrder");
                if (field.getAttributeAsBoolean("formGroupCollapsed") != null) {
                    sectionCollapsed.put(group, field.getAttributeAsBoolean("formGroupCollapsed"));
                }
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
                    final FormItem formItem = buildField(dataSource, field, fieldType, largeEntry, form);
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
        
        groupFields(form, sections, sectionNames, sectionCollapsed);
    }

    public static void buildMapForm(DataSource dataSource, DynamicForm form, MapStructure mapStructure, DataSource optionDataSource, String displayField, String valueField, Boolean showId, Record currentRecord) {
        buildMapForm(dataSource, form, mapStructure, null, optionDataSource, displayField, valueField, showId, currentRecord);
    }

    public static void buildMapForm(DataSource dataSource, DynamicForm form, MapStructure mapStructure, LinkedHashMap<String, String> mapKeys, Boolean showId, Record currentRecord) {
        buildMapForm(dataSource, form, mapStructure, mapKeys, null, null, null, showId, currentRecord);
    }
    
    private static void buildMapForm(DataSource dataSource, DynamicForm form, MapStructure mapStructure, LinkedHashMap<String, String> mapKeys, DataSource optionDataSource, String displayField, String valueField, Boolean showId, Record currentRecord) {
        if (mapKeys == null && optionDataSource == null) {
            throw new RuntimeException("Must provide either map keys or and option datasource to control the values for the key field.");
        }
        String[] recordAttributes = currentRecord == null?null:currentRecord.getAttributes();
        if (recordAttributes != null) {
            Arrays.sort(recordAttributes);
        }
        form.setDataSource(dataSource);
        Map<String, List<FormItem>> sections = new HashMap<String, List<FormItem>>();
        Map<String, Integer> sectionNames = new HashMap<String, Integer>();
        Map<String, Boolean> sectionCollapsed = new HashMap<String, Boolean>();
        DataSourceField[] fields = dataSource.getFields();
        for (DataSourceField field : fields) {
            String fieldType = field.getAttribute("fieldType");
            if (fieldType != null && !field.getHidden()) {
                String group = field.getAttribute("formGroup");
                String temp = field.getAttribute("formGroupOrder");
                if (field.getAttributeAsBoolean("formGroupCollapsed") != null) {
                    sectionCollapsed.put(group, field.getAttributeAsBoolean("formGroupCollapsed"));
                }
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
                    formItem = new ComboBoxItem();
                    if (mapKeys != null) {
                        formItem.setValueMap(mapKeys);
                    } else {
                        formItem.setOptionDataSource(optionDataSource);
                        formItem.setDisplayField(displayField);
                        formItem.setValueField(valueField);
                    }
                    ((ComboBoxItem) formItem).setDefaultToFirstOption(true);
                    setupField(null, null, sections, sectionNames, field, group, groupOrder, formItem, displayFormItem);
                } else {
                    /*
                        Check to make sure this field exists for this record. This could be a polymorphic type
                        for this record that does not have this field. This is normally taken care of during inspection
                        for a datasource ceiling entity. However, occasion can arise where there is a ManyToOne
                        relationship on an entity to another entity with a polymorphic type (e.g. an OrderItem
                        with a relationship to polymorphic product). In this instance, fields exposed for this
                        related entity should be hidden or shown appropriately in the form based on the the
                        type of this related entity. This only comes into play when the populateToOne property of
                        the @AdminPresentationClass annotation has been set to true.
                     */
                    boolean isFieldAvailableForRecord = false;
                    if (currentRecord != null) {
                        int pos = Arrays.binarySearch(recordAttributes, field.getName());
                        isFieldAvailableForRecord = pos >= 0;
                    } else {
                        isFieldAvailableForRecord = true;
                    }
                    if (isFieldAvailableForRecord && (!fieldType.equals(SupportedFieldType.ID.toString()) || (fieldType.equals(SupportedFieldType.ID.toString()) && showId))) {
                        Boolean largeEntry = field.getAttributeAsBoolean("largeEntry");
                        if (largeEntry == null) {
                            largeEntry = false;
                        }
                        formItem = buildField(dataSource, field, fieldType, largeEntry, form);
                        displayFormItem = buildDisplayField(field, fieldType);
                        setupField(null, null, sections, sectionNames, field, group, groupOrder, formItem, displayFormItem);
                    }
                }
            }
        }
        
        groupFields(form, sections, sectionNames, sectionCollapsed);
    }

    protected static void groupFields(DynamicForm form, Map<String, List<FormItem>> sections, final Map<String, Integer> sectionNames, Map<String, Boolean> sectionCollapsed) {
        if (sections.isEmpty()) {
            GWT.log("There were no fields available to show in the form. Rendering a blank DynamicForm.");
            return;
        }
        if (sections.size() > 0) {
            int j=0;
            List<FormItem> allItems = new ArrayList<FormItem>();
            String[] groups = new String[sectionNames.size()];
            groups = sectionNames.keySet().toArray(groups);
            Arrays.sort(groups, new Comparator<String>() {
                @Override
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
                HeaderItem headerItem = new HeaderItem();
                headerItem.setDefaultValue(group);
                List<FormItem> formItems = sections.get(group);
                String[] ids = new String[formItems.size()];
                int x=0;
                Boolean containsRichTextItem = null;
                for (FormItem formItem : formItems) {
                    ids[x] = formItem.getName();
                    x++;
                    if (containsRichTextItem == null && formItem instanceof RichTextCanvasItem) {
                        containsRichTextItem = true;
                    }
                }
                if (containsRichTextItem == null) {
                    containsRichTextItem = false;
                }

                allItems.add(headerItem);
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
        formItem.setWrapTitle(false);
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
            String className = formItem.getClass().getName();
            if (
                className.equals(FloatItem.class.getName()) ||
                className.equals(TextItem.class.getName()) ||
                className.equals(IntegerItem.class.getName()) ||
                className.equals(TextAreaItem.class.getName())
            ) {
                formItem.setAttribute("readOnly",!canEdit);
            } else {
                formItem.setDisabled(!canEdit);
            }
        }
        if (!field.getCanEdit()) {
            String className = formItem.getClass().getName();
            if (
                className.equals(FloatItem.class.getName()) ||
                className.equals(TextItem.class.getName()) ||
                className.equals(IntegerItem.class.getName()) ||
                className.equals(TextAreaItem.class.getName())
            ) {
                formItem.setAttribute("readOnly",true);
            } else {
                formItem.setDisabled(true);
            };
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
                            @Override
                            public void onFilterData(FetchDataEvent event) {
                                otherItem.setValue(formItem.getValue());
                            }
                        });
                        ((PasswordItem) otherItem).setLength(((PasswordItem) formItem).getLength());
                        otherItem.setName(otherFieldName);
                        String title = field.getAttribute("friendlyName") +" Repeat";
                        //check to see if we have an i18N version of the new title
                        try {
                            String val = BLCMain.getMessageManager().getString(title);
                            if (val != null) {
                                title = val;
                                break;
                            }
                        } catch (MissingResourceException e) {
                            //do nothing
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
        /*case UPLOAD:
            displayFormItem = new CanvasItem();
            ((CanvasItem) displayFormItem).setCanvas(new UploadStatusProgress(100, 20));
            displayFormItem.setName("__display_"+field.getName());
            displayFormItem.setShowTitle(false);
            break;*/
        }
        return displayFormItem;
    }

    protected static FormItem buildField(final DataSource dataSource, DataSourceField field, String fieldType, Boolean largeEntry, DynamicForm form) {
        final FormItem formItem;
        switch(SupportedFieldType.valueOf(fieldType)){
        case BOOLEAN:
            formItem = new BooleanItem();
            formItem.setValueFormatter(new FormItemValueFormatter() {
                @Override
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
                @Override
                public String formatValue(Object value, Record record, DynamicForm form, FormItem item) {
                    return value==null?"":NumberFormat.getFormat("0.00").format(NumberFormat.getFormat("0.00").parse(String.valueOf(value)));
                }
            });
            break;
        case FOREIGN_KEY:
            formItem = new SearchFormItem();
            formItem.setValueFormatter(new FormItemValueFormatter() {
                @Override
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
            formItem.setValueFormatter(new FormItemValueFormatter() {
                @Override
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
        case EXPLICIT_ENUMERATION:
            formItem = new SelectItem();
            LinkedHashMap<String,String> valueMap2 = new LinkedHashMap<String,String>();
            String[][] enumerationValues2 = (String[][]) field.getAttributeAsObject("enumerationValues");
            for (int j=0; j<enumerationValues2.length; j++) {
                valueMap2.put(enumerationValues2[j][0], enumerationValues2[j][1]);
            }
            formItem.setValueMap(valueMap2);
            break;
        case EMPTY_ENUMERATION:
            formItem = new SelectItem();
            break;
        case ID:
            formItem = new TextItem();
            ((TextItem)formItem).setLength(field.getLength());
            formItem.setValueFormatter(new FormItemValueFormatter() {
                @Override
                public String formatValue(Object value, Record record, DynamicForm form, FormItem item) {
                    return value==null?"":((DynamicEntityDataSource) dataSource).stripDuplicateAllowSpecialCharacters(String.valueOf(value));
                }
            });
            break;
        case PASSWORD:
            formItem = new PasswordItem();
            ((PasswordItem) formItem).setLength(field.getLength());
            break;
        case HTML:
            formItem = new BLCRichTextItem();
            formItem.setHeight(500);         
           break;
        case HTML_BASIC:
            formItem = new BLCRichTextItem();
            formItem.setHeight(150);         
           break;
        case UPLOAD:
            formItem = new UploadItem();
            break;
        case HIDDEN:
            formItem = new HiddenItem();
            break;
        case ASSET:
            formItem = new AssetItem();
            break;
        case ASSET_URL:
            formItem = new EditableSearchFormItem();
            break;
        default:
            if (!largeEntry) {
                formItem = new TextItem();
                ((TextItem)formItem).setLength(field.getLength());
            } else {
                formItem = new TextAreaItem();
                ((TextAreaItem)formItem).setLength(field.getLength());
                formItem.setHeight(70);
                formItem.setWidth("400");
            }
            break;
        }
        return formItem;
    }

}
