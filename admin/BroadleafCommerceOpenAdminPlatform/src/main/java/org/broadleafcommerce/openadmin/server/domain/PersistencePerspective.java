package org.broadleafcommerce.openadmin.server.domain;

import java.io.Serializable;
import java.util.Map;

import org.broadleafcommerce.openadmin.client.dto.OperationTypes;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspectiveItem;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspectiveItemType;

public interface PersistencePerspective extends Serializable {

	/**
	 * @return the id
	 */
	public abstract Long getId();

	/**
	 * @param id the id to set
	 */
	public abstract void setId(Long id);

	/**
	 * @return the additionalNonPersistentProperties
	 */
	public abstract String getAdditionalNonPersistentProperties();

	/**
	 * @param additionalNonPersistentProperties the additionalNonPersistentProperties to set
	 */
	public abstract void setAdditionalNonPersistentProperties(
			String additionalNonPersistentProperties);

	/**
	 * @return the additionalForeignKeys
	 */
	public abstract String getAdditionalForeignKeys();

	/**
	 * @param additionalForeignKeys the additionalForeignKeys to set
	 */
	public abstract void setAdditionalForeignKeys(String additionalForeignKeys);

	/**
	 * @return the persistencePerspectiveItems
	 */
	public abstract Map<PersistencePerspectiveItemType, PersistencePerspectiveItem> getPersistencePerspectiveItems();

	/**
	 * @param persistencePerspectiveItems the persistencePerspectiveItems to set
	 */
	public abstract void setPersistencePerspectiveItems(
			Map<PersistencePerspectiveItemType, PersistencePerspectiveItem> persistencePerspectiveItems);

	/**
	 * @return the operationTypes
	 */
	public abstract OperationTypes getOperationTypes();

	/**
	 * @param operationTypes the operationTypes to set
	 */
	public abstract void setOperationTypes(OperationTypes operationTypes);

	/**
	 * @return the populateToOneFields
	 */
	public abstract Boolean getPopulateToOneFields();

	/**
	 * @param populateToOneFields the populateToOneFields to set
	 */
	public abstract void setPopulateToOneFields(Boolean populateToOneFields);

	/**
	 * @return the excludeFields
	 */
	public abstract String getExcludeFields();

	/**
	 * @param excludeFields the excludeFields to set
	 */
	public abstract void setExcludeFields(String excludeFields);

	/**
	 * @return the includeFields
	 */
	public abstract String getIncludeFields();

	/**
	 * @param includeFields the includeFields to set
	 */
	public abstract void setIncludeFields(String includeFields);

	public abstract String getSandBox();

	public abstract void setSandBox(String sandBox);
	
	public Boolean getUseSandBox();

	public void setUseSandBox(Boolean useSandBox);
	
}