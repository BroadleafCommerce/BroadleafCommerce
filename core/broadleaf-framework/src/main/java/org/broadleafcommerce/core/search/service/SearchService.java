/*
 * Copyright 2008-2009 the original author or authors.
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

package org.broadleafcommerce.core.search.service;

import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.search.domain.SearchIntercept;
import org.broadleafcommerce.core.search.domain.SearchSynonym;

import java.util.List;

@Deprecated
public interface SearchService {

    public void rebuildProductIndex();

    public List<Product> performSearch(String input);

    public List<SearchIntercept> getAllSearchIntercepts();

    public SearchIntercept getInterceptForTerm(String term);
    public void createSearchIntercept(SearchIntercept intercept);
    public void updateSearchIntercept(SearchIntercept intercept);
    public void deleteSearchIntercept(SearchIntercept intercept);
    
    public List<SearchSynonym> getAllSearchSynonyms();
    public void createSearchSynonym(SearchSynonym synonym);
    public void updateSearchSynonym(SearchSynonym synonym);
    public void deleteSearchSynonym(SearchSynonym synonym);
}