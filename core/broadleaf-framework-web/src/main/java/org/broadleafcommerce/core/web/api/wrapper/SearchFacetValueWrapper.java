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

import org.broadleafcommerce.core.search.domain.SearchFacetResultDTO;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "searchFacetValue")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class SearchFacetValueWrapper extends BaseWrapper implements APIWrapper<SearchFacetResultDTO> {

    /*
     * Indicates if this facet value was active in a current search.
     */
    @XmlElement
    protected Boolean active = Boolean.FALSE;

    /*
     * A value. If the min and max values are populated, this should be null.
     */
    @XmlElement
    protected String value;

    /*
     * The value that should be passed in when using a search facet to filter a search. 
     * For example, a value key may be something like: "range[0.00000:5.00000]". This would 
     * be the value passed in as a query parameter (e.g. price=range[0.00000:5.00000]).  Or this could 
     * be a simple value if not min and max values are used.
     */
    @XmlElement
    protected String valueKey;

    /*
     * Indicates how many results are associated with this facet value.
     */
    @XmlElement
    protected Integer quantity;

    /*
     * Min value of a range. Should be null if value is not null.
     */
    @XmlElement
    protected BigDecimal minValue;

    /*
     * Max value of a range. Should be null if value is not null.
     */
    @XmlElement
    protected BigDecimal maxValue;

    @Override
    public void wrapDetails(SearchFacetResultDTO model, HttpServletRequest request) {
        this.active = model.isActive();
        this.valueKey = model.getValueKey();
        this.quantity = model.getQuantity();
        this.value = model.getValue();
        this.minValue = model.getMinValue();
        this.maxValue = model.getMaxValue();
    }

    @Override
    public void wrapSummary(SearchFacetResultDTO model, HttpServletRequest request) {
        wrapDetails(model, request);
    }
}
