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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.i18n.client.NumberFormat;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.widgets.DataBoundComponent;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.service.DynamicEntityServiceAsync;

/**
 * 
 * @author jfischer
 *
 */
public class ListGridDataSource extends PresentationLayerAssociatedDataSource {

    /**
     * @param name
     * @param persistencePerspective
     * @param service
     * @param modules
     */
    public ListGridDataSource(String name, PersistencePerspective persistencePerspective, DynamicEntityServiceAsync service, DataSourceModule[] modules) {
        super(name, persistencePerspective, service, modules);
    }

    public void setupGridFields(final String[] fieldNames) {
        Boolean[] canEdit = new Boolean[fieldNames.length];
        for (int j=0;j<fieldNames.length;j++) {
            canEdit[j] = false;
        }
        setupGridFields(fieldNames, canEdit);
    }

    @Override
    public void setAssociatedGrid(DataBoundComponent associatedGrid) {
        super.setAssociatedGrid(associatedGrid);
        ((ListGrid) this.associatedGrid).setCanAutoFitFields(false);
    }

    public void setupGridFields(final String[] fieldNames, final Boolean[] canEdit) {
        if (fieldNames.length != canEdit.length) {
            throw new IllegalArgumentException("The fieldNames and canEdit array parameters must be of equal length");
        }
        if (fieldNames.length > 0) {
            resetProminenceOnly(fieldNames);
        }
        
        String[] sortedFieldNames = new String[fieldNames.length];
        for (int j=0;j<fieldNames.length;j++) {
            sortedFieldNames[j] = fieldNames[j];
        }
        Arrays.sort(sortedFieldNames);
        
        DataSourceField[] fields = getFields();
        ListGridField[] gridFields = new ListGridField[fields.length];
        int j = 0;
        List<DataSourceField> prominentFields = new ArrayList<DataSourceField>();
        for (DataSourceField field : fields) {
            if (field.getAttributeAsBoolean("prominent") && !field.getAttributeAsBoolean("permanentlyHidden")) {
                prominentFields.add(field);
            }
        }
        int availableSlots = fieldNames.length==0?4:fieldNames.length;
        for (DataSourceField field : prominentFields) {
            String columnWidth = field.getAttribute("columnWidth");
            gridFields[j] = new ListGridField(field.getName(), field.getTitle(), j==0?200:150);
            if (j == 0) {
                if (fieldNames == null || fieldNames.length == 0) {
                    //gridFields[j].setFrozen(true);
                }
            }
            gridFields[j].setHidden(false);
            if (columnWidth != null) {
                gridFields[j].setWidth(columnWidth);
            } else {
                gridFields[j].setWidth("*");
            }
            int pos = Arrays.binarySearch(sortedFieldNames, field.getName());
            if (pos >= 0) {
                gridFields[j].setCanEdit(canEdit[pos]);
            }
            String fieldType = field.getAttribute("fieldType");
            if (fieldType != null && SupportedFieldType.MONEY.toString().equals(fieldType)) {
                gridFields[j].setCellFormatter(new CellFormatter() {
                    public String format(Object value, ListGridRecord record, int rowNum, int colNum) {
                        return value==null?"":NumberFormat.getFormat("0.00").format(NumberFormat.getFormat("0.00").parse(String.valueOf(value)));
                    }
                });
            }
            j++;
            availableSlots--;
        }
        for (DataSourceField field : fields) {
            if (!prominentFields.contains(field)) {
                gridFields[j] = new ListGridField(field.getName(), field.getTitle(), j==0?200:150);
                if (field.getAttributeAsBoolean("permanentlyHidden")) {
                    gridFields[j].setHidden(true);
                    gridFields[j].setCanHide(false);
                } else if (field.getAttributeAsBoolean("hidden") || field.getAttributeAsBoolean("excluded")) {
                    gridFields[j].setHidden(true);
                } else if (availableSlots <= 0) {
                    gridFields[j].setHidden(true);
                } else {
                    if (j == 0) {
                        if (fieldNames == null || fieldNames.length == 0) {
                            //gridFields[j].setFrozen(true);
                        }
                    }
                    String columnWidth = field.getAttribute("columnWidth");
                    if (columnWidth != null) {
                        gridFields[j].setWidth(columnWidth);
                    } else {
                        gridFields[j].setWidth("*");
                    }
                    int pos = Arrays.binarySearch(sortedFieldNames, field.getName());
                    if (pos >= 0) {
                        gridFields[j].setCanEdit(canEdit[pos]);
                    }
                    availableSlots--;
                }
                String fieldType = field.getAttribute("fieldType");
                if (fieldType != null && SupportedFieldType.MONEY.toString().equals(fieldType)) {
                    gridFields[j].setCellFormatter(new CellFormatter() {
                        public String format(Object value, ListGridRecord record, int rowNum, int colNum) {
                            return value==null?"":NumberFormat.getFormat("0.00").format(NumberFormat.getFormat("0.00").parse((String) value));
                        }
                    });
                }
                j++;
            }
        }
        ((ListGrid) getAssociatedGrid()).setFields(gridFields);
        if (fieldNames != null && fieldNames.length > 0) {
            int pos;
            if (((ListGrid) getAssociatedGrid()).getCanExpandRecords() != null && ((ListGrid) getAssociatedGrid()).getCanExpandRecords()) {
                pos = 1;
            } else {
                pos = 0;
            }
            for (String fieldName : fieldNames) {
                int originalPos = ((ListGrid) getAssociatedGrid()).getFieldNum(fieldName);
                if (pos != originalPos) {
                    ((ListGrid) getAssociatedGrid()).reorderField(originalPos, pos);
                }
                pos++;
            }
        }
        getAssociatedGrid().setHilites(hilites);
    }
    
}
