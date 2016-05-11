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

import java.util.Arrays;

/**
 * @author Jeff Fischer
 */
public abstract class CollectionMetadata extends FieldMetadata {

    private PersistencePerspective persistencePerspective;
    private String collectionCeilingEntity;
    private boolean mutable = true;
    private String[] customCriteria;

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

    public boolean isMutable() {
        return mutable;
    }

    public void setMutable(boolean mutable) {
        this.mutable = mutable;
    }

    public String[] getCustomCriteria() {
        return customCriteria;
    }

    public void setCustomCriteria(String[] customCriteria) {
        this.customCriteria = customCriteria;
    }

    @Override
    protected FieldMetadata populate(FieldMetadata metadata) {
        super.populate(metadata);
        ((CollectionMetadata) metadata).setPersistencePerspective(persistencePerspective.clonePersistencePerspective());
        ((CollectionMetadata) metadata).setCollectionCeilingEntity(collectionCeilingEntity);
        ((CollectionMetadata) metadata).setMutable(mutable);
        ((CollectionMetadata) metadata).setCustomCriteria(customCriteria);
        return metadata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!getClass().isAssignableFrom(o.getClass())) return false;

        CollectionMetadata metadata = (CollectionMetadata) o;

        if (mutable != metadata.mutable) return false;
        if (collectionCeilingEntity != null ? !collectionCeilingEntity.equals(metadata.collectionCeilingEntity) : metadata.collectionCeilingEntity != null)
            return false;
        if (!Arrays.equals(customCriteria, metadata.customCriteria)) return false;
        if (persistencePerspective != null ? !persistencePerspective.equals(metadata.persistencePerspective) : metadata.persistencePerspective != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = persistencePerspective != null ? persistencePerspective.hashCode() : 0;
        result = 31 * result + (collectionCeilingEntity != null ? collectionCeilingEntity.hashCode() : 0);
        result = 31 * result + (mutable ? 1 : 0);
        result = 31 * result + (customCriteria != null ? Arrays.hashCode(customCriteria) : 0);
        return result;
    }
}
