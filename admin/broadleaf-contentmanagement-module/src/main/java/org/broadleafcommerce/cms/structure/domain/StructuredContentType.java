/*
 * #%L
 * BroadleafCommerce CMS Module
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
package org.broadleafcommerce.cms.structure.domain;

import org.broadleafcommerce.common.copy.MultiTenantCloneable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * A content type corresponds to an area where content should be targeted.   For example,
 * a valid content name would be "homepage banner" or "cart rhs ad".
 * <br>
 * While typically used for placement, a content type can also be used just to describe
 * the fields.    For example, a content type of message might be used to store messages
 * that can be retrieved by name.
 * <br>
 * The custom fields associated by with a <code>StructuredContentType</code>
 * <br>
 *
 * @author bpolster.
 */
public interface StructuredContentType extends Serializable,MultiTenantCloneable<StructuredContentType> {

    /**
     * Gets the primary key.
     *
     * @return the primary key
     */
    @Nullable
    public Long getId();


    /**
     * Sets the primary key.
     *
     * @param id the new primary key
     */
    public void setId(@Nullable Long id);

    /**
     * Gets the name.
     *
     * @return the name
     */
    @Nonnull
    String getName();

    /**
     * Sets the name.
     */
    void setName(@Nonnull String name);

    /**
     * Gets the description.
     * @return
     */
    @Nullable
    String getDescription();

    /**
     * Sets the description.
     */
    void setDescription(@Nullable String description);

    /**
     * Returns the template associated with this content type.
     * @return
     */
    @Nonnull
    StructuredContentFieldTemplate getStructuredContentFieldTemplate();

    /**
     * Sets the template associated with this content type.
     * @param scft
     */
    void setStructuredContentFieldTemplate(@Nonnull StructuredContentFieldTemplate scft);
}
