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

package org.broadleafcommerce.core.search.service.solr;

import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.search.domain.Field;
import org.broadleafcommerce.core.search.domain.SearchFacetDTO;
import org.broadleafcommerce.core.search.domain.SearchFacetRange;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author Andre Azzolini (apazzolini)
 */
public class SolrSearchServiceExtensionManager implements SolrSearchServiceExtensionListener {
    
    protected List<SolrSearchServiceExtensionListener> listeners = new ArrayList<SolrSearchServiceExtensionListener>();

    @Override
    public String getPrefix() {
        StringBuilder sb = new StringBuilder();
        for (SolrSearchServiceExtensionListener listener : listeners) {
            sb.append(listener.getPrefix());
        }
        return sb.toString();
    }
    
    @Override
    public String getPrefixForPriceField() {
        StringBuilder sb = new StringBuilder();
        for (SolrSearchServiceExtensionListener listener : listeners) {
            sb.append(listener.getPrefixForPriceField());
        }
        return sb.toString();
    }

    @Override
    public void addPriceFieldPropertyValues(Product product, Field field, Map<String, Object> values, String propertyName) 
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        for (SolrSearchServiceExtensionListener listener : listeners) {
            listener.addPriceFieldPropertyValues(product, field, values, propertyName);
        }
    }
    
    @Override
    public void filterSearchFacetRanges(SearchFacetDTO dto, List<SearchFacetRange> ranges) {
        for (SolrSearchServiceExtensionListener listener : listeners) {
            listener.filterSearchFacetRanges(dto, ranges);
        }
    }
    
    public List<SolrSearchServiceExtensionListener> getListeners() {
        return listeners;
    }

    public void setListeners(List<SolrSearchServiceExtensionListener> listeners) {
        this.listeners = listeners;
    }

}