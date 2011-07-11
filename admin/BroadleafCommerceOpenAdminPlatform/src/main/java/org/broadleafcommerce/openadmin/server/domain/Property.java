package org.broadleafcommerce.openadmin.server.domain;

import java.io.Serializable;

import javax.persistence.Entity;

public interface Property extends Serializable {

	public abstract String getName();

	public abstract void setName(String name);

	public abstract String getValue();

	public abstract void setValue(String value);

	public abstract String getDisplayValue();

	public abstract void setDisplayValue(String displayValue);

	public Entity getEntity();

	/**
	 * @param entity the entity to set
	 */
	public void setEntity(Entity entity);

	/**
	 * @return the id
	 */
	public Long getId();

	/**
	 * @param id the id to set
	 */
	public void setId(Long id);
}