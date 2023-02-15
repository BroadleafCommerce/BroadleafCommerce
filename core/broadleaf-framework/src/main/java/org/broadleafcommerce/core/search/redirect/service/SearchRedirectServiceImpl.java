/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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
