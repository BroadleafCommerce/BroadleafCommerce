package org.broadleafcommerce.openadmin.server.domain;

public interface MapStructure extends PersistencePerspectiveItem {

	public abstract String getKeyClassName();

	public abstract void setKeyClassName(String keyClassName);

	public abstract String getValueClassName();

	public abstract void setValueClassName(String valueClassName);

	public abstract String getMapProperty();

	public abstract void setMapProperty(String mapProperty);

	public abstract String getKeyPropertyName();

	public abstract void setKeyPropertyName(String keyPropertyName);

	public abstract String getKeyPropertyFriendlyName();

	public abstract void setKeyPropertyFriendlyName(
			String keyPropertyFriendlyName);

	public abstract Boolean getDeleteValueEntity();

	public abstract void setDeleteValueEntity(Boolean deleteValueEntity);

}