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
	 * @param ceilingEntityFullyQualifiedClassname
	 * @param name
	 * @param persistencePerspective
	 * @param service
	 * @param modules
	 */
	public TreeGridDataSource(String ceilingEntityFullyQualifiedClassname, String name, PersistencePerspective persistencePerspective, DynamicEntityServiceAsync service, DataSourceModule[] modules, String rootId, String rootName) {
		super(ceilingEntityFullyQualifiedClassname, name, persistencePerspective, service, modules);
		this.rootId = rootId;
		this.rootName = rootName;
	}

	public void setupFields(String[] fieldNames, Boolean[] canEdit, String initialFieldWidth, String otherFieldWidth) {
		if (fieldNames.length > 0) {
			resetFieldVisibility(fieldNames);
		}
		DataSourceField[] fields = getFields();
        TreeGridField[] gridFields = new TreeGridField[fields.length];
        int j = 0;
        List<DataSourceField> prominentFields = new ArrayList<DataSourceField>();
        for (DataSourceField field : fields) {
        	if (field.getAttributeAsBoolean("prominent")) {
        		prominentFields.add(field);
        	}
        }
        int availableSlots = 4;
        for (DataSourceField field : prominentFields) {
        	gridFields[j] = new TreeGridField(field.getName(), field.getTitle(), j==0?200:150);
        	if (j == 0) {
        		gridFields[j].setFrozen(true);
        		if (initialFieldWidth != null) {
        			gridFields[j].setWidth(initialFieldWidth);
        		}
        	} else {
        		gridFields[j].setWidth(otherFieldWidth);
        	}
        	gridFields[j].setHidden(false);
        	int pos = Arrays.binarySearch(fieldNames, field.getName());
        	if (pos >= 0) {
        		gridFields[j].setCanEdit(canEdit[pos]);
        	}
        	j++;
        	availableSlots--;
        }
        for (DataSourceField field : fields) {
        	if (!prominentFields.contains(field)) {
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
	            		gridFields[j].setFrozen(true);
	            		if (initialFieldWidth != null) {
	            			gridFields[j].setWidth(initialFieldWidth);
	            		}
	            	} else {
	            		gridFields[j].setWidth(otherFieldWidth);
	            	}
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
