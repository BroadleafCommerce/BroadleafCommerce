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
    private String selectizeVisibleField;
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

    public String getSelectizeVisibleField() {
        return selectizeVisibleField;
    }

    public void setSelectizeVisibleField(String selectizeVisibleField) {
        this.selectizeVisibleField = selectizeVisibleField;
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
        ((AdornedTargetCollectionMetadata) metadata).selectizeVisibleField = selectizeVisibleField;
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
        if (selectizeVisibleField != null ? !selectizeVisibleField.equals(metadata.selectizeVisibleField) : metadata.selectizeVisibleField != null)
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
        result = 31 * result + (selectizeVisibleField != null ? selectizeVisibleField.hashCode() : 0);
        return result;
    }
}
