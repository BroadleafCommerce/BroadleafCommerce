/*
 * Copyright 2008-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.client.view.dynamic.form;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.FetchDataEvent;
import com.smartgwt.client.widgets.events.FetchDataHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.FormItemValueFormatter;
import com.smartgwt.client.widgets.form.fields.*;
import com.smartgwt.client.widgets.form.fields.events.IconClickEvent;
import com.smartgwt.client.widgets.form.fields.events.IconClickHandler;
import com.smartgwt.client.widgets.form.validator.Validator;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import org.broadleafcommerce.common.presentation.client.AddMethodType;
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.openadmin.client.dto.*;
import org.broadleafcommerce.openadmin.client.dto.visitor.MetadataVisitorAdapter;
import org.broadleafcommerce.openadmin.client.presenter.entity.DynamicEntityPresenter;
import org.broadleafcommerce.openadmin.client.presenter.entity.SubPresentable;
import org.broadleafcommerce.openadmin.client.presenter.structure.*;
import org.broadleafcommerce.openadmin.client.security.SecurityManager;
import org.broadleafcommerce.openadmin.client.view.dynamic.dialog.EntitySearchDialog;
import org.broadleafcommerce.openadmin.client.view.dynamic.dialog.MapStructureEntityEditDialog;
import org.broadleafcommerce.openadmin.client.view.dynamic.grid.GridStructureView;

import java.util.*;

/**
 * 
 * @author jfischer
 *
 */
public class FormBuilder {

    public static Layout findMemberById(Layout parent, String id) {
        Layout result = (Layout) parent.getMember(id);
        if (result == null) {
            check: {
                for (Canvas member : parent.getMembers()) {
                    if (member instanceof Layout) {
                        result = findMemberById((Layout) member, id);
                        if (result != null) {
                            break check;
                        }
                    }
                    if (member instanceof TabSet) {
                        for (Tab tab : ((TabSet) member).getTabs()) {
                            if (tab.getPane().getID().equals(id)) {
                                result = (Layout) tab.getPane();
                                break check;
                            }
                            result = findMemberById((Layout) tab.getPane(), id);
                            if (result != null) {
                                break check;
                            }
                        }
                    }
                }
            }
        }

        return result;
    }
    public static TabSet findTabSetById(Layout parent, String id) {
        TabSet retLayout = (TabSet) parent.getMember(id);
        Layout result;
        if (retLayout == null) {
            check: {
                for (Canvas member : parent.getMembers()) {
                    if (member instanceof Layout) {
                        result = findMemberById((Layout) member, id);
                        if (result != null) {
                            break check;
                        }
                    }
                    if (member instanceof TabSet) {
                        if(member.getID().equals(id)) {
                            return (TabSet) member;
                        }
                        for (Tab tab : ((TabSet) member).getTabs()) {
                            if (tab.getPane().getID().equals(id)) {
                                result = (Layout) tab.getPane();
                                break check;
                            }
                            result = findMemberById((Layout) tab.getPane(), id);
                            if (result != null) {
                                break check;
                            }
                        }
                    }
                }
            }
        }

        return retLayout;
    }

    public static void buildAdvancedCollectionForm(final DataSource dataSource, final DataSource lookupDataSource, CollectionMetadata metadata, String propertyName, final DynamicEntityPresenter presenter) {
        final Layout destination;
        if (metadata.getTargetElementId() != null && metadata.getTargetElementId().length() > 0) {
            destination = findMemberById((Layout) presenter.getDisplay(), metadata.getTargetElementId());
        } else {
            destination = (Layout) presenter.getDisplay().getDynamicFormDisplay().getFormOnlyDisplay();
        }

        final String viewTitle;
        if (metadata.getFriendlyName() == null || metadata.getFriendlyName().length() == 0) {
            viewTitle = propertyName;
        } else {
            String temp = BLCMain.getMessageManager().getString(metadata.getFriendlyName());
            if (temp == null) {
                temp = metadata.getFriendlyName();
            }
            viewTitle = temp;
        }

        //make the grid readonly if it fails a security check
        if (metadata.getSecurityLevel() != null && !"".equals(metadata.getSecurityLevel())){
            org.broadleafcommerce.openadmin.client.security.SecurityManager.getInstance().registerField(String.valueOf(metadata.hashCode()), metadata.getSecurityLevel());
            Boolean shouldLoad = org.broadleafcommerce.openadmin.client.security.SecurityManager.getInstance().isUserAuthorizedToEditField(String.valueOf(metadata.hashCode()));
            if (!shouldLoad) {
                metadata.setMutable(false);
            }
        }

        final String prefix;
        if (propertyName.contains(".")) {
            prefix = propertyName.substring(0, propertyName.lastIndexOf("."));
        } else {
            prefix = "";
        }

        metadata.accept(new MetadataVisitorAdapter() {
            @Override
            public void visit(BasicCollectionMetadata metadata) {
                GridStructureView advancedCollectionView = new GridStructureView(viewTitle, false, true);
                destination.addMember(advancedCollectionView);
                SubPresentable subPresentable;
                if (metadata.getAddMethodType() == AddMethodType.PERSIST) {
                    subPresentable = new CreateBasedListStructurePresenter(prefix, advancedCollectionView, metadata.getAvailableToTypes(), viewTitle, new HashMap<String, Object>());
                } else {
                    subPresentable = new SimpleSearchListPresenter(prefix, advancedCollectionView, new EntitySearchDialog((ListGridDataSource)lookupDataSource, true), metadata.getAvailableToTypes(), viewTitle);
                }
                subPresentable.setDataSource((ListGridDataSource) dataSource, new String[]{}, new Boolean[]{});
                subPresentable.setReadOnly(!metadata.isMutable());
                ((GridStructureView) subPresentable.getDisplay()).getToolBar().disable();
                presenter.setSubPresentable(dataSource.getDataURL(), subPresentable);
            }

            @Override
            public void visit(AdornedTargetCollectionMetadata metadata) {
                String sortField = ((AdornedTargetList) ((DynamicEntityDataSource) dataSource).getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.ADORNEDTARGETLIST)).getSortField();
                GridStructureView advancedCollectionView = new GridStructureView(viewTitle, sortField!=null&&sortField.length()>0, true);
                destination.addMember(advancedCollectionView);
                List<String> prominentNames = new ArrayList<String>();
                for (DataSourceField field : lookupDataSource.getFields()) {
                    if (field.getAttributeAsBoolean("prominent") && !field.getAttributeAsBoolean("permanentlyHidden")) {
                        prominentNames.add(field.getName());
                    }
                }
                ((ListGridDataSource) lookupDataSource).resetPermanentFieldVisibility(prominentNames.toArray(new String[prominentNames.size()]));
                EntitySearchDialog searchView = new EntitySearchDialog((ListGridDataSource) lookupDataSource, true);
                SubPresentable subPresentable;
                if (metadata.isIgnoreAdornedProperties() || metadata.getMaintainedAdornedTargetFields().length == 0) {
                    subPresentable = new SimpleSearchListPresenter(prefix, advancedCollectionView, searchView, metadata.getAvailableToTypes(), viewTitle);
                } else {
                    subPresentable = new EditableAdornedTargetListPresenter(prefix, advancedCollectionView, searchView, metadata.getAvailableToTypes(), viewTitle, viewTitle, metadata.getMaintainedAdornedTargetFields());
                }
                Boolean[] edits = new Boolean[metadata.getGridVisibleFields().length];
                for (int j=0;j<edits.length;j++) {
                    edits[j] = false;
                }
                subPresentable.setDataSource((ListGridDataSource) dataSource, metadata.getGridVisibleFields(), edits);
                subPresentable.setReadOnly(!metadata.isMutable());
                ((GridStructureView) subPresentable.getDisplay()).getToolBar().disable();
                presenter.setSubPresentable(dataSource.getDataURL(), subPresentable);
            }

            @Override
            public void visit(MapMetadata metadata) {
                GridStructureView advancedCollectionView = new GridStructureView(viewTitle, false, true);
                destination.addMember(advancedCollectionView);
                SubPresentable subPresentable;
                if (metadata.isSimpleValue()) {
                    subPresentable = new SimpleMapStructurePresenter(prefix, advancedCollectionView, metadata.getAvailableToTypes(), null);
                } else {
                    MapStructureEntityEditDialog mapEntityAdd;
                    if (lookupDataSource != null) {
                        mapEntityAdd = new MapStructureEntityEditDialog((MapStructure) metadata.getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.MAPSTRUCTURE), lookupDataSource, metadata.getMapKeyOptionEntityDisplayField(), metadata.getMapKeyOptionEntityValueField());
                    } else {
                        LinkedHashMap<String, String> keys = new LinkedHashMap<String, String>();
                        for (String[] key : metadata.getKeys()) {
                            String temp = BLCMain.getMessageManager().getString(key[1]);
                            if (temp == null) {
                                temp = key[1];
                            }
                            keys.put(key[0], temp);
                        }
                        mapEntityAdd = new MapStructureEntityEditDialog((MapStructure) metadata.getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.MAPSTRUCTURE), keys);
                    }
                    if (metadata.getMediaField() != null && metadata.getMediaField().length() > 0) {
                        mapEntityAdd.setShowMedia(true);
                        mapEntityAdd.setMediaField(metadata.getMediaField());
                    } else {
                        mapEntityAdd.setShowMedia(false);
                    }
                    subPresentable = new MapStructurePresenter(prefix, advancedCollectionView, mapEntityAdd, viewTitle, null);
                }
                subPresentable.setDataSource((ListGridDataSource) dataSource, new String[]{}, new Boolean[]{});
                subPresentable.setReadOnly(!metadata.isMutable());
                ((GridStructureView) subPresentable.getDisplay()).getToolBar().disable();
                presenter.setSubPresentable(dataSource.getDataURL(), subPresentable);
            }
        });
    }

    public static void buildAdvancedCollectionForm(DataSource dataSource, CollectionMetadata metadata, String propertyName, DynamicEntityPresenter presenter) {
        buildAdvancedCollectionForm(dataSource, null, metadata, propertyName, presenter);
    }

    public static void buildForm(final DataSource dataSource, DynamicForm form, Boolean showId, Record currentRecord) {
        buildForm(dataSource, form, null, null, showId, currentRecord);
    }
    
    public static void buildForm(final DataSource dataSource, DynamicForm form, Boolean showDisabledState, Boolean canEdit, Boolean showId, Record currentRecord) {
        String[] recordAttributes = currentRecord == null?null:currentRecord.getAttributes();
        if (recordAttributes != null) {
            Arrays.sort(recordAttributes);
        }
        form.setDataSource(dataSource);
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
            Boolean tempFormHidden = field.getAttribute("tempFormHidden") != null && Boolean.parseBoolean(field.getAttribute("tempFormHidden"));
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
            if (fieldType != null && (!field.getHidden() || enumVal == FormHiddenEnum.VISIBLE) && enumVal != FormHiddenEnum.HIDDEN && isFieldAvailableForRecord && !tempFormHidden) {
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
            field.setAttribute("tempFormHidden", false);
            
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
                    } else if (o1.equals("General") && (sectionNames.get(o1) == null || sectionNames.get(o1) == 99999)) {
                        return 1;
                    } else if (o2.equals("General") && (sectionNames.get(o2) == null || sectionNames.get(o2) == 99999)) {
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
                headerItem.setShowTitle(false);
                headerItem.setColSpan(2);
                List<FormItem> formItems = sections.get(group);
                String[] ids = new String[formItems.size()];
                int x=0;
             
                for (FormItem formItem : formItems) {
                    ids[x] = formItem.getName();
                    x++;
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
        formItem.setPrompt(field.getPrompt());

        String helpText = field.getAttribute("helpText");
        if (helpText != null && helpText.length() > 0) {
            final String text = helpText;
            FormItemIcon icon = new FormItemIcon();
            icon.setSrc(GWT.getModuleBaseURL()+"admin/images/button/help.png");
            formItem.setIcons(icon);
            formItem.addIconClickHandler(new IconClickHandler() {
                @Override
                public void onIconClick(IconClickEvent event) {
                    SC.say(text);
                }
            });
        }
        String hint = field.getAttributeAsString("hint");
        formItem.setHint(hint);

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
                className.equals(BLCRichTextItem.class.getName()) ||
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
                className.equals(BLCRichTextItem.class.getName()) ||
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
            displayFormItem.setTitle(field.getTitle());
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
                        String val = BLCMain.getMessageManager().getString(title);
                        if (val != null) {
                            title = val;
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
        case ADDITIONAL_FOREIGN_KEY:
            displayFormItem = new SearchFormItem();
            displayFormItem.setWidth(235);
            displayFormItem.setName("__display_"+field.getName());
            break;
        }
        return displayFormItem;
    }

    public static FormItem buildField(final DataSource dataSource, DataSourceField field, String fieldType, Boolean largeEntry, DynamicForm form) {
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
            formItem.setAttribute("type", "localDecimal");
            break;
        case EMAIL:
            formItem = new TextItem();
            formItem.setWidth(220);
            ((TextItem)formItem).setLength(field.getLength());
            break;
        case INTEGER:
            formItem = new IntegerItem();
            break;
        case MONEY:
            formItem = new FloatItem();
            formItem.setAttribute("type", "localMoneyDecimal");
            break;
        case FOREIGN_KEY:
        case ADDITIONAL_FOREIGN_KEY:
            formItem = new HiddenItem();
            break;
        case BROADLEAF_ENUMERATION:
        case EXPLICIT_ENUMERATION:
        case DATA_DRIVEN_ENUMERATION:
            if (field.getAttributeAsBoolean("canEditEnumeration")) {
                formItem = new ComboBoxItem();
                ((ComboBoxItem)formItem).setAddUnknownValues(true);
            } else {
                formItem = new SelectItem();
            }

            formItem.setHeight(25);
            formItem.setWidth(280);
            formItem.setPickerIconWidth(22);

            LinkedHashMap<String,String> valueMap = new LinkedHashMap<String,String>();
            String[][] enumerationValues = (String[][]) field.getAttributeAsObject("enumerationValues");
            for (String[] enumerationValue : enumerationValues) {
                valueMap.put(enumerationValue[0], enumerationValue[1]);
            }
            formItem.setValueMap(valueMap);

            break;
        case EMPTY_ENUMERATION:
            formItem = new SelectItem();
            break;
        case ID:
            formItem = new StaticTextItem();
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
            formItem.setWidth(220);
            ((PasswordItem) formItem).setLength(field.getLength());
            break;
        case HTML:
            formItem = new BLCRichTextItem();
           break;
        case HTML_BASIC:
            formItem = new BLCRichTextItem();
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
                formItem.setWidth(220);
                ((TextItem)formItem).setLength(field.getLength());
            } else {
                formItem = new TextAreaItem();
                ((TextAreaItem)formItem).setLength(field.getLength());
                formItem.setHeight(70);
                formItem.setWidth(440);
            }
            break;
        }
        return formItem;
    }

}
