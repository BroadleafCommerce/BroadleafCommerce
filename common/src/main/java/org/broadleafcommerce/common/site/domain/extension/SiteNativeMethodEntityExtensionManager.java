/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
package org.broadleafcommerce.common.site.domain.extension;

import org.broadleafcommerce.common.extension.ExtensionManager;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Extension manager holder for all of the {@link SiteNativeMethodEntityExtensionHandler}. Extension managers should be
 * added by injecting this bean and adding themselves in a {@link PostConstruct}.
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
@Service("blSiteNativeMethodEntityExtensionManager")
public class SiteNativeMethodEntityExtensionManager extends ExtensionManager<SiteNativeMethodEntityExtensionHandler> {

    public SiteNativeMethodEntityExtensionManager() {
        super(SiteNativeMethodEntityExtensionHandler.class);
    }

}
