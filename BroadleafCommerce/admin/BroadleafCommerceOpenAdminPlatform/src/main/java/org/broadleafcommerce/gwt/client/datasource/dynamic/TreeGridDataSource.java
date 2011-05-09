package org.broadleafcommerce.gwt.client.datasource.dynamic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.broadleafcommerce.gwt.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspective;
import org.broadleafcommerce.gwt.client.service.DynamicEntityServiceAsync;

import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.widgets.tree.TreeGridField;

public class TreeGridDataSource extends PresentationLayerAssociatedDataSource {
	
	protected String rootId;
	protected String rootName;

	/**
	 * @param name
	 * @param persistencePerspective
	 * @param service
	 * @param modules
	 */
	public TreeGridDataSource(String name, PersistencePerspective persistencePerspective, DynamicEntityServiceAsync service, DataSourceModule[] modules, String rootId, String rootName) {
		super(name, persistencePerspective, service, modules);
		this.rootId = rootId;
		this.rootName = rootName;
	}

	public void setupGridFields(String[] fieldNames, Boolean[] canEdit, String initialFieldWidth, String otherFieldWidth) {
		if (fieldNames != null && fieldNames.length > 0) {
			resetProminence(fieldNames);
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
        if (fieldNames != null && fieldNames.length > 0) {
        	int pos = 0;
        	for (String fieldName : fieldNames) {
        		getAssociatedGrid().reorderField(getAssociatedGrid().getFieldNum(fieldName), pos);
        		pos++;
        	}
        }
	}
	
	@Override
	public void resetFieldVisibility(String... fieldNames) {
		super.resetFieldVisibility(fieldNames);
		getAssociatedGrid().refreshFields();
	}

	public String getRootId() {
		return rootId;
	}

	public void setRootId(String root) {
		this.rootId = root;
	}

	public String getRootName() {
		return rootName;
	}

	public void setRootName(String rootName) {
		this.rootName = rootName;
	}

}
