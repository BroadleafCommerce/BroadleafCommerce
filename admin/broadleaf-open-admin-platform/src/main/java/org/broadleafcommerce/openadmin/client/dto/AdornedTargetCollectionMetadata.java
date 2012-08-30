package org.broadleafcommerce.openadmin.client.dto;

import org.broadleafcommerce.openadmin.client.dto.visitor.MetadataVisitor;

/**
 * @author Jeff Fischer
 */
public class AdornedTargetCollectionMetadata extends CollectionMetadata {

    private boolean ignoreAdornedProperties;

    public boolean isIgnoreAdornedProperties() {
        return ignoreAdornedProperties;
    }

    public void setIgnoreAdornedProperties(boolean ignoreAdornedProperties) {
        this.ignoreAdornedProperties = ignoreAdornedProperties;
    }

    @Override
    public void accept(MetadataVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    protected FieldMetadata populate(FieldMetadata metadata) {
        ((AdornedTargetCollectionMetadata) metadata).ignoreAdornedProperties = ignoreAdornedProperties;

        return super.populate(metadata);
    }

    @Override
    public FieldMetadata cloneFieldMetadata() {
        AdornedTargetCollectionMetadata metadata = new AdornedTargetCollectionMetadata();
        return populate(metadata);
    }
}
