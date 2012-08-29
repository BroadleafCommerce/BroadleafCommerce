package org.broadleafcommerce.common.presentation;

import org.broadleafcommerce.common.presentation.client.OperationType;

/**
 * Annotation for defining how CRUD operations are performed on an advanced collection in Broadleaf Commerce.
 * This is an advanced configuration, as the default operation settings are appropriate in most cases.
 *
 * @author Jeff Fischer
 */
public @interface AdminPresentationOperationTypes {

    /**
     * How should the system execute an addition for this item
     * <p/>
     * OperationType BASIC will result in the item being inserted
     * OperationType ADORNEDTARGETLIST will result in a adorned target entity being added (not either of the associated entities).
     * CrossSaleProductImpl is an example of an adorned target entity, since it adds additional fields around the target Product entity.
     * OperationType MAP will result in the item being added to the requisite map in the containing entity.
     *
     * @return the type of the add operation
     */
    OperationType addType() default OperationType.BASIC;

    /**
     * How should the system execute an update for this item
     * <p/>
     * OperationType BASIC will result in the item being updated based on it's primary key
     * OperationType ADORNEDTARGETLIST will result in a join structure entity being updated (not either of the associated entities).
     * CrossSaleProductImpl is an example of an adorned target entity, since it adds additional fields around the target Product entity.
     * OperationType MAP will result in the item being updated to the requisite map in the containing entity.
     *
     * @return the type of the update operation
     */
    OperationType updateType() default OperationType.BASIC;

    /**
     * How should the system execute a removal of this item.
     * <p/>
     * OperationType BASIC will result in the item being removed based on its primary key
     * OperationType NONDESTRUCTIVEREMOVE will result in the item being removed from the containing list in the containing entity. This
     * is useful when you don't want the item to actually be deleted, but simply removed from the parent collection.
     * OperationType ADORNEDTARGETLIST will result in a join structure being deleted (not either of the associated entities).
     * CrossSaleProductImpl is an example of an adorned target entity, since it adds additional fields around the target Product entity.
     * OperationType MAP will result in the item being removed from the requisite map in the containing entity.
     *
     * @return the type of remove operation
     */
    OperationType removeType() default OperationType.BASIC;

    /**
     * How should the system execute a fetch
     * <p/>
     * OperationType BASIC will result in a search for items having one or more basic properties matches
     * OperationType ADORNEDTARGETLIST will result in search for target items that match the parent association in the adorned target entity.
     * CrossSaleProductImpl is an example of an adorned target entity, since it adds additional fields around the target Product entity.
     * OperationType MAP will result retrieval of all map entries for the requisite map in the containing entity.
     *
     * @return the type of the fetch operation
     */
    OperationType fetchType() default OperationType.BASIC;

    /**
     * OperationType values are generally ignored for inspect and should be defined as BASIC for consistency in most circumstances.
     * This API is meant to support future persistence modules where specialized inspect phase management may be required.
     *
     * @return the type of the inspect operation
     */
    OperationType inspectType() default OperationType.BASIC;

}
