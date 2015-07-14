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

import org.broadleafcommerce.common.presentation.client.AdornedTargetAddMethodType;
import org.broadleafcommerce.openadmin.dto.visitor.MetadataVisitor;

import java.util.Arrays;

/**
 * @author Jeff Fischer
 */
public class AdornedTargetCollectionMetadata extends CollectionMetadata {

    private boolean ignoreAdornedProperties;
    private String parentObjectClass;
    private String[] maintainedAdornedTargetFields = {};
    private String[] gridVisibleFields = {};
    private AdornedTargetAddMethodType adornedTargetAddMethodType;

    public AdornedTargetAddMethodType getAdornedTargetAddMethodType() {
        return adornedTargetAddMethodType;
    }

    public void setAdornedTargetAddMethodType(AdornedTargetAddMethodType adornedTargetAddMethodType) {
        this.adornedTargetAddMethodType = adornedTargetAddMethodType;
    }

    public boolean isIgnoreAdornedProperties() {
        return ignoreAdornedProperties;
    }

    public void setIgnoreAdornedProperties(boolean ignoreAdornedProperties) {
        this.ignoreAdornedProperties = ignoreAdornedProperties;
    }

    public String getParentObjectClass() {
        return parentObjectClass;
    }

    public void setParentObjectClass(String parentObjectClass) {
        this.parentObjectClass = parentObjectClass;
    }

    public String[] getGridVisibleFields() {
        return gridVisibleFields;
    }

    public void setGridVisibleFields(String[] gridVisibleFields) {
        this.gridVisibleFields = gridVisibleFields;
    }

    public String[] getMaintainedAdornedTargetFields() {
        return maintainedAdornedTargetFields;
    }

    public void setMaintainedAdornedTargetFields(String[] maintainedAdornedTargetFields) {
        this.maintainedAdornedTargetFields = maintainedAdornedTargetFields;
    }

    @Override
    public void accept(MetadataVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    protected FieldMetadata populate(FieldMetadata metadata) {
        ((AdornedTargetCollectionMetadata) metadata).ignoreAdornedProperties = ignoreAdornedProperties;
        ((AdornedTargetCollectionMetadata) metadata).parentObjectClass = parentObjectClass;
        ((AdornedTargetCollectionMetadata) metadata).maintainedAdornedTargetFields = maintainedAdornedTargetFields;
        ((AdornedTargetCollectionMetadata) metadata).gridVisibleFields = gridVisibleFields;
        ((AdornedTargetCollectionMetadata) metadata).adornedTargetAddMethodType = adornedTargetAddMethodType;

        return super.populate(metadata);
    }

    @Override
    public FieldMetadata cloneFieldMetadata() {
        AdornedTargetCollectionMetadata metadata = new AdornedTargetCollectionMetadata();
        return populate(metadata);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!getClass().isAssignableFrom(o.getClass())) return false;
        if (!super.equals(o)) return false;

        AdornedTargetCollectionMetadata metadata = (AdornedTargetCollectionMetadata) o;

        if (ignoreAdornedProperties != metadata.ignoreAdornedProperties) return false;
        if (!Arrays.equals(gridVisibleFields, metadata.gridVisibleFields)) return false;
        if (!Arrays.equals(maintainedAdornedTargetFields, metadata.maintainedAdornedTargetFields)) return false;
        if (parentObjectClass != null ? !parentObjectClass.equals(metadata.parentObjectClass) : metadata.parentObjectClass != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (ignoreAdornedProperties ? 1 : 0);
        result = 31 * result + (parentObjectClass != null ? parentObjectClass.hashCode() : 0);
        result = 31 * result + (maintainedAdornedTargetFields != null ? Arrays.hashCode(maintainedAdornedTargetFields) : 0);
        result = 31 * result + (gridVisibleFields != null ? Arrays.hashCode(gridVisibleFields) : 0);
        return result;
    }
}
