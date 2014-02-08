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

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.broadleafcommerce.openadmin.audit.AdminAuditable;

/**
 * Holds the values for custom fields that are part of a <code>StructuredContent</code> item.
 * <br>
 * Each item maintains a list of its custom fields.    The fields associated with an item are
 * determined by the {@link org.broadleafcommerce.cms.field.domain.FieldDefinition}s  associated
 * with the {@link StructuredContentType}.
 *
 * @see StructuredContentType
 * @see org.broadleafcommerce.cms.field.domain.FieldDefinition
 * @author bpolster
 *
 */
public interface StructuredContentField extends Serializable {

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
     * Returns the fieldKey associated with this field.   The key used for a
     * <code>StructuredContentField</code> is determined by the associated
     * {@link org.broadleafcommerce.cms.field.domain.FieldDefinition} that was used by the
     * Content Management System to create this instance.
     *
     * As an example, a <code>StructuredContentType</code> might be configured to contain a
     * field definition with a key of "targetUrl".
     *
     * @return the key associated with this item
     * @see org.broadleafcommerce.cms.field.domain.FieldDefinition
     */
    @Nonnull
    public String getFieldKey();

    /**
     * Sets the fieldKey.
     * @param fieldKey
     * @see org.broadleafcommerce.cms.field.domain.FieldDefinition
     */
    public void setFieldKey(@Nonnull String fieldKey);

    /**
     * Returns the value for this custom field.
     *
     * @param value
     */
    public void setValue(@Nonnull String value);

    /**
     * Sets the value of this custom field.
     * @return
     */
    @Nonnull
    public String getValue();

    /**
     * Returns audit information for this content item.
     *
     * @return
     */
    @Nullable
    public AdminAuditable getAuditable();

    /**
     * Sets audit information for this content item.   Default implementations automatically
     * populate this data during persistence.
     *
     * @param auditable
     */
    public void setAuditable(@Nullable AdminAuditable auditable);

}
