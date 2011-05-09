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
	protected Boolean populateManyToOneFields = false;
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
		Arrays.sort(additionalNonPersistentProperties);
		this.additionalNonPersistentProperties = additionalNonPersistentProperties;
	}

	public ForeignKey[] getAdditionalForeignKeys() {
		return additionalForeignKeys;
	}

	public void setAdditionalForeignKeys(ForeignKey[] additionalNonPersistentForeignKeys) {
		Arrays.sort(additionalNonPersistentForeignKeys, new Comparator<ForeignKey>() {
			public int compare(ForeignKey o1, ForeignKey o2) {
				return o1.getManyToField().compareTo(o2.getManyToField());
			}
		});
		this.additionalForeignKeys = additionalNonPersistentForeignKeys;
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

	public Boolean getPopulateManyToOneFields() {
		return populateManyToOneFields;
	}

	public void setPopulateManyToOneFields(Boolean populateManyToOneFields) {
		this.populateManyToOneFields = populateManyToOneFields;
	}

	public String[] getExcludeFields() {
		return excludeFields;
	}

	public void setExcludeFields(String[] excludeManyToOneFields) {
		Arrays.sort(excludeManyToOneFields);
		this.excludeFields = excludeManyToOneFields;
	}

	public String[] getIncludeFields() {
		return includeFields;
	}

	public void setIncludeFields(String[] includeManyToOneFields) {
		Arrays.sort(includeManyToOneFields);
		this.includeFields = includeManyToOneFields;
	}

}
