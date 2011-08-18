package org.broadleafcommerce.openadmin.server.domain;

import org.broadleafcommerce.openadmin.client.presentation.SupportedFieldType;

import java.io.Serializable;

public interface Property extends Serializable {

	public abstract String getName();

	public abstract void setName(String name);

	public abstract String getValue();

	public abstract void setValue(String value);

	public abstract String getDisplayValue();

	public abstract void setDisplayValue(String displayValue);

	public org.broadleafcommerce.openadmin.server.domain.Entity getEntity();

	/**
	 * @param entity the entity to set
	 */
	public void setEntity(org.broadleafcommerce.openadmin.server.domain.Entity entity);

	/**
	 * @return the id
	 */
	public Long getId();

	/**
	 * @param id the id to set
	 */
	public void setId(Long id);
	
	public Boolean getIsDirty();

	public void setIsDirty(Boolean isDirty);

    public SupportedFieldType getSecondaryType();

    public void setSecondaryType(SupportedFieldType secondaryType);
}