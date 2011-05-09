package org.broadleafcommerce.gwt.client.datasource.relations;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.broadleafcommerce.gwt.client.datasource.relations.operations.OperationTypes;

public class PersistencePerspective implements Serializable {
	 
	private static final long serialVersionUID = 1L;
	
	protected String[] additionalNonPersistentProperties;
	protected ForeignKey[] additionalNonPersistentForeignKeys;
	protected Map<PersistencePerspectiveItemType, PersistencePerspectiveItem> persistencePerspectiveItems = new HashMap<PersistencePerspectiveItemType, PersistencePerspectiveItem>();
	protected OperationTypes operationTypes;
	
	public PersistencePerspective() {
		//do nothing
	}
	
	public PersistencePerspective(OperationTypes operationTypes, String[] additionalNonPersistentProperties, ForeignKey[] additionalNonPersistentForeignKeys) {
		this.additionalNonPersistentProperties = additionalNonPersistentProperties;
		this.additionalNonPersistentForeignKeys = additionalNonPersistentForeignKeys;
		this.operationTypes = operationTypes;
	}

	public String[] getAdditionalNonPersistentProperties() {
		return additionalNonPersistentProperties;
	}

	public void setAdditionalNonPersistentProperties(String[] additionalNonPersistentProperties) {
		this.additionalNonPersistentProperties = additionalNonPersistentProperties;
	}

	public ForeignKey[] getAdditionalNonPersistentForeignKeys() {
		return additionalNonPersistentForeignKeys;
	}

	public void setAdditionalNonPersistentForeignKeys(ForeignKey[] additionalNonPersistentForeignKeys) {
		this.additionalNonPersistentForeignKeys = additionalNonPersistentForeignKeys;
	}

	public OperationTypes getOperationTypes() {
		return operationTypes;
	}

	public void setOperationTypes(OperationTypes operationTypes) {
		this.operationTypes = operationTypes;
	}
	
	public void addPersistencePerspectiveItem(PersistencePerspectiveItemType type, PersistencePerspectiveItem item) {
		persistencePerspectiveItems.put(type, item);
	}

	public Map<PersistencePerspectiveItemType, PersistencePerspectiveItem> getPersistencePerspectiveItems() {
		return persistencePerspectiveItems;
	}

	public void setPersistencePerspectiveItems(Map<PersistencePerspectiveItemType, PersistencePerspectiveItem> persistencePerspectiveItems) {
		this.persistencePerspectiveItems = persistencePerspectiveItems;
	}

}
