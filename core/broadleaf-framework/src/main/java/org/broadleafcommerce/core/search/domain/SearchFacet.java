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
    public Long getId();

    /**
     * Sets the internal id
     * 
     * @param id
     */
    public void setId(Long id);

    /**
     * The main relationship to the rest of the search index entities
     * @see {@link #getField()}
     * @see {@link #getFacetFieldType()}
     */
    public IndexFieldType getFieldType();
    
    public void setFieldType(IndexFieldType fieldType);
    
    /**
     * <p>
     * Returns the field associated with this facet.
     * 
     * <p>
     * This is a convenience method for <pre>{@code getFieldType().getIndexField().getField()}</pre>
     * 
     * @return the fieldName
     */
    public Field getField();
    
    /**
     * <p>
     * This String represents the FieldType for the given SearchFacet. This is the FieldType that will be used when this Field is indexed in Solr
     *
     * <p>
     * This is a convience method for <pre>{@code getFieldType().getFieldType().getType()}</pre>
     *
     * @return the String representing the FieldType of this SearchFacet
     */
    public String getFacetFieldType();

    /**
     * Gets the name of this SearchFacet. This is for admin naming purposes.
     *
     * @return the name
     */
    public String getName();

    /**
     * Sets the name
     *
     * @see #getName()
     * @param name
     */
    public void setName(String name);

    /**
     * Gets the label of this SearchFacet. This is the label that will be used for the user-friendly
     * display name of this facet
     * 
     * @return the label
     */
    public String getLabel();

    /**
     * Sets the label
     * 
     * @see #getLabel()
     * @param label
     */
    public void setLabel(String label);

    /**
     * Gets a boolean that specifies whether or not this SearchFacet should be displayed on search
     * result pages in addition to category pages
     * 
     * @return whether or not to display on search result pages
     */
    public Boolean getShowOnSearch();

    /**
     * Sets showOnSearch
     * 
     * @see #getShowOnSearch()
     * @param showOnSearch
     */
    public void setShowOnSearch(Boolean showOnSearch);

    /**
     * Gets the display priority of this SearchFacet on search result pages
     * 
     * @return the priority
     */
    public Integer getSearchDisplayPriority();

    /**
     * Sets the display priority on search result pages
     * 
     * @param searchDisplayPriority
     */
    public void setSearchDisplayPriority(Integer searchDisplayPriority);
    
    /**
     * Sets whether or not you can multiselect values for this Facet.
     * 
     * @param canMultiselect
     */
    public void setCanMultiselect(Boolean canMultiselect);

    /**
     * Gets whether or not you can multiselect values for this Facet
     * 
     * @return the multiselect flag
     */
    public Boolean getCanMultiselect();

    /**
     * Gets whether or not this facet uses facet ranges
     *
     * @return the useFacetRanges flag
     */
    public Boolean getUseFacetRanges();

    /**
     * Sets useFacetRanges
     *
     * @see #getUseFacetRanges()
     * @return
     */
    public void setUseFacetRanges(Boolean useFacetRanges);

    /**
     * Gets the applicable ranges for this search facet, if any are specified. For example, the 
     * SearchFacet that interacts with "Manufacturers" might not have any ranges defined (as it
     * would depend on the manufacturers that are in the result list), but a facet on "Price"
     * might have predefined ranges (such as 0-5, 5-10, 10-20).
     * 
     * @return the associated search facet ranges, if any
     */
    public List<SearchFacetRange> getSearchFacetRanges();
    
    /**
     * Sets the SearchFacetRanges
     * 
     * <b>Note: This method will set ALL search facet ranges</b>
     * 
     * @see #getSearchFacetRanges()
     * @param searchFacetRanges
     */
    public void setSearchFacetRanges(List<SearchFacetRange> searchFacetRanges);

    /**
     * @see #getRequiresAllDependentFacets()
     * 
     * @return a list of SearchFacets that must have an active value set for this SearchFacet to be applicable.
     */
    public List<RequiredFacet> getRequiredFacets();

    /**
     * Sets the list of facets which this facet depends on.
     * 
     * @param dependentFacets
     */
    public void setRequiredFacets(List<RequiredFacet> requiredFacets);

    /**
     * This boolean controls whether or not this particular facet requires one of the dependent facets to be active, or if
     * it requires all of the dependent facets to be active.
     * 
     * @see #getRequiredFacets()
     * 
     * @return whether the dependent facet list should be AND'ed together
     */
    public Boolean getRequiresAllDependentFacets();
    
    /**
     * Sets whether or not all dependent facets must be active, or if only one is necessary
     * 
     * @param requiresAllDependentFacets
     */
    public void setRequiresAllDependentFacets(Boolean requiresAllDependentFacets);

}
