package org.broadleafcommerce.openadmin.client.dto;

import org.broadleafcommerce.openadmin.client.dto.visitor.MetadataVisitor;

/**
 * @author Jeff Fischer
 */
public class AdornedTargetCollectionMetadata extends CollectionMetadata {

    private boolean ignoreAdornedProperties;
    private String parentObjectClass;
    private String[] maintainedAdornedTargetFields = {};
    private String[] gridVisibleFields = {};

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

        return super.populate(metadata);
    }

    @Override
    public FieldMetadata cloneFieldMetadata() {
        AdornedTargetCollectionMetadata metadata = new AdornedTargetCollectionMetadata();
        return populate(metadata);
    }
}
