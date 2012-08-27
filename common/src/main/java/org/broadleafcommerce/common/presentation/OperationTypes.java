package org.broadleafcommerce.common.presentation;

/**
 * Annotation for defining how CRUD operations are performed on an advanced collection in Broadleaf Commerce.
 *
 * @author Jeff Fischer
 */
public @interface OperationTypes {

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
    OperationType addType() default OperationType.ENTITY;

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
    OperationType updateType() default OperationType.ENTITY;

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
    OperationType removeType() default OperationType.ENTITY;

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
    OperationType fetchType() default OperationType.ENTITY;

    /**
     * OperationType values are generally ignored for inspect and should be defined as ENTITY for consistency in most circumstances.
     * This API is meant to support future persistence modules where specialized inspect phase management may be required.
     *
     * @return the type of the inspect operation
     */
    OperationType inspectType() default OperationType.ENTITY;

}
