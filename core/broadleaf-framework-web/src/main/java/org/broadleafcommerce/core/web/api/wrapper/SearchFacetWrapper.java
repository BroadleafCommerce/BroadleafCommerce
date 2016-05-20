/*
 * #%L
 * BroadleafCommerce Framework Web
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
package org.broadleafcommerce.core.web.api.wrapper;

import org.broadleafcommerce.core.search.domain.SearchFacetDTO;
import org.broadleafcommerce.core.search.domain.SearchFacetResultDTO;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This wrapper provides information about the search facets available 
 * for use with a search.
 * 
 * Search facets are typically returned from a catalog search as part of the result. 
 * You can use facets to narrow a search.
 * 
 * @deprecated - use {@link com.broadleafcommerce.core.rest.api.v2.wrapper.SearchFacetWrapper}
 * 
 * @author Kelly Tisdell
 *
 */
@Deprecated
@XmlRootElement(name = "searchFacet")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class SearchFacetWrapper extends BaseWrapper implements APIWrapper<SearchFacetDTO> {

    /*
     * Name of the field. (e.g. you might have a field name 
     * like "price" or "description")
     */
    @XmlElement
    protected String fieldName;

    /*
     * Indicates if this facet is active in the current search.
     */
    @XmlElement
    protected Boolean active = Boolean.FALSE;

    /*
     * List of values that are appropriate for this facet.
     */
    @XmlElement
    protected List<SearchFacetValueWrapper> values;

    @Override
    public void wrapDetails(SearchFacetDTO model, HttpServletRequest request) {
        this.fieldName = model.getFacet().getField().getAbbreviation();
        this.active = model.isActive();

        if (model.getFacetValues() != null) {
            this.values = new ArrayList<SearchFacetValueWrapper>();
            for (SearchFacetResultDTO result : model.getFacetValues()) {
                SearchFacetValueWrapper wrapper = (SearchFacetValueWrapper) context.getBean(SearchFacetValueWrapper.class.getName());
                wrapper.wrapSummary(result, request);
                this.values.add(wrapper);
            }
        }
    }

    @Override
    public void wrapSummary(SearchFacetDTO model, HttpServletRequest request) {
        wrapDetails(model, request);
    }

    
    /**
     * @return the fieldName
     */
    public String getFieldName() {
        return fieldName;
    }

    
    /**
     * @param fieldName the fieldName to set
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    
    /**
     * @return the active
     */
    public Boolean getActive() {
        return active;
    }

    
    /**
     * @param active the active to set
     */
    public void setActive(Boolean active) {
        this.active = active;
    }

    
    /**
     * @return the values
     */
    public List<SearchFacetValueWrapper> getValues() {
        return values;
    }

    
    /**
     * @param values the values to set
     */
    public void setValues(List<SearchFacetValueWrapper> values) {
        this.values = values;
    }
}
