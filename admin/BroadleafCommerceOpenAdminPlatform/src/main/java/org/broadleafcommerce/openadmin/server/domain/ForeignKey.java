package org.broadleafcommerce.openadmin.server.domain;

import org.broadleafcommerce.openadmin.client.dto.ForeignKeyRestrictionType;

public interface ForeignKey extends PersistencePerspectiveItem {

	public abstract String getManyToField();

	public abstract void setManyToField(String manyToField);

	public abstract String getForeignKeyClass();

	public abstract void setForeignKeyClass(String foreignKeyClass);

	public abstract String getCurrentValue();

	public abstract void setCurrentValue(String currentValue);

	public abstract String getDataSourceName();

	public abstract void setDataSourceName(String dataSourceName);

	public abstract ForeignKeyRestrictionType getRestrictionType();

	public abstract void setRestrictionType(ForeignKeyRestrictionType restrictionType);

	public abstract String getDisplayValueProperty();

	public abstract void setDisplayValueProperty(String displayValueProperty);

	public Long getId();

	public void setId(Long id);

}