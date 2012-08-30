package org.broadleafcommerce.openadmin.client.dto;

/**
 * @author Jeff Fischer
 */
public abstract class CollectionMetadata extends FieldMetadata {

    private PersistencePerspective persistencePerspective;
    private String collectionCeilingEntity;
    private String targetElementId;
    private String dataSourceName;

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

    public String getTargetElementId() {
        return targetElementId;
    }

    public void setTargetElementId(String targetElementId) {
        this.targetElementId = targetElementId;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    @Override
    protected FieldMetadata populate(FieldMetadata metadata) {
        super.populate(metadata);
        ((CollectionMetadata) metadata).setPersistencePerspective(persistencePerspective.clonePersistencePerspective());
        ((CollectionMetadata) metadata).setCollectionCeilingEntity(collectionCeilingEntity);
        ((CollectionMetadata) metadata).setTargetElementId(targetElementId);
        ((CollectionMetadata) metadata).setDataSourceName(dataSourceName);

        return metadata;
    }

}
