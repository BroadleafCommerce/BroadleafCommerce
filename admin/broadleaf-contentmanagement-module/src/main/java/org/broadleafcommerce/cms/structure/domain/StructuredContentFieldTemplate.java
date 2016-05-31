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

import org.broadleafcommerce.cms.field.domain.FieldGroup;
import org.broadleafcommerce.common.copy.MultiTenantCloneable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.List;

/**
 * A structured content field template holds the structure for a
 * structured content.
 *
 * For example, an 'Ad' template might describe the fields 'Ad URL' and
 * 'Target URL'.   The 'Ad' template might be used in multiple StructuredContentType
 * instances such as 'Home Page Banner Ad' or 'Cart Bottom Ad', etc.
 *
 * @author bpolster
 */
public interface StructuredContentFieldTemplate extends Serializable, MultiTenantCloneable<StructuredContentFieldTemplate> {

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
     * Returns the list of the field groups for this template.
     * @return a list of FieldGroups associated with this template
     */
    @Nullable
    List<FieldGroup> getFieldGroups();

    /**
     * Sets the list of field groups for this template.
     * @param fieldGroups
     */
    void setFieldGroups(@Nullable List<FieldGroup> fieldGroups);
}
