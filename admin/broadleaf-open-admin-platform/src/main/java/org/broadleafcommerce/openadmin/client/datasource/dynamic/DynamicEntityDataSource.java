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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.*;
import com.smartgwt.client.types.FieldType;
import com.smartgwt.client.types.OperatorId;
import com.smartgwt.client.util.JSOHelper;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.tree.TreeNode;
import org.broadleafcommerce.common.presentation.client.OperationType;
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.openadmin.client.dto.*;
import org.broadleafcommerce.openadmin.client.service.DynamicEntityServiceAsync;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * 
 * @author jfischer
 *
 */
public class DynamicEntityDataSource extends AbstractDynamicDataSource {

    /**
     * Creates a datasource capable of all CRUD operations on the passed in Entity.
     * This constructor handles the simplest needs for an Entity Datasource which is
     * what is called for most of the time.    For more advanced needs involving
     * Foreign Keys, Lists, and Maps, the more advanced constructor is required.
     *
     * @param ceilingEntityClassName - The fully qualified name of the ceilingEntity.
     */
    public DynamicEntityDataSource(String ceilingEntityClassName) {
         super(ceilingEntityClassName);
    }
	
	/**
     * Note that the name parameter on this constructor is not meaningful to the internal representation
     * of DynamicEntityDataSource.    It is required by superclasses but unused.
     *
	 * @param name
	 * @param persistencePerspective
	 * @param service
	 * @param modules
	 */
	public DynamicEntityDataSource(String name, PersistencePerspective persistencePerspective, DynamicEntityServiceAsync service, DataSourceModule[] modules) {
		super(name, persistencePerspective, service, modules);
		//this.setTypeOps(OperatorId.AND, OperatorId.EQUALS);
	}
	
	public void buildFields(final String[] customCriteria, Boolean overrideFieldSort, final AsyncCallback<DataSource> cb) {
		DataSourceModule myModule = getCompatibleModule(persistencePerspective.getOperationTypes().getInspectType());
		myModule.buildFields(customCriteria, overrideFieldSort, cb);
	}
	
	@Override
	protected void executeFetch(final String requestId, final DSRequest request, final DSResponse response) {
		this.executeFetch(requestId, request, response, null, null);
	}
	
	protected void executeFetch(final String requestId, final DSRequest request, final DSResponse response, final String[] customCriteria, final AsyncCallback<DataSource> cb) {
		DataSourceModule myModule = getCompatibleModule(persistencePerspective.getOperationTypes().getFetchType());
		myModule.executeFetch(requestId, request, response, customCriteria, cb);
	}

	@Override
	protected void executeAdd(final String requestId, final DSRequest request, final DSResponse response) {
		this.executeAdd(requestId, request, response, null, null);
	}
	
	protected void executeAdd(final String requestId, final DSRequest request, final DSResponse response, final String[] customCriteria, final AsyncCallback<DataSource> cb) {
		DataSourceModule myModule = getCompatibleModule(persistencePerspective.getOperationTypes().getAddType());
        myModule.executeAdd(requestId, request, response, customCriteria, cb);
	}

	@Override
	protected void executeUpdate(final String requestId, final DSRequest request, final DSResponse response) {
		this.executeUpdate(requestId, request, response, null, null);
	}
	
	protected void executeUpdate(final String requestId, final DSRequest request, final DSResponse response, final String[] customCriteria, final AsyncCallback<DataSource> cb) {
		DataSourceModule myModule = getCompatibleModule(persistencePerspective.getOperationTypes().getUpdateType());
        myModule.executeUpdate(requestId, request, response, customCriteria, cb);
	}

	@Override
	protected void executeRemove(final String requestId, final DSRequest request, final DSResponse response) {
		this.executeRemove(requestId, request, response, null, null);
	}
	
	protected void executeRemove(final String requestId, final DSRequest request, final DSResponse response, final String[] customCriteria, final AsyncCallback<DataSource> cb) {
		DataSourceModule myModule = getCompatibleModule(persistencePerspective.getOperationTypes().getRemoveType());
        myModule.executeRemove(requestId, request, response, customCriteria, cb);
	}
	
	public void setLinkedValue(String linkedValue) {
		for(DataSourceModule dataSourceModule : modules) {
			dataSourceModule.setLinkedValue(linkedValue);
		}
	}
	
	public DataSourceModule getCompatibleModule(OperationType operationType) {
		DataSourceModule myModule = null;
		for(DataSourceModule dataSourceModule : modules) {
			if (dataSourceModule.isCompatible(operationType)) {
				myModule = dataSourceModule;
				break;
			}
		}
        if (myModule == null) {
			SC.warn("Unable to find a compatible data source module for the operation type: " + operationType);
            java.util.logging.Logger.getLogger(getClass().toString()).log(Level.SEVERE,"Unable to find a compatible data source module for the operation type: " + operationType);;
			throw new RuntimeException("Unable to find a compatible data source module for the operation type: " + operationType);
		}
        
        return myModule;
	}
	
	public Criteria createRelationshipCriteria(String relationshipValue) {
		ForeignKey foreignField = (ForeignKey) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY);
		AdornedTargetList adornedTargetList = (AdornedTargetList) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.ADORNEDTARGETLIST);
		if (foreignField == null && adornedTargetList == null) {
			throw new RuntimeException("Only datasources that utilize foreignField or adornedTargetList relationships may utilize this method");
		}
		Criteria criteria = new Criteria();
		String relationshipName;
		if (foreignField != null) {
			relationshipName = foreignField.getManyToField();
		} else if (adornedTargetList != null) {
			relationshipName = adornedTargetList.getCollectionFieldName();
		} else {
			relationshipName = "containingEntityId";
		}
        if (relationshipValue == null) {
            relationshipValue = "null";
        }
		criteria.addCriteria(relationshipName, relationshipValue);
		for(DataSourceModule dataSourceModule : modules) {
			dataSourceModule.setLinkedValue(relationshipValue);
		}
		
		return criteria;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map extractRecordValues(TreeNode record, String... excludeFields) {
		Map extractedValues = new HashMap();
		for (String attribute : record.getAttributes()) {
			if (!attribute.startsWith("__") && getField(attribute) != null && Arrays.binarySearch(excludeFields, attribute) < 0) {
				if (record.getAttribute(attribute) != null && this.getField(attribute) != null && getField(attribute).getType().equals(FieldType.DATETIME)) {
					extractedValues.put(attribute, record.getAttributeAsDate(attribute));
				} else if (attribute.equals("_type")){
					extractedValues.put(attribute, record.getAttributeAsStringArray(attribute));
				} else {
					extractedValues.put(attribute, record.getAttribute(attribute));
				}
			}
		}
		return extractedValues;
	}
	
	public void resetPermanentFieldVisibility() {
		resetPermanentFieldVisibilityBasedOnType(new String[]{getDefaultNewEntityFullyQualifiedClassname()});
	}
	
	public void permanentlyHideFields(String... fieldNames) {
		DataSourceField[] fields = getFields();
		String[] sortedFieldNames = new String[fieldNames.length];
		for (int j=0;j<fieldNames.length;j++) {
			sortedFieldNames[j] = fieldNames[j];
		}
		Arrays.sort(sortedFieldNames);
		for (DataSourceField field : fields) {
			String fieldName = field.getName();
			int pos = Arrays.binarySearch(sortedFieldNames, fieldName);
			if (pos >= 0) {
				field.setHidden(true);
				field.setAttribute("permanentlyHidden", true);
				field.setAttribute("prominent", false);
			}
		}
	}
	
	public void permanentlyShowFields(String... fieldNames) {
		DataSourceField[] fields = getFields();
		String[] sortedFieldNames = new String[fieldNames.length];
		for (int j=0;j<fieldNames.length;j++) {
			sortedFieldNames[j] = fieldNames[j];
		}
		Arrays.sort(sortedFieldNames);
		for (DataSourceField field : fields) {
			String fieldName = field.getName();
			int pos = Arrays.binarySearch(sortedFieldNames, fieldName);
			if (pos >= 0) {
				field.setHidden(false);
				field.setAttribute("permanentlyHidden", false);
			}
		}
	}
	
	public void resetPermanentFieldVisibility(String... fieldNames) {
		DataSourceField[] fields = getFields();
		String[] sortedFieldNames = new String[fieldNames.length];
		for (int j=0;j<fieldNames.length;j++) {
			sortedFieldNames[j] = fieldNames[j];
		}
		Arrays.sort(sortedFieldNames);
		for (DataSourceField field : fields) {
			String fieldName = field.getName();
			int pos = Arrays.binarySearch(sortedFieldNames, fieldName);
			if (pos >= 0) {
				field.setHidden(false);
				field.setAttribute("permanentlyHidden", false);
				field.setAttribute("prominent", true);
			} else {
				field.setHidden(true);
				field.setAttribute("permanentlyHidden", true);
				field.setAttribute("prominent", false);
			}
		}
	}
	
	public void resetVisibilityOnly(String... fieldNames) {
		DataSourceField[] fields = getFields();
		String[] sortedFieldNames = new String[fieldNames.length];
		for (int j=0;j<fieldNames.length;j++) {
			sortedFieldNames[j] = fieldNames[j];
		}
		Arrays.sort(sortedFieldNames);
		for (DataSourceField field : fields) {
			String fieldName = field.getName();
			int pos = Arrays.binarySearch(sortedFieldNames, fieldName);
			if (pos >= 0) {
				field.setHidden(false);
			} else {
				field.setHidden(true);
			}
		}
	}
	
	public void resetProminenceOnly(String... fieldNames) {
		DataSourceField[] fields = getFields();
		String[] sortedFieldNames = new String[fieldNames.length];
		for (int j=0;j<fieldNames.length;j++) {
			sortedFieldNames[j] = fieldNames[j];
		}
		Arrays.sort(sortedFieldNames);
		for (DataSourceField field : fields) {
			String fieldName = field.getName();
			int pos = Arrays.binarySearch(sortedFieldNames, fieldName);
			if (pos >= 0) {
				field.setAttribute("prominent", true);
			} else {
				field.setAttribute("prominent", false);
			}
		}
	}
	
	public void updateFriendlyName(String fieldName, String friendlyName) {
		DataSourceField field = getField(fieldName);
		if (field != null) {
			field.setTitle(friendlyName);
		}
	}

	public void resetPermanentFieldVisibilityBasedOnType(String[] type) {
		DataSourceField[] fields = getFields();
		for (DataSourceField field : fields) {
			Boolean foundType = false;
			if (field.getAttribute("permanentlyHidden") == null || field.getAttributeAsBoolean("permanentlyHidden") == false) {
				String[] availableTypes = field.getAttributeAsStringArray("availableToTypes");
				if (availableTypes != null) {
					for (String singleType : type) {
						if (Arrays.binarySearch(availableTypes, singleType) >= 0) {
							foundType = true;
							break;
						}
					}
					
				}
			}
			if (foundType) {
				field.setHidden(false);
			} else {
				field.setHidden(true);
			}
		}
	}

	public PersistencePerspective getPersistencePerspective() {
		return persistencePerspective;
	}
	
	public void setTypeOps(OperatorId...ids)
	{
		setTypeOps( null, JSOHelper.convertToJavaScriptArray( ids ) );
	}
	
	public native void setTypeOps(JavaScriptObject type, JavaScriptObject operatorArray) /*-{
  		var self = this.@com.smartgwt.client.core.BaseClass::getOrCreateJsObj()();
  		self.setTypeOperators(type,operatorArray);
	}-*/;
}
