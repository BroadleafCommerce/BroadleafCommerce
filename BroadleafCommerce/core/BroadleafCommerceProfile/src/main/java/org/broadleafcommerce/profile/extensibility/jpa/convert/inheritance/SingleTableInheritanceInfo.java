package org.broadleafcommerce.profile.extensibility.jpa.convert.inheritance;

import javax.persistence.DiscriminatorType;

public class SingleTableInheritanceInfo {

	protected String className;
	protected String discriminatorName;
	protected DiscriminatorType discriminatorType;
	protected int discriminatorLength;
	
	public String getClassName() {
		return className;
	}
	
	public void setClassName(String className) {
		this.className = className;
	}
	
	public String getDiscriminatorName() {
		return discriminatorName;
	}
	
	public void setDiscriminatorName(String discriminatorName) {
		this.discriminatorName = discriminatorName;
	}
	
	public DiscriminatorType getDiscriminatorType() {
		return discriminatorType;
	}
	
	public void setDiscriminatorType(DiscriminatorType discriminatorType) {
		this.discriminatorType = discriminatorType;
	}
	
	public int getDiscriminatorLength() {
		return discriminatorLength;
	}
	
	public void setDiscriminatorLength(int discriminatorLength) {
		this.discriminatorLength = discriminatorLength;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((className == null) ? 0 : className.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SingleTableInheritanceInfo other = (SingleTableInheritanceInfo) obj;
		if (className == null) {
			if (other.className != null)
				return false;
		} else if (!className.equals(other.className))
			return false;
		return true;
	}
	
}
