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
package org.broadleafcommerce.core.search.redirect.service;

import org.broadleafcommerce.core.search.redirect.dao.SearchRedirectDao;
import org.broadleafcommerce.core.search.redirect.domain.SearchRedirect;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by ppatel.
 */
@Service("blSearchRedirectService")
public class SearchRedirectServiceImpl implements SearchRedirectService {

  
    @Resource(name = "blSearchRedirectDao")
    protected SearchRedirectDao SearchRedirectDao;


    /**
     * Checks the passed in URL to determine if there is a matching
     * SearchRedirect. Returns null if no handler was found.
     * 
     * @param uri
     * @return
     */
    @Override
    public SearchRedirect findSearchRedirectBySearchTerm(String uri) {

        SearchRedirect SearchRedirect = SearchRedirectDao
                .findSearchRedirectBySearchTerm(uri);
        if (SearchRedirect != null) {
            return SearchRedirect;
        } else {
            return null;
        }

    }

}
