/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.openadmin.dto;

import org.broadleafcommerce.common.presentation.client.ForeignKeyRestrictionType;
import org.broadleafcommerce.openadmin.dto.visitor.PersistencePerspectiveItemVisitor;

import java.io.Serializable;


/**
 * 
 * @author jfischer
 *
 */
public class ForeignKey implements Serializable, PersistencePerspectiveItem {

    private static final long serialVersionUID = 1L;
    
    private String manyToField;
    private String originatingField;
    private String foreignKeyClass;
    private String currentValue;
    private String dataSourceName;
    private ForeignKeyRestrictionType restrictionType = ForeignKeyRestrictionType.ID_EQ;
    private String displayValueProperty = "name";
    private Boolean mutable = true;
    private String sortField;
    private Boolean sortAscending;
    
    public ForeignKey() {
        //do nothing
    }
    
    public ForeignKey(String manyToField, String foreignKeyClass) {
        this(manyToField, foreignKeyClass, null);
    }
    
    public ForeignKey(String manyToField, String foreignKeyClass, String dataSourceName) {
        this(manyToField, foreignKeyClass, dataSourceName, ForeignKeyRestrictionType.ID_EQ);
    }

    public ForeignKey(String manyToField, String foreignKeyClass, String dataSourceName, ForeignKeyRestrictionType restrictionType) {
        this(manyToField, foreignKeyClass, dataSourceName, restrictionType, "name");
    }

    public ForeignKey(String manyToField, String foreignKeyClass, String dataSourceName, ForeignKeyRestrictionType restrictionType, String displayValueProperty) {
        this.manyToField = manyToField;
        this.foreignKeyClass = foreignKeyClass;
        this.dataSourceName = dataSourceName;
        this.restrictionType = restrictionType;
        this.displayValueProperty = displayValueProperty;
    }

    public ForeignKey withManyToField(String manyToField) {
        this.setManyToField(manyToField);
        return this;
    }

    public ForeignKey withOriginatingField(String originatingField) {
        this.setOriginatingField(originatingField);
        return this;
    }

    public ForeignKey withForeignKeyClass(String foreignKeyClass) {
        this.setForeignKeyClass(foreignKeyClass);
        return this;
    }

    public ForeignKey withSortField(String sortField) {
        this.setSortField(sortField);
        return this;
    }
    
    public String getManyToField() {
        return manyToField;
    }
    
    public void setManyToField(String manyToField) {
        this.manyToField = manyToField;
    }
    
    public String getForeignKeyClass() {
        return foreignKeyClass;
    }
    
    public void setForeignKeyClass(String foreignKeyClass) {
        this.foreignKeyClass = foreignKeyClass;
    }

    public String getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(String currentValue) {
        this.currentValue = currentValue;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public ForeignKeyRestrictionType getRestrictionType() {
        return restrictionType;
    }

    public void setRestrictionType(ForeignKeyRestrictionType restrictionType) {
        this.restrictionType = restrictionType;
    }

    public String getDisplayValueProperty() {
        return displayValueProperty;
    }

    public void setDisplayValueProperty(String displayValueProperty) {
        this.displayValueProperty = displayValueProperty;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public Boolean getSortAscending() {
        return sortAscending;
    }

    public void setSortAscending(Boolean sortAscending) {
        this.sortAscending = sortAscending;
    }

    public Boolean getMutable() {
        return mutable;
    }

    public void setMutable(Boolean mutable) {
        this.mutable = mutable;
    }

    public String getOriginatingField() {
        return originatingField;
    }

    public void setOriginatingField(String originatingField) {
        this.originatingField = originatingField;
    }

    public void accept(PersistencePerspectiveItemVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ForeignKey{");
        sb.append("manyToField='").append(manyToField).append('\'');
        sb.append(", originatingField='").append(originatingField).append('\'');
        sb.append(", foreignKeyClass='").append(foreignKeyClass).append('\'');
        sb.append(", currentValue='").append(currentValue).append('\'');
        sb.append(", dataSourceName='").append(dataSourceName).append('\'');
        sb.append(", restrictionType=").append(restrictionType);
        sb.append(", displayValueProperty='").append(displayValueProperty).append('\'');
        sb.append(", sortField='").append(sortField).append('\'');
        sb.append(", sortAscending=").append(sortAscending).append('\'');
        sb.append(", mutable=").append(mutable);
        sb.append('}');
        return sb.toString();
    }

    public ForeignKey cloneForeignKey() {
        ForeignKey foreignKey = new ForeignKey();
        foreignKey.manyToField = manyToField;
        foreignKey.foreignKeyClass = foreignKeyClass;
        foreignKey.currentValue = currentValue;
        foreignKey.dataSourceName = dataSourceName;
        foreignKey.restrictionType = restrictionType;
        foreignKey.displayValueProperty = displayValueProperty;
        foreignKey.sortField = sortField;
        foreignKey.sortAscending = sortAscending;
        foreignKey.mutable = mutable;
        foreignKey.originatingField = originatingField;

        return foreignKey;
    }

    @Override
    public PersistencePerspectiveItem clonePersistencePerspectiveItem() {
        return cloneForeignKey();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!getClass().isAssignableFrom(o.getClass())) return false;

        ForeignKey that = (ForeignKey) o;

        if (currentValue != null ? !currentValue.equals(that.currentValue) : that.currentValue != null) return false;
        if (dataSourceName != null ? !dataSourceName.equals(that.dataSourceName) : that.dataSourceName != null)
            return false;
        if (displayValueProperty != null ? !displayValueProperty.equals(that.displayValueProperty) : that
                .displayValueProperty != null)
            return false;
        if (foreignKeyClass != null ? !foreignKeyClass.equals(that.foreignKeyClass) : that.foreignKeyClass != null)
            return false;
        if (sortAscending != null ? !sortAscending.equals(that.sortAscending) : that.sortAscending != null)
            return false;
        if (sortField != null ? !sortField.equals(that.sortField) : that.sortField != null) return false;
        if (manyToField != null ? !manyToField.equals(that.manyToField) : that.manyToField != null) return false;
        if (mutable != null ? !mutable.equals(that.mutable) : that.mutable != null) return false;
        if (originatingField != null ? !originatingField.equals(that.originatingField) : that.originatingField != null)
            return false;
        if (restrictionType != that.restrictionType) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = manyToField != null ? manyToField.hashCode() : 0;
        result = 31 * result + (originatingField != null ? originatingField.hashCode() : 0);
        result = 31 * result + (foreignKeyClass != null ? foreignKeyClass.hashCode() : 0);
        result = 31 * result + (currentValue != null ? currentValue.hashCode() : 0);
        result = 31 * result + (dataSourceName != null ? dataSourceName.hashCode() : 0);
        result = 31 * result + (sortField != null ? sortField.hashCode() : 0);
        result = 31 * result + (sortAscending != null ? sortAscending.hashCode() : 0);
        result = 31 * result + (restrictionType != null ? restrictionType.hashCode() : 0);
        result = 31 * result + (displayValueProperty != null ? displayValueProperty.hashCode() : 0);
        result = 31 * result + (mutable != null ? mutable.hashCode() : 0);
        return result;
    }
}
