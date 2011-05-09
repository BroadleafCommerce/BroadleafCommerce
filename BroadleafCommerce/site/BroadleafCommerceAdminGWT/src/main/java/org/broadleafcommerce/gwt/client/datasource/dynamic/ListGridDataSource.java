package org.broadleafcommerce.gwt.client.datasource.dynamic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.broadleafcommerce.gwt.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspective;
import org.broadleafcommerce.gwt.client.service.DynamicEntityServiceAsync;
import org.broadleafcommerce.presentation.SupportedFieldType;

import com.google.gwt.i18n.client.NumberFormat;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

public class ListGridDataSource extends PresentationLayerAssociatedDataSource {

	/**
	 * @param ceilingEntityFullyQualifiedClassname
	 * @param name
	 * @param persistencePerspective
	 * @param service
	 * @param modules
	 */
	public ListGridDataSource(String ceilingEntityFullyQualifiedClassname, String name, PersistencePerspective persistencePerspective, DynamicEntityServiceAsync service, DataSourceModule[] modules) {
		super(ceilingEntityFullyQualifiedClassname, name, persistencePerspective, service, modules);
	}
	
	public void setupGridFields(final String[] fieldNames, final Boolean[] canEdit) {
		if (fieldNames != null && fieldNames.length > 0) {
			resetProminence(fieldNames);
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
        int availableSlots = 4;
        for (DataSourceField field : prominentFields) {
        	String columnWidth = field.getAttribute("columnWidth");
        	gridFields[j] = new ListGridField(field.getName(), field.getTitle(), j==0?200:150);
        	if (j == 0) {
        		if (fieldNames == null || fieldNames.length == 0) {
        			gridFields[j].setFrozen(true);
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
        getAssociatedGrid().setFields(gridFields);
        if (fieldNames != null && fieldNames.length > 0) {
        	int pos;
        	if (getAssociatedGrid().getCanExpandRecords() != null && getAssociatedGrid().getCanExpandRecords()) {
        		pos = 1;
        	} else {
        		pos = 0;
        	}
        	for (String fieldName : fieldNames) {
        		int originalPos = getAssociatedGrid().getFieldNum(fieldName);
        		if (pos != originalPos) {
        			getAssociatedGrid().reorderField(originalPos, pos);
        		}
        		pos++;
        	}
        }
	}
	
}
