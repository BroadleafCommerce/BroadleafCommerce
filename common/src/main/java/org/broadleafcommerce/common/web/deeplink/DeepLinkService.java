/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.web.deeplink;


import org.broadleafcommerce.common.config.RuntimeEnvironmentPropertiesManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * This abstract class should be extended by services that provide deep links for specific entities.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public abstract class DeepLinkService<T> {
    
    protected static final String ADMIN_BASE_URL_PROP = "admin.baseurl";
    
    @Autowired
    protected RuntimeEnvironmentPropertiesManager propMgr;
    
    public String getAdminBaseUrl() {
        return propMgr.getProperty(ADMIN_BASE_URL_PROP);
    }

    public final List<DeepLink> getLinks(T item) {
        return getLinksInternal(item);
    }

    protected abstract List<DeepLink> getLinksInternal(T item);

}