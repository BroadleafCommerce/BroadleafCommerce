package org.broadleafcommerce.openadmin.domain;

import java.io.Serializable;

public interface Property extends Serializable {

	public abstract String getName();

	public abstract void setName(String name);

	public abstract String getValue();

	public abstract void setValue(String value);

	public abstract String getDisplayValue();

	public abstract void setDisplayValue(String displayValue);

}