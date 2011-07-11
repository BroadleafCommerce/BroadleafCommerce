package org.broadleafcommerce.openadmin.server.domain;

import java.io.Serializable;
import java.util.List;

public interface Entity extends Serializable {

	public abstract List<Property> getProperties();

	public abstract void setProperties(List<Property> properties);

	public abstract void addProperty(Property property);

	/**
	 * @return the id
	 */
	public Long getId();

	/**
	 * @param id the id to set
	 */
	public void setId(Long id);

	/**
	 * @return the type
	 */
	public String getType();

	/**
	 * @param type the type to set
	 */
	public void setType(String type);
}