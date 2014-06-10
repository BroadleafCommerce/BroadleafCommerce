/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
        result = 31 * result + (restrictionType != null ? restrictionType.hashCode() : 0);
        result = 31 * result + (displayValueProperty != null ? displayValueProperty.hashCode() : 0);
        result = 31 * result + (mutable != null ? mutable.hashCode() : 0);
        return result;
    }
}
