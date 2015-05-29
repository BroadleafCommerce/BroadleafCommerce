/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.common.copy;

/**
 * Describes an entity (or @Embeddable) that is able to be cloned for the purpose of multiple tenancy.
 *
 * @author Jeff Fischer
 */
public interface MultiTenantCloneable<T> {

    /**
     * Clone this entity for the purpose of multiple tenancy. Note, extending classes should follow this pattern:
     * </p>
     * <code>
     * public CreateResponse&lt;MyClass&gt; createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
     *      CreateResponse&lt;MyClass&gt; createResponse = super.createOrRetrieveCopyInstance(context);
     *      if (createResponse.isAlreadyPopulated()) {
     *          return createResponse;
     *      }
     *      MyClass myClone = createResponse.getClone();
     *
     *      //copy extended field values on myClone here
     *
     *      return createResponse;
     * }
     * </code>
     * </p>
     * Support should also be added for @Embeddable classes that contribute fields (collections or basic) to a cloneable entity:
     * </p>
     * <code>
     * public CreateResponse&lt;G extends MyClass&gt; createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
     *      CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
     *      MyClass myClone = createResponse.getClone();
     *
     *      //copy extended field values on myClone here
     *
     *      return createResponse;
     * }
     * </code>
     *
     * @param context a context object providing persistence and library functionality for copying entities
     * @return the resulting copy container, possibly already persisted
     * @throws CloneNotSupportedException if there's a problem detected with the cloning configuration
     */
    public <G extends T> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException;
}
