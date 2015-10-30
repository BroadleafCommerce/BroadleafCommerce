/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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
package org.broadleafcommerce.core.search.domain;

import org.broadleafcommerce.common.copy.MultiTenantCloneable;

import java.io.Serializable;
import java.util.List;

/**
 * Represents a field that gets stored in the search index
 * 
 * @author Chad Harchar (charchar)
 */
public interface IndexField extends Serializable, MultiTenantCloneable<IndexField> {

    /**
     * Gets the id for this search field
     *
     * @return
     */
    public Long getId();

    /**
     * Sets the id for this search field
     *
     * @param id
     */
    public void setId(Long id);
    
    /**
     * Whether or not the user should see results for this field when typing in search terms in the omnibox, or if
     * this is just a field stored in the index (like margin or sorts)
     */
    public Boolean getSearchable();

    public void setSearchable(Boolean searchable);
    
    /**
     * Gets the field for this search field
     *
     * @return
     */
    public Field getField();

    /**
     * Sets the field for this search field
     *
     * @param field
     */
    public void setField(Field field);

    /**
     * Gets the searchable field types for this search field
     *
     * @return
     */
    public List<IndexFieldType> getFieldTypes();

    /**
     * Sets the searchable field types for this search field
     *
     * @param fieldTypes
     */
    public void setFieldTypes(List<IndexFieldType> fieldTypes);
}
