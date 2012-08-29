package org.broadleafcommerce.openadmin.client.dto;

import org.broadleafcommerce.common.presentation.client.AddType;
import org.broadleafcommerce.openadmin.client.dto.visitor.MetadataVisitor;

/**
 * @author Jeff Fischer
 */
public class CollectionMetadata extends FieldMetadata {

    private AddType addType;
    private PersistencePerspective persistencePerspective;
    private String collectionCeilingEntity;

    public AddType getAddType() {
        return addType;
    }

    public void setAddType(AddType addType) {
        this.addType = addType;
    }

    public PersistencePerspective getPersistencePerspective() {
        return persistencePerspective;
    }

    public void setPersistencePerspective(PersistencePerspective persistencePerspective) {
        this.persistencePerspective = persistencePerspective;
    }

    public String getCollectionCeilingEntity() {
        return collectionCeilingEntity;
    }

    public void setCollectionCeilingEntity(String collectionCeilingEntity) {
        this.collectionCeilingEntity = collectionCeilingEntity;
    }

    @Override
    public FieldMetadata cloneFieldMetadata() {
        CollectionMetadata metadata = new CollectionMetadata();
        metadata.setAddType(addType);
        metadata.setPersistencePerspective(persistencePerspective.clonePersistencePerspective());
        metadata.setCollectionCeilingEntity(collectionCeilingEntity);

        metadata = (CollectionMetadata) populate(metadata);

        return metadata;
    }

    @Override
    public void accept(MetadataVisitor visitor) {
        visitor.visit(this);
    }
}
