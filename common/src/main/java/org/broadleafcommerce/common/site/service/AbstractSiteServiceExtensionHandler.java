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
package org.broadleafcommerce.common.site.service;

import org.broadleafcommerce.common.extension.AbstractExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.site.domain.Site;

/**
 * Default implementation of {@link SiteServiceExtensionHandler}
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
public class AbstractSiteServiceExtensionHandler extends AbstractExtensionHandler implements SiteServiceExtensionHandler {

    @Override
    public ExtensionResultStatusType contributeNonPersitentSiteProperties(Site from, Site to) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

}
