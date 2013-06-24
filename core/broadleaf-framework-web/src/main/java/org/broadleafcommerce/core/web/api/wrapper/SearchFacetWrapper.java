/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
 * @author Kelly Tisdell
 *
 */
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
}
