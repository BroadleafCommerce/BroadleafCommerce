/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.openadmin.client.dto;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


/**
 * 
 * @author jfischer
 *
 */
public class PersistencePerspective implements Serializable {
	 
	private static final long serialVersionUID = 1L;
	
	protected String[] additionalNonPersistentProperties = new String[]{};
	protected ForeignKey[] additionalForeignKeys = new ForeignKey[]{};
	protected Map<PersistencePerspectiveItemType, PersistencePerspectiveItem> persistencePerspectiveItems = new HashMap<PersistencePerspectiveItemType, PersistencePerspectiveItem>();
	protected OperationTypes operationTypes = new OperationTypes();
	protected Boolean populateToOneFields = false;
	protected String[] excludeFields = new String[]{};
	protected String[] includeFields = new String[]{};
	
	public PersistencePerspective() {
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
