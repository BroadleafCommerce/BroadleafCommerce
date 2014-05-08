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

import org.broadleafcommerce.common.extension.AbstractExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.site.domain.Site;

/**
 * Default implementation of the {@link SiteNativeMethodEntityExtensionHandler}. Implementors should subclass this class
 * rather than depend on the interface directly
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
public abstract class AbstractSiteNativeMethodEntityExtensionHandler extends AbstractExtensionHandler implements SiteNativeMethodEntityExtensionHandler {

    @Override
    public void contributeClone(Site original, Site preCloned) {
        // unimplemented
    }

    @Override
    public void contributeEquals(Site original, Site test, ExtensionResultHolder<Boolean> result) {
        result.setResult(true);
    }

    @Override
    public void contributeHashCode(Site entity, int precomputedHashCode, ExtensionResultHolder<Integer> result) {
        result.setResult(precomputedHashCode);
    }

}
