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

package org.broadleafcommerce.openadmin.client.datasource.dynamic;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.openadmin.client.dto.MergedPropertyType;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.service.DynamicEntityServiceAsync;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 
 * @author jfischer
 *
 */
public class ComplexValueMapStructureDataSource extends ListGridDataSource {
	
	protected LinkedHashMap<String, String> keyMap;
    protected DataSource optionDataSource;
    protected String displayField;
    protected String valueField;

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

    public ComplexValueMapStructureDataSource(String name, PersistencePerspective persistencePerspective, DynamicEntityServiceAsync service, DataSourceModule[] modules, DataSource optionDataSource, String displayField, String valueField) {
		super(name, persistencePerspective, service, modules);
		this.optionDataSource = optionDataSource;
        this.displayField = displayField;
        this.valueField = valueField;
	}
	
	@Override
	public void setupGridFields(String[] fieldNames, Boolean[] canEdit) {
		if (fieldNames.length > 0) {
			resetPermanentFieldVisibility(fieldNames);
		}
		DataSourceField[] fields = getFields();
		ListGridField[] gridFields = new ListGridField[fields.length];
        int j = 0;
        List<DataSourceField> prominentFields = new ArrayList<DataSourceField>();
        for (DataSourceField field : fields) {
        	if (field.getAttributeAsBoolean("prominent")) {
        		prominentFields.add(field);
        	}
        }
        int availableSlots = 4;
        for (DataSourceField field : prominentFields) {
        	gridFields[j] = new ListGridField(field.getName(), field.getTitle(), j==0?200:150);
        	if (MergedPropertyType.MAPSTRUCTUREKEY.toString().equals(field.getAttribute("mergedPropertyType"))) {
        		ComboBoxItem selectItem = new ComboBoxItem();
        		//selectItem.setMultiple(false);
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
        	if (j == 0) {
        		if (fieldNames == null || fieldNames.length == 0) {
        			gridFields[j].setFrozen(true);
        		}
        	}
        	gridFields[j].setHidden(false);
        	gridFields[j].setWidth("*");
        	int pos = Arrays.binarySearch(fieldNames, field.getName());
        	if (pos >= 0) {
        		gridFields[j].setCanEdit(canEdit[pos]);
        	}
        	j++;
        	availableSlots--;
        }
        for (DataSourceField field : fields) {
        	if (!prominentFields.contains(field)) {
        		gridFields[j] = new ListGridField(field.getName(), field.getTitle(), j==0?200:150);
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
	        		if (j == 0) {
	        			if (fieldNames == null || fieldNames.length == 0) {
	            			gridFields[j].setFrozen(true);
	            		}
		        	}
	        		gridFields[j].setWidth("*");
	        		int pos = Arrays.binarySearch(fieldNames, field.getName());
	            	if (pos >= 0) {
	            		gridFields[j].setCanEdit(canEdit[pos]);
	            	}
	        		availableSlots--;
	        	}
        		j++;
        	}
        }
        ((ListGrid) getAssociatedGrid()).setFields(gridFields);
        if (fieldNames != null && fieldNames.length > 0) {
        	int pos = 0;
        	for (String fieldName : fieldNames) {
        		int originalPos = ((ListGrid) getAssociatedGrid()).getFieldNum(fieldName);
        		((ListGrid) getAssociatedGrid()).reorderField(originalPos, pos);
        		pos++;
        	}
        }
        getAssociatedGrid().setHilites(hilites);
	}
}
