/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.client.dto;

import com.google.gwt.user.client.rpc.IsSerializable;
import org.broadleafcommerce.common.presentation.ForeignKeyRestrictionType;
import org.broadleafcommerce.openadmin.client.dto.visitor.PersistencePerspectiveItemVisitor;

import java.io.Serializable;


/**
 * 
 * @author jfischer
 *
 */
public class ForeignKey implements IsSerializable, Serializable, PersistencePerspectiveItem {

	private static final long serialVersionUID = 1L;
	
	private String manyToField;
	private String foreignKeyClass;
	private String currentValue;
	private String dataSourceName;
	private ForeignKeyRestrictionType restrictionType = ForeignKeyRestrictionType.ID_EQ;
	private String displayValueProperty = "name";
	
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

	public void accept(PersistencePerspectiveItemVisitor visitor) {
        visitor.visit(this);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(manyToField);
        sb.append(foreignKeyClass);
        sb.append(currentValue);
        sb.append(dataSourceName);
        sb.append(restrictionType);
        sb.append(displayValueProperty);

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

        return foreignKey;
    }

    @Override
    public PersistencePerspectiveItem clonePersistencePerspectiveItem() {
        return cloneForeignKey();
    }
}
