package org.broadleafcommerce.gwt.client.datasource.dynamic;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.broadleafcommerce.gwt.client.Main;
import org.broadleafcommerce.gwt.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.gwt.client.datasource.relations.ForeignKey;
import org.broadleafcommerce.gwt.client.datasource.relations.JoinStructure;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspective;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspectiveItemType;
import org.broadleafcommerce.gwt.client.datasource.relations.operations.OperationType;
import org.broadleafcommerce.gwt.client.service.DynamicEntityServiceAsync;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.types.FieldType;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.tree.TreeNode;

public class DynamicEntityDataSource extends AbstractDynamicDataSource {
	
	protected final String ceilingEntityFullyQualifiedClassname;
	
	/**
	 * @param name
	 * @param persistencePerspective
	 * @param service
	 * @param modules
	 */
	public DynamicEntityDataSource(String ceilingEntityFullyQualifiedClassname, String name, PersistencePerspective persistencePerspective, DynamicEntityServiceAsync service, DataSourceModule[] modules) {
		super(name, persistencePerspective, service, modules);
		this.ceilingEntityFullyQualifiedClassname = ceilingEntityFullyQualifiedClassname;
	}
	
	public String getCeilingEntityFullyQualifiedClassname() {
		return ceilingEntityFullyQualifiedClassname;
	}
	
	public void buildFields(final String[] customCriteria, final AsyncCallback<DataSource> cb) {
		Main.NON_MODAL_PROGRESS.startProgress();
		DataSourceModule myModule = getCompatibleModule(persistencePerspective.getOperationTypes().getInspectType());
		myModule.buildFields(customCriteria, cb);
	}
	
	@Override
	protected void executeFetch(final String requestId, final DSRequest request, final DSResponse response) {
		this.executeFetch(requestId, request, response, null, null);
	}
	
	protected void executeFetch(final String requestId, final DSRequest request, final DSResponse response, final String[] customCriteria, final AsyncCallback<DataSource> cb) {
		Main.NON_MODAL_PROGRESS.startProgress();
		DataSourceModule myModule = getCompatibleModule(persistencePerspective.getOperationTypes().getFetchType());
		myModule.executeFetch(requestId, request, response, customCriteria, cb);
	}

	@Override
	protected void executeAdd(final String requestId, final DSRequest request, final DSResponse response) {
		this.executeAdd(requestId, request, response, null, null);
	}
	
	protected void executeAdd(final String requestId, final DSRequest request, final DSResponse response, final String[] customCriteria, final AsyncCallback<DataSource> cb) {
		Main.NON_MODAL_PROGRESS.startProgress();
		DataSourceModule myModule = getCompatibleModule(persistencePerspective.getOperationTypes().getAddType());
        myModule.executeAdd(requestId, request, response, customCriteria, cb);
	}

	@Override
	protected void executeUpdate(final String requestId, final DSRequest request, final DSResponse response) {
		this.executeUpdate(requestId, request, response, null, null);
	}
	
	protected void executeUpdate(final String requestId, final DSRequest request, final DSResponse response, final String[] customCriteria, final AsyncCallback<DataSource> cb) {
		Main.NON_MODAL_PROGRESS.startProgress();
		DataSourceModule myModule = getCompatibleModule(persistencePerspective.getOperationTypes().getUpdateType());
        myModule.executeUpdate(requestId, request, response, customCriteria, cb);
	}

	@Override
	protected void executeRemove(final String requestId, final DSRequest request, final DSResponse response) {
		this.executeRemove(requestId, request, response, null, null);
	}
	
	protected void executeRemove(final String requestId, final DSRequest request, final DSResponse response, final String[] customCriteria, final AsyncCallback<DataSource> cb) {
		Main.NON_MODAL_PROGRESS.startProgress();
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
			throw new RuntimeException("Unable to find a compatible data source module for the operation type: " + operationType);
		}
        
        return myModule;
	}
	
	public Criteria createRelationshipCriteria(String relationshipValue) {
		ForeignKey foreignField = (ForeignKey) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY);
		JoinStructure joinStructure = (JoinStructure) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.JOINSTRUCTURE);
		if (foreignField == null && joinStructure == null) {
			throw new RuntimeException("Only datasources that utilize a foreignField or joinStructure relationship may utilize this method");
		}
		Criteria criteria = new Criteria();
		String foreignKeyName;
		if (foreignField != null) {
			foreignKeyName = foreignField.getManyToField();
		} else {
			foreignKeyName = joinStructure.getManyToField();
		}
		criteria.addCriteria(foreignKeyName, relationshipValue);
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
				} else if (attribute.equals("type")){
					extractedValues.put(attribute, record.getAttributeAsStringArray(attribute));
				} else {
					extractedValues.put(attribute, record.getAttribute(attribute));
				}
			}
		}
		return extractedValues;
	}
	
	public void resetFieldVisibility() {
		resetFieldVisibilityBasedOnType(new String[]{getDefaultNewEntityFullyQualifiedClassname()});
	}
	
	public void resetFieldVisibility(String... fieldNames) {
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
				//field.setAttribute("permanentlyHidden", false);
				field.setAttribute("prominent", true);
			} else {
				field.setHidden(true);
				//field.setAttribute("permanentlyHidden", true);
				field.setAttribute("prominent", false);
			}
		}
	}
	
	public void resetProminence(String... fieldNames) {
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
				//field.setHidden(false);
				//field.setAttribute("permanentlyHidden", false);
				field.setAttribute("prominent", true);
			} else {
				//field.setHidden(true);
				//field.setAttribute("permanentlyHidden", true);
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
	
	public void removeFields(String... fieldNames) {
		Arrays.sort(fieldNames);
		DataSourceField[] fields = getFields();
		for (DataSourceField field : fields) {
			String fieldName = field.getName();
			int pos = Arrays.binarySearch(fieldNames, fieldName);
			if (pos >= 0) {
				field.setHidden(true);
				field.setAttribute("permanentlyHidden", true);
			}
		}
	}

	public void resetFieldVisibilityBasedOnType(String[] type) {
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
				//field.setAttribute("permanentlyHidden", false);
				field.setAttribute("prominent", true);
			} else {
				field.setHidden(true);
				//field.setAttribute("permanentlyHidden", true);
				field.setAttribute("prominent", false);
			}
		}
	}

	public PersistencePerspective getPersistencePerspective() {
		return persistencePerspective;
	}
	
}
