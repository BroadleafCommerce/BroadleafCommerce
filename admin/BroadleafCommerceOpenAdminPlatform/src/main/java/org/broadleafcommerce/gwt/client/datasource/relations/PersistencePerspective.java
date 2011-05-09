package org.broadleafcommerce.gwt.client.datasource.relations;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.broadleafcommerce.gwt.client.datasource.relations.operations.OperationTypes;

public class PersistencePerspective implements Serializable {
	 
	private static final long serialVersionUID = 1L;
	
	protected String[] additionalNonPersistentProperties;
	protected ForeignKey[] additionalForeignKeys;
	protected Map<PersistencePerspectiveItemType, PersistencePerspectiveItem> persistencePerspectiveItems = new HashMap<PersistencePerspectiveItemType, PersistencePerspectiveItem>();
	protected OperationTypes operationTypes;
	protected Boolean populateToOneFields = false;
	protected String[] excludeFields = new String[]{};
	protected String[] includeFields = new String[]{};
	
	public PersistencePerspective() {
		//do nothing
	}
	
	public PersistencePerspective(OperationTypes operationTypes, String[] additionalNonPersistentProperties, ForeignKey[] additionalNonPersistentForeignKeys) {
		setAdditionalNonPersistentProperties(additionalNonPersistentProperties);
		setAdditionalForeignKeys(additionalNonPersistentForeignKeys);
		this.operationTypes = operationTypes;
	}

	public String[] getAdditionalNonPersistentProperties() {
		return additionalNonPersistentProperties;
	}

	public void setAdditionalNonPersistentProperties(String[] additionalNonPersistentProperties) {
		this.additionalNonPersistentProperties = additionalNonPersistentProperties;
		Arrays.sort(this.additionalNonPersistentProperties);
	}

	public ForeignKey[] getAdditionalForeignKeys() {
		return additionalForeignKeys;
	}

	public void setAdditionalForeignKeys(ForeignKey[] additionalNonPersistentForeignKeys) {
		this.additionalForeignKeys = additionalNonPersistentForeignKeys;
		Arrays.sort(this.additionalForeignKeys, new Comparator<ForeignKey>() {
			public int compare(ForeignKey o1, ForeignKey o2) {
				return o1.getManyToField().compareTo(o2.getManyToField());
			}
		});
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

	public Boolean getPopulateToOneFields() {
		return populateToOneFields;
	}

	public void setPopulateToOneFields(Boolean populateToOneFields) {
		this.populateToOneFields = populateToOneFields;
	}

	public String[] getExcludeFields() {
		return excludeFields;
	}

	public void setExcludeFields(String[] excludeManyToOneFields) {
		this.excludeFields = excludeManyToOneFields;
		Arrays.sort(this.excludeFields);
	}

	public String[] getIncludeFields() {
		return includeFields;
	}

	public void setIncludeFields(String[] includeManyToOneFields) {
		this.includeFields = includeManyToOneFields;
		Arrays.sort(this.includeFields);
	}

}
