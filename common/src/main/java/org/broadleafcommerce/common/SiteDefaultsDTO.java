/*
 * #%L
 * broadleaf-multitenant-singleschema
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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

package org.broadleafcommerce.common;

import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.locale.domain.Locale;

/**
 * A DTO for transmitting some default values from a selected Site (because it is done through an extension handler and those don't return values, so 
 * these have to be passed by reference inside a method call)
 * @author gdiaz
 *
 */
public class SiteDefaultsDTO {

    protected BroadleafCurrency defaultCurrency;

    protected Locale defaultLocale;

    public BroadleafCurrency getDefaultCurrency() {
        return defaultCurrency;
    }

    public void setDefaultCurrency(BroadleafCurrency defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
    }

    public Locale getDefaultLocale() {
        return defaultLocale;
    }

    public void setDefaultLocale(Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

}
