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
package org.broadleafcommerce.common.money;

import org.broadleafcommerce.common.classloader.release.ThreadLocalManager;

import java.util.HashMap;

/**
 * 
 * @author jfischer
 *
 */
public class CurrencyConsiderationContext {
    
    private static final ThreadLocal<CurrencyDeterminationService> currencyDeterminationService = ThreadLocalManager.createThreadLocal(CurrencyDeterminationService.class);

    private static final ThreadLocal<HashMap> currencyConsiderationContext = ThreadLocalManager.createThreadLocal(HashMap.class);

    public static HashMap getCurrencyConsiderationContext() {
        return CurrencyConsiderationContext.currencyConsiderationContext.get();
    }
    
    public static void setCurrencyConsiderationContext(HashMap currencyConsiderationContext) {
        CurrencyConsiderationContext.currencyConsiderationContext.set(currencyConsiderationContext);
    }
    
    public static CurrencyDeterminationService getCurrencyDeterminationService() {
        return CurrencyConsiderationContext.currencyDeterminationService.get();
    }
    
    public static void setCurrencyDeterminationService(CurrencyDeterminationService currencyDeterminationService) {
        CurrencyConsiderationContext.currencyDeterminationService.set(currencyDeterminationService);
    }
}
