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
package org.broadleafcommerce.common.web;

import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.locale.service.LocaleService;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Specific Spring component to override the default behavior of {@link CookieLocaleResolver} so that the default Broadleaf
 * Locale looked up in the database is used. This should be hooked up in applicationContext-servlet.xml in place of Spring's
 * {@link CookieResolver}.
 * 
 * @author Phillip Verheyden (phillipuniverse)
 * @see {@link BroadleafLocaleResolverImpl}
 */
public class BroadleafCookieLocaleResolver extends CookieLocaleResolver {

    @Resource(name = "blLocaleService")
    private LocaleService localeService;
    
    @Override
    protected java.util.Locale determineDefaultLocale(HttpServletRequest request) {
        java.util.Locale defaultLocale = getDefaultLocale();
        if (defaultLocale == null) {
            Locale defaultBroadleafLocale = localeService.findDefaultLocale();
            if (defaultBroadleafLocale == null) {
                return super.determineDefaultLocale(request);
            } else {
                return BroadleafRequestContext.convertLocaleToJavaLocale(defaultBroadleafLocale);
            }
        }
        return defaultLocale;
    }
    
}
