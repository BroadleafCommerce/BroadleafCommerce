package org.broadleafcommerce.openadmin.client.dto.override;

/**
 * @author Jeff Fischer
 */
public class AdornedTargetCollectionMetadataOverride extends CollectionMetadataOverride {

    private String configurationKey;
    private String parentObjectProperty;
    private String parentObjectIdProperty;
    private String targetObjectProperty;
    private String[] maintainedAdornedTargetFields;
    private String[] gridVisibleFields;
    private String targetObjectIdProperty;
    private String sortProperty;
    private Boolean sortAscending;
    private Boolean ignoreAdornedProperties;

    public String getConfigurationKey() {
        return configurationKey;
    }

    public void setConfigurationKey(String configurationKey) {
        this.configurationKey = configurationKey;
    }

    public String[] getGridVisibleFields() {
        return gridVisibleFields;
    }

    public void setGridVisibleFields(String[] gridVisibleFields) {
        this.gridVisibleFields = gridVisibleFields;
    }

    public Boolean isIgnoreAdornedProperties() {
        return ignoreAdornedProperties;
    }

    public void setIgnoreAdornedProperties(Boolean ignoreAdornedProperties) {
        this.ignoreAdornedProperties = ignoreAdornedProperties;
    }

    public String[] getMaintainedAdornedTargetFields() {
        return maintainedAdornedTargetFields;
    }

    public void setMaintainedAdornedTargetFields(String[] maintainedAdornedTargetFields) {
        this.maintainedAdornedTargetFields = maintainedAdornedTargetFields;
    }

    public String getParentObjectIdProperty() {
        return parentObjectIdProperty;
    }

    public void setParentObjectIdProperty(String parentObjectIdProperty) {
        this.parentObjectIdProperty = parentObjectIdProperty;
    }

    public String getParentObjectProperty() {
        return parentObjectProperty;
    }

    public void setParentObjectProperty(String parentObjectProperty) {
        this.parentObjectProperty = parentObjectProperty;
    }

    public Boolean isSortAscending() {
        return sortAscending;
    }

    public void setSortAscending(Boolean sortAscending) {
        this.sortAscending = sortAscending;
    }

    public String getSortProperty() {
        return sortProperty;
    }

    public void setSortProperty(String sortProperty) {
        this.sortProperty = sortProperty;
    }

    public String getTargetObjectIdProperty() {
        return targetObjectIdProperty;
    }

    public void setTargetObjectIdProperty(String targetObjectIdProperty) {
        this.targetObjectIdProperty = targetObjectIdProperty;
    }

    public String getTargetObjectProperty() {
        return targetObjectProperty;
    }

    public void setTargetObjectProperty(String targetObjectProperty) {
        this.targetObjectProperty = targetObjectProperty;
    }
}
