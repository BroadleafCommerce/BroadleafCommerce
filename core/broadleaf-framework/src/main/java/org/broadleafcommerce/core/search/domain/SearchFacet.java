/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.core.search.domain;

import org.broadleafcommerce.common.copy.MultiTenantCloneable;

import java.io.Serializable;
import java.util.List;

/**
 * A SearchFacet is an object that represents a particular facet that can be used to guide faceted
 * searching on a results page.
 *
 * @author Andre Azzolini (apazzolini)
 */
public interface SearchFacet extends Serializable, MultiTenantCloneable<SearchFacet> {

    /**
     * Returns the internal id
     *
     * @return the internal id
     */
    Long getId();

    /**
     * Sets the internal id
     *
     * @param id
     */
    void setId(Long id);

    /**
     * The main relationship to the rest of the search index entities
     *
     * @see {@link #getField()}
     * @see {@link #getFacetFieldType()}
     */
    IndexFieldType getFieldType();

    void setFieldType(IndexFieldType fieldType);

    /**
     * <p>
     * Returns the field associated with this facet.
     *
     * <p>
     * This is a convenience method for <pre>{@code getFieldType().getIndexField().getField()}</pre>
     *
     * @return the fieldName
     */
    Field getField();

    /**
     * <p>
     * This String represents the FieldType for the given SearchFacet. This is the FieldType that will be used when this Field is indexed in Solr
     *
     * <p>
     * This is a convience method for <pre>{@code getFieldType().getFieldType().getType()}</pre>
     *
     * @return the String representing the FieldType of this SearchFacet
     */
    String getFacetFieldType();

    /**
     * Gets the name of this SearchFacet. This is for admin naming purposes.
     *
     * @return the name
     */
    String getName();

    /**
     * Sets the name
     *
     * @param name
     * @see #getName()
     */
    void setName(String name);

    /**
     * Gets the label of this SearchFacet. This is the label that will be used for the user-friendly
     * display name of this facet
     *
     * @return the label
     */
    String getLabel();

    /**
     * Sets the label
     *
     * @param label
     * @see #getLabel()
     */
    void setLabel(String label);

    /**
     * Gets a boolean that specifies whether or not this SearchFacet should be displayed on search
     * result pages in addition to category pages
     *
     * @return whether or not to display on search result pages
     */
    Boolean getShowOnSearch();

    /**
     * Sets showOnSearch
     *
     * @param showOnSearch
     * @see #getShowOnSearch()
     */
    void setShowOnSearch(Boolean showOnSearch);

    /**
     * Gets the display priority of this SearchFacet on search result pages
     *
     * @return the priority
     */
    Integer getSearchDisplayPriority();

    /**
     * Sets the display priority on search result pages
     *
     * @param searchDisplayPriority
     */
    void setSearchDisplayPriority(Integer searchDisplayPriority);

    /**
     * Gets whether or not you can multiselect values for this Facet
     *
     * @return the multiselect flag
     */
    Boolean getCanMultiselect();

    /**
     * Sets whether or not you can multiselect values for this Facet.
     *
     * @param canMultiselect
     */
    void setCanMultiselect(Boolean canMultiselect);

    /**
     * Gets whether or not this facet uses facet ranges
     *
     * @return the useFacetRanges flag
     */
    Boolean getUseFacetRanges();

    /**
     * Sets useFacetRanges
     *
     * @return
     * @see #getUseFacetRanges()
     */
    void setUseFacetRanges(Boolean useFacetRanges);

    /**
     * Gets the applicable ranges for this search facet, if any are specified. For example, the
     * SearchFacet that interacts with "Manufacturers" might not have any ranges defined (as it
     * would depend on the manufacturers that are in the result list), but a facet on "Price"
     * might have predefined ranges (such as 0-5, 5-10, 10-20).
     *
     * @return the associated search facet ranges, if any
     */
    List<SearchFacetRange> getSearchFacetRanges();

    /**
     * Sets the SearchFacetRanges
     *
     * <b>Note: This method will set ALL search facet ranges</b>
     *
     * @param searchFacetRanges
     * @see #getSearchFacetRanges()
     */
    void setSearchFacetRanges(List<SearchFacetRange> searchFacetRanges);

    /**
     * @return a list of SearchFacets that must have an active value set for this SearchFacet to be applicable.
     * @see #getRequiresAllDependentFacets()
     */
    List<RequiredFacet> getRequiredFacets();

    /**
     * Sets the list of facets which this facet depends on.
     *
     * @param requiredFacets
     */
    void setRequiredFacets(List<RequiredFacet> requiredFacets);

    /**
     * This boolean controls whether or not this particular facet requires one of the dependent facets to be active, or if
     * it requires all of the dependent facets to be active.
     *
     * @return whether the dependent facet list should be AND'ed together
     * @see #getRequiredFacets()
     */
    Boolean getRequiresAllDependentFacets();

    /**
     * Sets whether or not all dependent facets must be active, or if only one is necessary
     *
     * @param requiresAllDependentFacets
     */
    void setRequiresAllDependentFacets(Boolean requiresAllDependentFacets);

}
