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
package org.broadleafcommerce.common.extension.currency;

import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.extension.AbstractExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.web.BroadleafCurrencyResolver;


/**
 * Implementors of this interface can override the currency that will be resolved by the
 * {@link BroadleafCurrencyResolver}.   A default, community implementation of Broadleaf will
 * support a single currency.    If additional currencies are added (either by inclusion of enterprise
 * pricing modules) or with custom development, then an implementation of this handler can be 
 * provided that allows the request to be associated with an alternate currency. 
 * 
 * @author bpolster
 */
public class AbstractCurrencyResolverExtensionHandler extends AbstractExtensionHandler
        implements CurrencyResolverExtensionHandler {
    
    /**
     * Implementation sets the {@link ExtensionResultHolder} value to the currency that should
     * be used.    Setting to null will force the currency resolver to utilize the defaultCurrency. 
     * 
     * Note that implementations run in {@link #getPriority()} order and the last one wins.   Also note
     * that prior implementations may have set the value on the {@link ExtensionResultHolder}.   Later
     * implementations are free to alter but should do so with clear intent to override.
     * 
     * @param desiredCurrency
     * @param defaultCurrency
     * @return
     */
    public ExtensionResultStatusType overrideCurrency(BroadleafCurrency desiredCurrency,
            BroadleafCurrency defaultCurrency, ExtensionResultHolder<BroadleafCurrency> currencyToUse) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }
}
