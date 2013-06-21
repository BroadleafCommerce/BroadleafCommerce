/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.search.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andre Azzolini (apazzolini)
 */
public class SearchFacetDTO {
    
    protected SearchFacet facet;
    protected boolean showQuantity;
    protected List<SearchFacetResultDTO> facetValues = new ArrayList<SearchFacetResultDTO>();
    protected boolean active;
    
    public SearchFacet getFacet() {
        return facet;
    }
    
    public void setFacet(SearchFacet facet) {
        this.facet = facet;
    }
    
    public boolean isShowQuantity() {
        return showQuantity;
    }
    
    public void setShowQuantity(boolean showQuantity) {
        this.showQuantity = showQuantity;
    }
    
    public List<SearchFacetResultDTO> getFacetValues() {
        return facetValues;
    }
    
    public void setFacetValues(List<SearchFacetResultDTO> facetValues) {
        this.facetValues = facetValues;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    
}
