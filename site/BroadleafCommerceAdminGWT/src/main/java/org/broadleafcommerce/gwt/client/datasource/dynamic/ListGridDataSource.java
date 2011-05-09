package org.broadleafcommerce.gwt.client.datasource.dynamic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.broadleafcommerce.gwt.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspective;
import org.broadleafcommerce.gwt.client.service.DynamicEntityServiceAsync;

import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.widgets.grid.ListGridField;

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
	
	public void setupFields(String[] fieldNames, Boolean[] canEdit) {
		if (fieldNames.length > 0) {
			resetFieldVisibility(fieldNames);
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
        	if (j == 0) {
        		gridFields[j].setFrozen(true);
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
        		if (field.getAttributeAsBoolean("permanentlyHidden")) {
        			gridFields[j].setHidden(true);
	        		gridFields[j].setCanHide(false);
        		} else if (field.getAttributeAsBoolean("hidden")) {
        			gridFields[j].setHidden(true);
        		} else if (availableSlots <= 0) {
	        		gridFields[j].setHidden(true);
	        	} else {
	        		if (j == 0) {
		        		gridFields[j].setFrozen(true);
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
        getAssociatedGrid().setFields(gridFields);
	}
	
}
