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

package org.broadleafcommerce.openadmin.client.datasource.dynamic;

import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.Hilite;
import com.smartgwt.client.widgets.grid.ColumnTree;
import com.smartgwt.client.widgets.tree.TreeGridField;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.service.DynamicEntityServiceAsync;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author jfischer
 *
 */
public class ColumnTreeDataSource extends DynamicEntityDataSource {

    public static Hilite[] hilites = new Hilite[] {
        new Hilite() {{
            setCssText("background-color: #fffedc;");
            setId("listGridDirtyPropertyHilite");
        }}
    };

	protected ColumnTree associatedGrid;

	/**
	 * @param name
	 * @param persistencePerspective
	 * @param service
	 * @param modules
	 */
	public ColumnTreeDataSource(String name, PersistencePerspective persistencePerspective, DynamicEntityServiceAsync service, DataSourceModule[] modules) {
		super(name, persistencePerspective, service, modules);
	}

    public ColumnTree getAssociatedGrid() {
		return associatedGrid;
	}

	public void setAssociatedGrid(ColumnTree associatedGrid) {
		this.associatedGrid = associatedGrid;
	}

	public void loadAssociatedGridBasedOnRelationship(String relationshipValue, DSCallback dsCallback) {
		Criteria criteria = createRelationshipCriteria(relationshipValue);
		if (dsCallback != null) {
			getAssociatedGrid().fetchData(criteria, dsCallback);
		} else {
			getAssociatedGrid().fetchData(criteria);
		}
	}

    public void setupGridFields() {
        setupGridFields(new String[]{});
    }

    public void setupGridFields(final String[] fieldNames) {
        Boolean[] canEdit = new Boolean[fieldNames.length];
        for (int j=0;j<fieldNames.length;j++) {
            canEdit[j] = false;
        }
        setupGridFields(fieldNames, canEdit, "*", "*");
    }

	public void setupGridFields(String[] fieldNames, Boolean[] canEdit, String initialFieldWidth, String otherFieldWidth) {
        if (fieldNames.length != canEdit.length) {
            throw new IllegalArgumentException("The fieldNames and canEdit array parameters must be of equal length");
        }
		if (fieldNames != null && fieldNames.length > 0) {
			resetProminenceOnly(fieldNames);
		}
		
		String[] sortedFieldNames = new String[fieldNames.length];
		for (int j=0;j<fieldNames.length;j++) {
			sortedFieldNames[j] = fieldNames[j];
		}
		Arrays.sort(sortedFieldNames);
		
		DataSourceField[] fields = getFields();
        TreeGridField[] gridFields = new TreeGridField[fields.length];
        int j = 0;
        List<DataSourceField> prominentFields = new ArrayList<DataSourceField>();
        for (DataSourceField field : fields) {
        	if (field.getAttributeAsBoolean("prominent")) {
        		prominentFields.add(field);
        	}
        }
        int availableSlots = fieldNames==null?4:fieldNames.length;
        if (availableSlots == 0 && prominentFields.size() == 0) {
            throw new RuntimeException("You have explicitly specified zero length array for fieldsNames and have no fields defined as prominent via AdminPresentation annotation. Cannot proceed with a column tree without defining at least one column to show.");
        }
        for (DataSourceField field : prominentFields) {
        	String columnWidth = field.getAttribute("columnWidth");
        	gridFields[j] = new TreeGridField(field.getName(), field.getTitle(), j==0?200:150);
        	if (j == 0) {
        		if (fieldNames == null || fieldNames.length == 0) {
        			gridFields[j].setFrozen(true);
        		}
        		if (columnWidth != null) {
        			gridFields[j].setWidth(columnWidth);
        		} else if (initialFieldWidth != null) {
        			gridFields[j].setWidth(initialFieldWidth);
        		}
        	} else {
        		if (columnWidth != null) {
        			gridFields[j].setWidth(columnWidth);
        		} else {
        			gridFields[j].setWidth(otherFieldWidth);
        		}
        	}
        	gridFields[j].setHidden(false);
        	int pos = Arrays.binarySearch(sortedFieldNames, field.getName());
        	if (pos >= 0) {
        		gridFields[j].setCanEdit(canEdit[pos]);
        	}
        	j++;
        	availableSlots--;
        }
        for (DataSourceField field : fields) {
        	if (!prominentFields.contains(field)) {
        		String columnWidth = field.getAttribute("columnWidth");
        		gridFields[j] = new TreeGridField(field.getName(), field.getTitle(), j==0?200:150);
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
	            		if (columnWidth != null) {
	            			gridFields[j].setWidth(columnWidth);
	            		} else if (initialFieldWidth != null) {
	            			gridFields[j].setWidth(initialFieldWidth);
	            		}
	            	} else {
	            		if (columnWidth != null) {
	            			gridFields[j].setWidth(columnWidth);
	            		} else {
	            			gridFields[j].setWidth(otherFieldWidth);
	            		}
	            	}
	        		int pos = Arrays.binarySearch(sortedFieldNames, field.getName());
	            	if (pos >= 0) {
	            		gridFields[j].setCanEdit(canEdit[pos]);
	            	}
	        		availableSlots--;
	        	}
        		j++;
        	}
        }
        getAssociatedGrid().setFields(gridFields);
        getAssociatedGrid().setHilites(hilites);
	}

}
