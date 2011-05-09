package org.broadleafcommerce.gwt.client.datasource.dynamic;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.broadleafcommerce.gwt.client.Main;
import org.broadleafcommerce.gwt.client.datasource.EntityServiceDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.gwt.client.datasource.relations.ForeignKey;
import org.broadleafcommerce.gwt.client.datasource.relations.JoinTable;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspective;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspectiveItemType;
import org.broadleafcommerce.gwt.client.datasource.relations.operations.OperationType;
import org.broadleafcommerce.gwt.client.datasource.results.DynamicResultSet;
import org.broadleafcommerce.gwt.client.datasource.results.Entity;
import org.broadleafcommerce.gwt.client.service.DynamicEntityServiceAsync;

import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.FieldType;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.tree.TreeNode;

public class DynamicEntityDataSource extends EntityServiceDataSource {
	
	protected final String ceilingEntityFullyQualifiedClassname;
	protected HashMap<String, String> polymorphicEntities = new HashMap<String, String>();
	protected DynamicEntityServiceAsync service;
	protected String defaultNewEntityFullyQualifiedClassname;
	protected PersistencePerspective persistencePerspective;
	protected DataSourceModule[] modules;
	
	public DynamicEntityDataSource(String ceilingEntityFullyQualifiedClassname, String name, PersistencePerspective persistencePerspective, DynamicEntityServiceAsync service, DataSourceModule[] modules) {
		super(name);
		this.ceilingEntityFullyQualifiedClassname = ceilingEntityFullyQualifiedClassname;
		this.service = service;
		this.persistencePerspective = persistencePerspective;
		for (DataSourceModule module : modules) {
			module.setDataSource(this);
		}
		this.modules = modules;
	}
	
	public HashMap<String, String> getPolymorphicEntities() {
		return polymorphicEntities;
	}
	
	public String getCeilingEntityFullyQualifiedClassname() {
		return ceilingEntityFullyQualifiedClassname;
	}

	public String getDefaultNewEntityFullyQualifiedClassname() {
		return defaultNewEntityFullyQualifiedClassname;
	}

	public void setDefaultNewEntityFullyQualifiedClassname(String defaultNewEntityFullyQualifiedClassname) {
		this.defaultNewEntityFullyQualifiedClassname = defaultNewEntityFullyQualifiedClassname;
	}
	
	public void buildFields(final AsyncCallback<DataSource> cb) {
		Main.NON_MODAL_PROGRESS.startProgress();
		DataSourceModule myModule = getCompatibleModule(persistencePerspective.getOperationTypes().getInspectType());
		myModule.buildFields(cb);
	}
	
	@Override
	protected void executeFetch(final String requestId, final DSRequest request, final DSResponse response) {
		Main.NON_MODAL_PROGRESS.startProgress();
		DataSourceModule myModule = getCompatibleModule(persistencePerspective.getOperationTypes().getFetchType());
		myModule.executeFetch(requestId, request, response);
	}

	@Override
	protected void executeAdd(final String requestId, final DSRequest request, final DSResponse response) {
		Main.NON_MODAL_PROGRESS.startProgress();
		DataSourceModule myModule = getCompatibleModule(persistencePerspective.getOperationTypes().getAddType());
        myModule.executeAdd(requestId, request, response);
	}

	@Override
	protected void executeUpdate(final String requestId, final DSRequest request, final DSResponse response) {
		Main.NON_MODAL_PROGRESS.startProgress();
		DataSourceModule myModule = getCompatibleModule(persistencePerspective.getOperationTypes().getUpdateType());
        myModule.executeUpdate(requestId, request, response);
	}

	@Override
	protected void executeRemove(final String requestId, final DSRequest request, final DSResponse response) {
		Main.NON_MODAL_PROGRESS.startProgress();
		DataSourceModule myModule = getCompatibleModule(persistencePerspective.getOperationTypes().getRemoveType());
        myModule.executeRemove(requestId, request, response);
	}
	
	public Entity buildEntity(Record record) {
		DataSourceModule myModule = getCompatibleModule(persistencePerspective.getOperationTypes().getMiscType());
        return myModule.buildEntity(record);
	}
	
	public CriteriaTransferObject getCto(DSRequest request) {
		DataSourceModule myModule = getCompatibleModule(persistencePerspective.getOperationTypes().getMiscType());
        return myModule.getCto(request);
	}
	
	public Record updateRecord(Entity entity, Record record, Boolean updateId) {
		DataSourceModule myModule = getCompatibleModule(persistencePerspective.getOperationTypes().getMiscType());
        return myModule.updateRecord(entity, record, updateId);
	}
	
	public String getLinkedValue() {
		DataSourceModule myModule = getCompatibleModule(persistencePerspective.getOperationTypes().getMiscType());
		return myModule.getLinkedValue();
	}
	
	public void setLinkedValue(String linkedValue) {
		DataSourceModule myModule = getCompatibleModule(persistencePerspective.getOperationTypes().getMiscType());
		myModule.setLinkedValue(linkedValue);
	}
	
	public Record buildRecord(Entity entity) {
		DataSourceModule myModule = getCompatibleModule(persistencePerspective.getOperationTypes().getMiscType());
		return myModule.buildRecord(entity);
	}
	
	public TreeNode[] buildRecords(DynamicResultSet result, String[] filterOutIds) {
		DataSourceModule myModule = getCompatibleModule(persistencePerspective.getOperationTypes().getMiscType());
		return myModule.buildRecords(result, filterOutIds);
	}
	
	protected DataSourceModule getCompatibleModule(OperationType operationType) {
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
		JoinTable joinTable = (JoinTable) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.JOINTABLE);
		if (foreignField == null && joinTable == null) {
			throw new RuntimeException("Only datasources that utilize a foreignField or joinTable relationship may utilize this method");
		}
		Criteria criteria = new Criteria();
		String foreignKeyName;
		if (foreignField != null) {
			foreignKeyName = foreignField.getManyToField();
		} else {
			foreignKeyName = joinTable.getManyToField();
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
				} else {
					extractedValues.put(attribute, record.getAttribute(attribute));
				}
			}
		}
		return extractedValues;
	}
	
	public void resetFieldVisibility() {
		resetFieldVisibilityBasedOnType(getDefaultNewEntityFullyQualifiedClassname());
	}
	
	public void resetFieldVisibility(String... fieldNames) {
		DataSourceField[] fields = getFields();
		Arrays.sort(fieldNames);
		for (DataSourceField field : fields) {
			String fieldName = field.getName();
			int pos = Arrays.binarySearch(fieldNames, fieldName);
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

	public void resetFieldVisibilityBasedOnType(String type) {
		DataSourceField[] fields = getFields();
		for (DataSourceField field : fields) {
			if (field.getAttribute("permanentlyHidden") == null || field.getAttributeAsBoolean("permanentlyHidden") == false) {
				if (field.getAttribute("availableToTypes") != null && field.getAttribute("availableToTypes").contains(type)) {
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
	}

	public PersistencePerspective getPersistencePerspective() {
		return persistencePerspective;
	}

	public String stripDuplicateAllowSpecialCharacters(String string) {
    	if (string != null) {
			int index = string.indexOf("_");
	        if (index >= 0) {
	        	string = string.substring(0,index);
	        }
    	}
        return string;
	}
}
