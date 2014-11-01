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
package org.broadleafcommerce.common.locale.service;

import org.broadleafcommerce.common.locale.domain.Locale;

import java.util.List;

/**
 * Created by bpolster.
 */
public interface LocaleService {

    /**
     * @return the locale for the passed in code
     */
    public Locale findLocaleByCode(String localeCode);

    /**
     * @return the default locale
     */
    public Locale findDefaultLocale();

    /**
     * @return a list of all known locales
     */
    public List<Locale> findAllLocales();
    
    /**
     * Persists the given locale
     * 
     * @param locale
     * @return the persisted locale
     */
    public Locale save(Locale locale);
    
}
