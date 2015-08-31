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
import org.broadleafcommerce.core.search.domain.solr.FieldType;

import java.io.Serializable;
import java.util.List;

/**
 * @author Chad Harchar (charchar)
 */
public interface SearchField extends Serializable, MultiTenantCloneable<SearchField> {

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
     * Gets the abbreviation for this search field
     *
     * @return
     */
    public String getAbbreviation();

    /**
     * Sets the abbreviation for this search field
     *
     * @param abbreviation
     */
    public void setAbbreviation(String abbreviation);

    /**
     * Gets the searchable field types for this search field
     *
     * @return
     */
    public List<FieldType> getSearchableFieldTypes();

    /**
     * Sets the searchable field types for this search field
     *
     * @param searchableFieldTypes
     */
    public void setSearchableFieldTypes(List<FieldType> searchableFieldTypes);
}
