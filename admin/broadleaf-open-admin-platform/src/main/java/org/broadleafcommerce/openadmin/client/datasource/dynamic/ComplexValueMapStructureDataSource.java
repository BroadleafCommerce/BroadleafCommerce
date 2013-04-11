/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.client.datasource.dynamic;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.types.FieldType;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.MiniDateRangeItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.openadmin.client.dto.MergedPropertyType;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.service.DynamicEntityServiceAsync;
import org.broadleafcommerce.openadmin.client.setup.PresenterSequenceSetupManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 
 * @author jfischer
 *
 */
public class ComplexValueMapStructureDataSource extends CustomCriteriaListGridDataSource {
    
    protected LinkedHashMap<String, String> keyMap;
    protected String displayField;
    protected String valueField;
    protected String lookupDataSourcename;
    protected PresenterSequenceSetupManager presenterSequenceSetupManager;

    /**
     * @param name
     * @param persistencePerspective
     * @param service
     * @param modules
     */
    public ComplexValueMapStructureDataSource(String name, PersistencePerspective persistencePerspective, DynamicEntityServiceAsync service, DataSourceModule[] modules, LinkedHashMap<String, String> keyMap) {
        super(name, persistencePerspective, service, modules);
        this.keyMap = keyMap;
    }

    public ComplexValueMapStructureDataSource(String name, PersistencePerspective persistencePerspective, DynamicEntityServiceAsync service, DataSourceModule[] modules, PresenterSequenceSetupManager presenterSequenceSetupManager, String lookupDataSourceName, String displayField, String valueField) {
        super(name, persistencePerspective, service, modules);
        this.presenterSequenceSetupManager = presenterSequenceSetupManager;
        this.lookupDataSourcename = lookupDataSourceName;
        this.displayField = displayField;
        this.valueField = valueField;
    }
    
    @Override
    public String[] setupGridFields(String[] fieldNames, Boolean[] canEdit) {
        if (fieldNames.length > 0) {
            resetPermanentFieldVisibility(fieldNames);
        }
        DataSourceField[] fields = getFields();
        ListGridField[] gridFields = new ListGridField[fields.length];
        int j = 0;
        List<DataSourceField> prominentFields = new ArrayList<DataSourceField>();
        String keyProperty = null;
        for (DataSourceField field : fields) {
            if (field.getAttributeAsBoolean("prominent") && !prominentFields.contains(field)) {
                prominentFields.add(field);
            }
            if (MergedPropertyType.MAPSTRUCTUREKEY.toString().equals(field.getAttribute("mergedPropertyType")) && !prominentFields.contains(field)) {
                permanentlyShowFields(field.getName());
                setProminent(field.getName());
                prominentFields.add(field);
                keyProperty = field.getName();
            }
        }
        int availableSlots = 4;
        for (DataSourceField field : prominentFields) {
            gridFields[j] = new ListGridField(field.getName(), field.getTitle(), j==0?200:150);
            if (FieldType.DATE == field.getType() || FieldType.DATETIME == field.getType()) {
                gridFields[j].setEditorType(new MiniDateRangeItem());
            }
            if (MergedPropertyType.MAPSTRUCTUREKEY.toString().equals(field.getAttribute("mergedPropertyType"))) {
                ComboBoxItem selectItem = new ComboBoxItem();
                //selectItem.setMultiple(false);
                DataSource optionDataSource = null;
                if (presenterSequenceSetupManager != null && lookupDataSourcename != null) {
                    optionDataSource = presenterSequenceSetupManager.getDataSource(lookupDataSourcename);
                }
                if (keyMap == null && optionDataSource == null) {
                    throw new RuntimeException("Must supply either a key map or option data source to support the key values for this map structure.");
                }
                if (keyMap != null) {
                    selectItem.setValueMap(keyMap);
                } else {
                    selectItem.setOptionDataSource(optionDataSource);
                    selectItem.setDisplayField(displayField);
                    selectItem.setValueField(valueField);
                }
                selectItem.setDefaultToFirstOption(true);
                selectItem.setAutoFetchData(false);
                gridFields[j].setEditorType(selectItem);
            }
            gridFields[j].setHidden(false);
            gridFields[j].setWidth("*");
            int pos = Arrays.binarySearch(fieldNames, field.getName());
            if (pos >= 0) {
                gridFields[j].setCanEdit(canEdit[pos]);
            }
            setupDecimalFormatters(gridFields[j], field);
            j++;
            availableSlots--;
        }
        for (DataSourceField field : fields) {
            if (!prominentFields.contains(field)) {
                gridFields[j] = new ListGridField(field.getName(), field.getTitle(), j==0?200:150);
                if (FieldType.DATE == field.getType() || FieldType.DATETIME == field.getType()) {
                    gridFields[j].setEditorType(new MiniDateRangeItem());
                }
                if (MergedPropertyType.MAPSTRUCTUREKEY.toString().equals(field.getAttribute("mergedPropertyType"))) {
                    SelectItem selectItem = new SelectItem();
                    selectItem.setMultiple(false);
                    selectItem.setValueMap(keyMap);
                    selectItem.setDefaultToFirstOption(true);
                    selectItem.setAutoFetchData(false);
                    gridFields[j].setEditorType(selectItem);
                }
                if (field.getAttributeAsBoolean("permanentlyHidden")) {
                    gridFields[j].setHidden(true);
                    gridFields[j].setCanHide(false);
                } else if (field.getAttributeAsBoolean("hidden")) {
                    gridFields[j].setHidden(true);
                } else if (availableSlots <= 0) {
                    gridFields[j].setHidden(true);
                } else {
                    gridFields[j].setWidth("*");
                    int pos = Arrays.binarySearch(fieldNames, field.getName());
                    if (pos >= 0) {
                        gridFields[j].setCanEdit(canEdit[pos]);
                    }
                    availableSlots--;
                }
                setupDecimalFormatters(gridFields[j], field);
                j++;
            }
        }
        final String finalKeyProperty = keyProperty;
        //sort so the key field appears first
        if (fieldNames == null || fieldNames.length == 0) {
            Arrays.sort(gridFields, new Comparator<ListGridField>() {
                @Override
                public int compare(ListGridField o1, ListGridField o2) {
                    if (finalKeyProperty != null) {
                        if (o1.getName().equals(o2.getName())) {
                            return 0;
                        } else if (o1.getName().equals(finalKeyProperty)) {
                            return -1;
                        } else if (o2.getName().equals(finalKeyProperty)) {
                            return 1;
                        } else {
                            return 0;
                        }
                    } else {
                        return 0;
                    }
                }
            });
        }
        ((ListGrid) getAssociatedGrid()).setFields(gridFields);
        if (fieldNames != null && fieldNames.length > 0) {
            int pos = 0;
            for (String fieldName : fieldNames) {
                int originalPos = ((ListGrid) getAssociatedGrid()).getFieldNum(fieldName);
                ((ListGrid) getAssociatedGrid()).reorderField(originalPos, pos);
                pos++;
            }
        } else {
            fieldNames = new String[gridFields.length];
            for (int x=0;x<gridFields.length;x++){
                fieldNames[x] = gridFields[x].getName();
                getField(gridFields[x].getName()).setHidden(false);
            }
        }
        getAssociatedGrid().setHilites(hilites);

        return fieldNames;
    }
}
