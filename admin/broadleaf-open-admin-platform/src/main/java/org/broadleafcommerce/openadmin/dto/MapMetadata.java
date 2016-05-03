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

import org.broadleafcommerce.openadmin.dto.visitor.MetadataVisitor;

/**
 * @author Jeff Fischer
 */
public class MapMetadata extends CollectionMetadata {

    private String valueClassName;
    private String[][] keys;
    private boolean isSimpleValue;
    private String mediaField;
    private String mapKeyValueProperty;
    private String mapKeyOptionEntityClass;
    private String mapKeyOptionEntityDisplayField;
    private String mapKeyOptionEntityValueField;
    private Boolean forceFreeFormKeys;
    private String toOneTargetProperty;
    private String toOneParentProperty;

    public String getValueClassName() {
        return valueClassName;
    }

    public void setValueClassName(String valueClassName) {
        this.valueClassName = valueClassName;
    }

    public boolean isSimpleValue() {
        return isSimpleValue;
    }

    public void setSimpleValue(boolean simpleValue) {
        isSimpleValue = simpleValue;
    }

    public String getMediaField() {
        return mediaField;
    }

    public void setMediaField(String mediaField) {
        this.mediaField = mediaField;
    }

    public String[][] getKeys() {
        return keys;
    }

    public void setKeys(String[][] keys) {
        this.keys = keys;
    }
    
    public String getMapKeyValueProperty() {
        return mapKeyValueProperty;
    }
    
    public void setMapKeyValueProperty(String mapKeyValueProperty) {
        this.mapKeyValueProperty = mapKeyValueProperty;
    }

    public String getMapKeyOptionEntityClass() {
        return mapKeyOptionEntityClass;
    }

    public void setMapKeyOptionEntityClass(String mapKeyOptionEntityClass) {
        this.mapKeyOptionEntityClass = mapKeyOptionEntityClass;
    }

    public String getMapKeyOptionEntityDisplayField() {
        return mapKeyOptionEntityDisplayField;
    }

    public void setMapKeyOptionEntityDisplayField(String mapKeyOptionEntityDisplayField) {
        this.mapKeyOptionEntityDisplayField = mapKeyOptionEntityDisplayField;
    }

    public String getMapKeyOptionEntityValueField() {
        return mapKeyOptionEntityValueField;
    }

    public void setMapKeyOptionEntityValueField(String mapKeyOptionEntityValueField) {
        this.mapKeyOptionEntityValueField = mapKeyOptionEntityValueField;
    }

    public Boolean getForceFreeFormKeys() {
        return forceFreeFormKeys;
    }

    public void setForceFreeFormKeys(Boolean forceFreeFormKeys) {
        this.forceFreeFormKeys = forceFreeFormKeys;
    }

    public String getToOneTargetProperty() {
        return toOneTargetProperty;
    }

    public void setToOneTargetProperty(String toOneTargetProperty) {
        this.toOneTargetProperty = toOneTargetProperty;
    }

    public String getToOneParentProperty() {
        return toOneParentProperty;
    }

    public void setToOneParentProperty(String toOneParentProperty) {
        this.toOneParentProperty = toOneParentProperty;
    }

    @Override
    public void accept(MetadataVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    protected FieldMetadata populate(FieldMetadata metadata) {
        ((MapMetadata) metadata).valueClassName = valueClassName;
        ((MapMetadata) metadata).isSimpleValue = isSimpleValue;
        ((MapMetadata) metadata).mediaField = mediaField;
        ((MapMetadata) metadata).keys = keys;
        ((MapMetadata) metadata).mapKeyValueProperty = mapKeyValueProperty;
        ((MapMetadata) metadata).mapKeyOptionEntityClass = mapKeyOptionEntityClass;
        ((MapMetadata) metadata).mapKeyOptionEntityDisplayField = mapKeyOptionEntityDisplayField;
        ((MapMetadata) metadata).mapKeyOptionEntityValueField = mapKeyOptionEntityValueField;
        ((MapMetadata) metadata).forceFreeFormKeys = forceFreeFormKeys;
        ((MapMetadata) metadata).toOneTargetProperty = toOneTargetProperty;
        ((MapMetadata) metadata).toOneParentProperty = toOneParentProperty;

        return super.populate(metadata);
    }

    @Override
    public FieldMetadata cloneFieldMetadata() {
        MapMetadata metadata = new MapMetadata();
        return populate(metadata);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!getClass().isAssignableFrom(o.getClass())) return false;
        if (!super.equals(o)) return false;

        MapMetadata metadata = (MapMetadata) o;

        if (isSimpleValue != metadata.isSimpleValue) return false;
        if (mapKeyValueProperty != null ? !mapKeyValueProperty.equals(metadata.mapKeyValueProperty) : metadata.mapKeyValueProperty != null)
            return false;
        if (mapKeyOptionEntityClass != null ? !mapKeyOptionEntityClass.equals(metadata.mapKeyOptionEntityClass) : metadata.mapKeyOptionEntityClass != null)
            return false;
        if (mapKeyOptionEntityDisplayField != null ? !mapKeyOptionEntityDisplayField.equals(metadata.mapKeyOptionEntityDisplayField) : metadata.mapKeyOptionEntityDisplayField != null)
            return false;
        if (mapKeyOptionEntityValueField != null ? !mapKeyOptionEntityValueField.equals(metadata.mapKeyOptionEntityValueField) : metadata.mapKeyOptionEntityValueField != null)
            return false;
        if (mediaField != null ? !mediaField.equals(metadata.mediaField) : metadata.mediaField != null) return false;
        if (valueClassName != null ? !valueClassName.equals(metadata.valueClassName) : metadata.valueClassName != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (valueClassName != null ? valueClassName.hashCode() : 0);
        result = 31 * result + (isSimpleValue ? 1 : 0);
        result = 31 * result + (mediaField != null ? mediaField.hashCode() : 0);
        result = 31 * result + (mapKeyValueProperty != null ? mapKeyValueProperty.hashCode() : 0);
        result = 31 * result + (mapKeyOptionEntityClass != null ? mapKeyOptionEntityClass.hashCode() : 0);
        result = 31 * result + (mapKeyOptionEntityDisplayField != null ? mapKeyOptionEntityDisplayField.hashCode() : 0);
        result = 31 * result + (mapKeyOptionEntityValueField != null ? mapKeyOptionEntityValueField.hashCode() : 0);
        return result;
    }
}
