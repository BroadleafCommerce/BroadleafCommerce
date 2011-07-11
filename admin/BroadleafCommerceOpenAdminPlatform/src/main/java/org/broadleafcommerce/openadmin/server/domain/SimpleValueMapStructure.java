package org.broadleafcommerce.openadmin.server.domain;

public interface SimpleValueMapStructure extends MapStructure {

	public abstract String getValuePropertyName();

	public abstract void setValuePropertyName(String valuePropertyName);

	public abstract String getValuePropertyFriendlyName();

	public abstract void setValuePropertyFriendlyName(
			String valuePropertyFriendlyName);

}