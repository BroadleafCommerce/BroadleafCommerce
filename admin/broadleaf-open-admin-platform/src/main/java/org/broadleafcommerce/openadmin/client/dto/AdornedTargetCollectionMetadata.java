package org.broadleafcommerce.openadmin.client.dto;

import org.broadleafcommerce.openadmin.client.dto.visitor.MetadataVisitor;

/**
 * @author Jeff Fischer
 */
public class AdornedTargetCollectionMetadata extends CollectionMetadata {

    private boolean ignoreAdornedProperties;
    private String parentObjectClass;

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

    @Override
    public void accept(MetadataVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    protected FieldMetadata populate(FieldMetadata metadata) {
        ((AdornedTargetCollectionMetadata) metadata).ignoreAdornedProperties = ignoreAdornedProperties;
        ((AdornedTargetCollectionMetadata) metadata).parentObjectClass = parentObjectClass;

        return super.populate(metadata);
    }

    @Override
    public FieldMetadata cloneFieldMetadata() {
        AdornedTargetCollectionMetadata metadata = new AdornedTargetCollectionMetadata();
        return populate(metadata);
    }
}
