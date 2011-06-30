package org.broadleafcommerce.core.catalog.domain.common;

import java.io.Serializable;

public interface SandBoxItem extends Serializable {

	public long getVersion();
	
	public void setVersion(long version);
	
	/**
	 * @return the dirty
	 */
	public boolean isDirty();

	/**
	 * @param dirty the dirty to set
	 */
	public void setDirty(boolean dirty);

	/**
	 * @return the commaDelimitedDirtyFields
	 */
	public String getCommaDelimitedDirtyFields();

	/**
	 * @param commaDelimitedDirtyFields the commaDelimitedDirtyFields to set
	 */
	public void setCommaDelimitedDirtyFields(String commaDelimitedDirtyFields);
}
