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

package org.broadleafcommerce.common.presentation;

import org.broadleafcommerce.common.presentation.client.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for defining how CRUD operations are performed on an advanced collection in Broadleaf Commerce.
 * This is an advanced configuration, as the default operation settings are appropriate in most cases.
 *
 * @author Jeff Fischer
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface AdminPresentationOperationTypes {

    /**
     * <p>How should the system execute an addition for this item</p>
     * 
     * <p>OperationType BASIC will result in the item being inserted<BR>
     * OperationType ADORNEDTARGETLIST will result in a adorned target entity being added (not either of the associated entities).<BR>
     * CrossSaleProductImpl is an example of an adorned target entity, since it adds additional fields around the target Product entity.<BR>
     * OperationType MAP will result in the item being added to the requisite map in the containing entity.</p>
     *
     * @return the type of the add operation
     */
    OperationType addType() default OperationType.BASIC;

    /**
     * <p>How should the system execute an update for this item</p>
     * 
     * <p>OperationType BASIC will result in the item being updated based on it's primary key<BR>
     * OperationType ADORNEDTARGETLIST will result in a join structure entity being updated (not either of the associated entities).<BR>
     * CrossSaleProductImpl is an example of an adorned target entity, since it adds additional fields around the target Product entity.<BR>
     * OperationType MAP will result in the item being updated to the requisite map in the containing entity.</p>
     *
     * @return the type of the update operation
     */
    OperationType updateType() default OperationType.BASIC;

    /**
     * <p>How should the system execute a removal of this item.</p>
     * 
     * <p>OperationType BASIC will result in the item being removed based on its primary key<BR>
     * OperationType NONDESTRUCTIVEREMOVE will result in the item being removed from the containing list in the containing entity. This
     * is useful when you don't want the item to actually be deleted, but simply removed from the parent collection.<BR>
     * OperationType ADORNEDTARGETLIST will result in a join structure being deleted (not either of the associated entities).<BR>
     * CrossSaleProductImpl is an example of an adorned target entity, since it adds additional fields around the target Product entity.<BR>
     * OperationType MAP will result in the item being removed from the requisite map in the containing entity.</p>
     *
     * @return the type of remove operation
     */
    OperationType removeType() default OperationType.BASIC;

    /**
     * <p>How should the system execute a fetch</p>
     * 
     * <p>OperationType BASIC will result in a search for items having one or more basic properties matches<BR>
     * OperationType ADORNEDTARGETLIST will result in search for target items that match the parent association in the adorned target entity.<BR>
     * CrossSaleProductImpl is an example of an adorned target entity, since it adds additional fields around the target Product entity.<BR>
     * OperationType MAP will result retrieval of all map entries for the requisite map in the containing entity.</p>
     *
     * @return the type of the fetch operation
     */
    OperationType fetchType() default OperationType.BASIC;

    /**
     * <p>OperationType values are generally ignored for inspect and should be defined as BASIC for consistency in most circumstances.
     * This API is meant to support future persistence modules where specialized inspect phase management may be required.</p>
     *
     * @return the type of the inspect operation
     */
    OperationType inspectType() default OperationType.BASIC;

}
