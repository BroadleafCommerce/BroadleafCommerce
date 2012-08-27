/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.client.dto;

import com.google.gwt.user.client.rpc.IsSerializable;
import org.broadleafcommerce.common.presentation.OperationType;

import java.io.Serializable;

/**
 * @author jfischer
 */
public class OperationTypes implements IsSerializable, Serializable {

    private static final long serialVersionUID = 1L;

    private OperationType fetchType = OperationType.ENTITY;
    private OperationType removeType = OperationType.ENTITY;
    private OperationType addType = OperationType.ENTITY;
    private OperationType updateType = OperationType.ENTITY;
    private OperationType inspectType = OperationType.ENTITY;

    public OperationTypes() {
        //do nothing
    }

    public OperationTypes(OperationType fetchType, OperationType removeType, OperationType addType, OperationType updateType, OperationType inspectType) {
        this.removeType = removeType;
        this.addType = addType;
        this.updateType = updateType;
        this.fetchType = fetchType;
        this.inspectType = inspectType;
    }

    /**
     * How should the system execute a removal of this item.
     * <p/>
     * OperationType ENTITY will result in the item being removed based on its primary key
     * OperationType FOREIGNKEY will result in the item being removed from the containing list in the containing entity. This
     * is useful when you don't want the item to actually be deleted, but simply removed from the parent collection.
     * OperationType JOINSTRUCTURE will result in a join structure being deleted (not either of the associated entities).
     * org.broadleafcommerce.core.catalog.domain.CategoryProductXrefImpl is an example of a join structure entity.
     * OperationType MAPSTRUCTURE will result in the item being removed from the requisite map in the containing entity.
     *
     * @return the type of remove operation
     */
    public OperationType getRemoveType() {
        return removeType;
    }

    /**
     * How should the system execute a removal of this item.
     * <p/>
     * OperationType ENTITY will result in the item being removed based on its primary key
     * OperationType FOREIGNKEY will result in the item being removed from the containing list in the containing entity. This
     * is useful when you don't want the item to be removed to actually be deleted, but simply removed from the parent collection.
     * OperationType JOINSTRUCTURE will result in a join structure being deleted (not either of the associated entities).
     * org.broadleafcommerce.core.catalog.domain.CategoryProductXrefImpl is an example of a join structure entity.
     * OperationType MAPSTRUCTURE will result in the item being removed from the requisite map in the containing entity.
     *
     * @param removeType
     */
    public void setRemoveType(OperationType removeType) {
        this.removeType = removeType;
    }

    /**
     * How should the system execute an addition for this item
     * <p/>
     * OperationType ENTITY will result in the item being inserted
     * OperationType FOREIGNKEY is not supported and will result in the same behavior as ENTITY. Note, any foreign key associations in the
     * persistence perspective (@see PersistencePerspective) will be honored during the ENTITY based add.
     * OperationType JOINSTRUCTURE will result in a join structure entity being added (not either of the associated entities).
     * org.broadleafcommerce.core.catalog.domain.CategoryProductXrefImpl is an example of a join structure entity.
     * OperationType MAPSTRUCTURE will result in the item being added to the requisite map in the containing entity.
     *
     * @return the type of the add operation
     */
    public OperationType getAddType() {
        return addType;
    }

    /**
     * How should the system execute an addition for this item
     * <p/>
     * OperationType ENTITY will result in the item being inserted
     * OperationType FOREIGNKEY is not supported and will result in the same behavior as ENTITY. Note, any foreign key associations in the
     * persistence perspective (@see PersistencePerspective) will be honored during the ENTITY based add.
     * OperationType JOINSTRUCTURE will result in a join structure entity being added (not either of the associated entities).
     * org.broadleafcommerce.core.catalog.domain.CategoryProductXrefImpl is an example of a join structure entity.
     * OperationType MAPSTRUCTURE will result in the item being added to the requisite map in the containing entity.
     *
     * @param addType
     */
    public void setAddType(OperationType addType) {
        this.addType = addType;
    }

    /**
     * How should the system execute an update for this item
     * <p/>
     * OperationType ENTITY will result in the item being updated based on it's primary key
     * OperationType FOREIGNKEY is not supported and will result in the same behavior as ENTITY. Note, any foreign key associations in the
     * persistence perspective (@see PersistencePerspective) will be honored during the ENTITY based update.
     * OperationType JOINSTRUCTURE will result in a join structure entity being updated (not either of the associated entities).
     * org.broadleafcommerce.core.catalog.domain.CategoryProductXrefImpl is an example of a join structure entity.
     * OperationType MAPSTRUCTURE will result in the item being updated to the requisite map in the containing entity.
     *
     * @return the type of the update operation
     */
    public OperationType getUpdateType() {
        return updateType;
    }

    /**
     * How should the system execute an update for this item
     * <p/>
     * OperationType ENTITY will result in the item being updated based on it's primary key
     * OperationType FOREIGNKEY is not supported and will result in the same behavior as ENTITY. Note, any foreign key associations in the
     * persistence perspective (@see PersistencePerspective) will be honored during the ENTITY based update.
     * OperationType JOINSTRUCTURE will result in a join structure entity being updated (not either of the associated entities).
     * org.broadleafcommerce.core.catalog.domain.CategoryProductXrefImpl is an example of a join structure entity.
     * OperationType MAPSTRUCTURE will result in the item being updated to the requisite map in the containing entity.
     *
     * @param updateType
     */
    public void setUpdateType(OperationType updateType) {
        this.updateType = updateType;
    }

    /**
     * How should the system execute a fetch
     * <p/>
     * OperationType ENTITY will result in a search for items having one or more basic properties matches
     * OperationType FOREINKEY is not support and will result in the same behavior as ENTITY. Note, any foreign key associations will be included
     * as part of the query.
     * OperationType JOINSTRUCTURE will result in search for items that match one of the associations in a join structure. For example, CategoryProductXrefImpl
     * is used in a JoinStructure fetch to retrieve all products for a particular category.
     * OperationType MAPSTRUCTURE will result retrieval of all map entries for the requisite map in the containing entity.
     *
     * @return the type of the fetch operation
     */
    public OperationType getFetchType() {
        return fetchType;
    }

    /**
     * How should the system execute a fetch
     * <p/>
     * OperationType ENTITY will result in a search for items having one or more basic properties matches
     * OperationType FOREINKEY is not support and will result in the same behavior as ENTITY. Note, any foreign key associations will be included
     * as part of the query.
     * OperationType JOINSTRUCTURE will result in search for items that match one of the associations in a join structure. For example, CategoryProductXrefImpl
     * is used in a JoinStructure fetch to retrieve all products for a particular category.
     * OperationType MAPSTRUCTURE will result retrieval of all map entries for the requisite map in the containing entity.
     *
     * @param fetchType
     */
    public void setFetchType(OperationType fetchType) {
        this.fetchType = fetchType;
    }

    /**
     * OperationType values are generally ignored for inspect and should be defined as ENTITY for consistency in most circumstances.
     * This API is meant to support future persistence modules where specialized inspect phase management may be required.
     *
     * @return the type of the inspect operation
     */
    public OperationType getInspectType() {
        return inspectType;
    }

    /**
     * OperationType values are generally ignored for inspect and should be defined as ENTITY for consistency in most circumstances.
     * This API is meant to support future persistence modules where specialized inspect phase management may be required.
     *
     * @param inspectType
     */
    public void setInspectType(OperationType inspectType) {
        this.inspectType = inspectType;
    }

}
