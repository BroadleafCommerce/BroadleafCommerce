/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
        ((CollectionMetadata) metadata).setTab(getTab());
        ((CollectionMetadata) metadata).setTabOrder(getTabOrder());
        return metadata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CollectionMetadata)) return false;

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
