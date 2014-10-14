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
 * Describes an entity that is able to be cloned for the purpose of multiple tenancy.
 *
 * @author Jeff Fischer
 */
public interface MultiTenantCloneable<T> {

    /**
     * Clone this entity for the purpose of multiple tenancy. Note, extending classes should follow this pattern:
     * </p>
     * <code>
     * public MyObject clone(MultiTenantCopyContext context, boolean save) throws CloneNotSupportedException {
     *      //we don't want the superclass to persist, we'll take care of that when we're done adding our fields to the clone
     *      MyObject cloned = super.clone(context, false)
     *      //copy extended field values here
     *      return context.conditionallySaveClone(this, cloned, MyObject.class, save);
     * }
     * </code>
     *
     * @param context a context object providing persistence and library functionality for copying entities
     * @param save whether or not the clone should be saved directly after this clone operation
     * @return the resulting copy, possibly persisted
     * @throws CloneNotSupportedException
     */
    public <G extends T> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException;
}
