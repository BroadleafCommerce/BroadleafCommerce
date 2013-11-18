/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.search.dao;

import org.broadleafcommerce.core.search.domain.SearchFacet;

import java.util.List;

/**
 * DAO used to interact with the database search facets
 * 
 * @author Andre Azzolini (apazzolini)
 */
public interface SearchFacetDao {

    /**
     * Returns the distinct values for the given fieldName inside of the search clas sas a list of the specified 
     * type. For example, reading the distinct values for "manufacturer" in the ProductImpl class and specifying
     * the value class as String would search the ProductImpl entity's distinct manufacturers and return a 
     * List<String> of these values.
     * 
     * @param fieldName
     * @param fieldValueClass
     * @return  the distinct values for the field
     */
    public <T> List<T> readDistinctValuesForField(String fieldName, Class<T> fieldValueClass);

    /**
     * Returns all SearchFacets that are tagged with showOnSearch
     * 
     * @return the facets to display on searches
     */
    public List<SearchFacet> readAllSearchFacets();

    /**
     * Persist to the data layer.
     *
     * @param searchFacet the instance to persist
     * @return the instance after it has been persisted
     */
    public SearchFacet save(SearchFacet searchFacet);
}
