/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.search.dao;

import org.broadleafcommerce.core.search.domain.Field;
import org.broadleafcommerce.core.search.domain.FieldEntity;
import org.broadleafcommerce.core.search.domain.SearchFacet;
import org.broadleafcommerce.core.search.domain.SearchFacetRange;

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
     * Returns all SearchFacets that are tagged with showOnSearch for the given entity type
     * 
     * @return the facets to display on searches
     */
    public List<SearchFacet> readAllSearchFacets(FieldEntity entityType);

    /**
     * Persist to the data layer.
     *
     * @param searchFacet the instance to persist
     * @return the instance after it has been persisted
     */
    public SearchFacet save(SearchFacet searchFacet);

    /**
     * Returns a SearchFacet for the given field, if one exists
     *
     * @param field the field to find a SearchFacet for
     * @return the SearchFacet for the given field
     */
    public SearchFacet readSearchFacetForField(Field field);

    /**
     * Returns a list of SearchFacetRanges for a given SearchFacet
     *
     * @param searchFacet the SearchFacet to use
     * @return the SearchFacetRanges for the given SearchFacet
     */
    public List<SearchFacetRange> readSearchFacetRangesForSearchFacet(SearchFacet searchFacet);
}
