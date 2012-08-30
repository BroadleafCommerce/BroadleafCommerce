package org.broadleafcommerce.openadmin.client.dto;

import org.broadleafcommerce.common.presentation.client.AddType;
import org.broadleafcommerce.openadmin.client.dto.visitor.MetadataVisitor;

/**
 * @author Jeff Fischer
 */
public class BasicCollectionMetadata extends CollectionMetadata {

    private AddType addType;

    public AddType getAddType() {
        return addType;
    }

    public void setAddType(AddType addType) {
        this.addType = addType;
    }

    @Override
    public void accept(MetadataVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    protected FieldMetadata populate(FieldMetadata metadata) {
        ((BasicCollectionMetadata) metadata).addType = addType;
        return super.populate(metadata);
    }

    @Override
    public FieldMetadata cloneFieldMetadata() {
        BasicCollectionMetadata basicCollectionMetadata = new BasicCollectionMetadata();
        return populate(basicCollectionMetadata);
    }
}
