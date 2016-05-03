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

import org.broadleafcommerce.common.presentation.client.AddMethodType;
import org.broadleafcommerce.openadmin.dto.visitor.MetadataVisitor;

/**
 * @author Jeff Fischer
 */
public class BasicCollectionMetadata extends CollectionMetadata {

    private AddMethodType addMethodType;

    private String sortProperty;

    private String selectizeVisibleField;

    public AddMethodType getAddMethodType() {
        return addMethodType;
    }

    public void setAddMethodType(AddMethodType addMethodType) {
        this.addMethodType = addMethodType;
    }

    public String getSortProperty() {
        return sortProperty;
    }

    public void setSortProperty(String sortProperty) {
        this.sortProperty = sortProperty;
    }

    public String getSelectizeVisibleField() {
        return selectizeVisibleField;
    }

    public void setSelectizeVisibleField(String selectizeVisibleField) {
        this.selectizeVisibleField = selectizeVisibleField;
    }

    @Override
    public void accept(MetadataVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    protected FieldMetadata populate(FieldMetadata metadata) {
        ((BasicCollectionMetadata) metadata).addMethodType = addMethodType;
        ((BasicCollectionMetadata) metadata).sortProperty = sortProperty;
        ((BasicCollectionMetadata) metadata).selectizeVisibleField = selectizeVisibleField;
        return super.populate(metadata);
    }

    @Override
    public FieldMetadata cloneFieldMetadata() {
        BasicCollectionMetadata basicCollectionMetadata = new BasicCollectionMetadata();
        return populate(basicCollectionMetadata);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!getClass().isAssignableFrom(o.getClass())) return false;
        if (!super.equals(o)) return false;

        BasicCollectionMetadata that = (BasicCollectionMetadata) o;

        if (!addMethodType.equals(that.addMethodType)) return false;
        if (!selectizeVisibleField.equals(that.selectizeVisibleField)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (addMethodType != null ? addMethodType.hashCode() : 0);
        result = 31 * result + (selectizeVisibleField != null ? selectizeVisibleField.hashCode() : 0);
        return result;
    }
}
