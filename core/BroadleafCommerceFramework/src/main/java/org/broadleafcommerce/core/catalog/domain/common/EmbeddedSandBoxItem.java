package org.broadleafcommerce.core.catalog.domain.common;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class EmbeddedSandBoxItem implements SandBoxItem {

	private static final long serialVersionUID = 1L;

	@Column(name = "VERSION", nullable=false)
    protected long version;
	
	@Column(name = "IS_DIRTY", nullable=false)
    protected boolean dirty;
	
	@Column(name = "DIRTY_FIELDS")
    protected String commaDelimitedDirtyFields;

	/**
	 * @return the version
	 */
	public long getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(long version) {
		this.version = version;
	}

	/**
	 * @return the dirty
	 */
	public boolean isDirty() {
		return dirty;
	}

	/**
	 * @param dirty the dirty to set
	 */
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	/**
	 * @return the commaDelimitedDirtyFields
	 */
	public String getCommaDelimitedDirtyFields() {
		return commaDelimitedDirtyFields;
	}

	/**
	 * @param commaDelimitedDirtyFields the commaDelimitedDirtyFields to set
	 */
	public void setCommaDelimitedDirtyFields(String commaDelimitedDirtyFields) {
		this.commaDelimitedDirtyFields = commaDelimitedDirtyFields;
	}
	
}
