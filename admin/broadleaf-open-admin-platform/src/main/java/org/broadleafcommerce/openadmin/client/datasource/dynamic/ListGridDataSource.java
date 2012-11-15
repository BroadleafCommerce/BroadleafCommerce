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

import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.service.DynamicEntityServiceAsync;

import com.google.gwt.i18n.client.NumberFormat;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.types.FieldType;
import com.smartgwt.client.widgets.DataBoundComponent;
import com.smartgwt.client.widgets.form.fields.MiniDateRangeItem;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public void setupGridFields() {
        setupGridFields(new String[]{});
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

    public String[] setupGridFields(String[] fieldNames, final Boolean[] canEdit) {
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
            if (FieldType.DATE == field.getType() || FieldType.DATETIME == field.getType()) {
                gridFields[j].setEditorType(new MiniDateRangeItem());
            }
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
                    prominentFields.add(field);
	        		availableSlots--;
	        	}
                setupDecimalFormatters(gridFields[j], field);
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
        } else {
            fieldNames = new String[gridFields.length];
            for (int k=0;k<gridFields.length;k++) {
                fieldNames[k] = gridFields[k].getName();
            }
        }
        getAssociatedGrid().setHilites(hilites);

        return fieldNames;
	}

    protected void setupDecimalFormatters(ListGridField gridField, DataSourceField field) {    
        String fieldType = field.getAttribute("fieldType");
        if (fieldType != null && SupportedFieldType.MONEY.toString().equals(fieldType)) {
            String currencyCodeField = null;
            if(field.getAttribute("currencyCodeField")!=null && !field.getAttribute("currencyCodeField").equals("")) {
                currencyCodeField = field.getAttribute("currencyCodeField");
            }
            final String formatCodeField = currencyCodeField;
            gridField.setCellFormatter(new CellFormatter() {
                @Override
                public String format( Object value, ListGridRecord record, int rowNum, int colNum) {
                    if (value == null) {
                        return "";
                    }
                    String formatCodeFieldTemp = formatCodeField;
                    if (formatCodeFieldTemp == null) {
                        formatCodeFieldTemp = getAttribute("currencyCodeField");
                    }
                    String currencyCode = null;
                    if (formatCodeFieldTemp != null) {
                        currencyCode = record.getAttribute(formatCodeFieldTemp);
                    }
                    if (currencyCode == null) {
                        currencyCode = getAttribute("blcCurrencyCode");
                    }
                    Number formatValue;
                    if (value.getClass().getName().equals(String.class.getName())) {
                        formatValue = Double.parseDouble((String) value);
                    } else {
                        formatValue = (Number) value;
                    }
                    try {
                        return NumberFormat.getCurrencyFormat(currencyCode).format(formatValue);
                    } catch (Exception e) {
                        return String.valueOf(value);
                    }
                }
            });
            gridField.setAttribute("type", "localMoneyDecimal");
        }
        if (fieldType != null && SupportedFieldType.DECIMAL.toString().equals(fieldType)) {
            gridField.setAttribute("type", "localDecimal");
        }
    }

}
