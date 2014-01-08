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

package org.broadleafcommerce.common.web.payment.processor;

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;

import java.util.Map;

/**
 * @author Elbert Bautista (elbertbautista)
 */
public interface CreditCardTypesExtensionHandler extends ExtensionHandler {

    /**
     * The registered Extension Handler will populate any specific Payment Gateway
     * codes required for Credit Card Types.
     *
     * key = "Card Type Code to send to the Gateway"
     * value = "Friendly Name of Card type (e.g. Visa, MasterCard, etc...)"
     *
     * @param creditCardTypes
     * @return
     */
    public ExtensionResultStatusType populateCreditCardMap(Map<String, String> creditCardTypes);

}
