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
import org.broadleafcommerce.common.payment.dto.PaymentRequestDTO;
import org.broadleafcommerce.common.vendor.service.exception.PaymentException;

import java.util.Map;

/**
 * @author Elbert Bautista (elbertbautista)
 */
public interface TRCreditCardExtensionHandler extends ExtensionHandler {

    /**
     * <p>The implementing modules should take into consideration the passed in configuration settings map
     * and call their implementing TransparentRedirectService to generate either an Authorize
     * or Authorize and Capture Form. The decision should be based on the implementing
     * PaymentGatewayConfigurationService.isPerformAuthorizeAndCapture();
     * </p>
     * <p>
     * This method accepts a RequestDTO that represents the order along with a map of
     * gateway-specific configuration settings.
     * The hidden values and the form action will be placed on the passed in formParameters
     * variable. The keys to that map can be retrieved by calling the following methods.
     * getFormActionKey, getFormHiddenParamsKey.
     * </p>
     *
     * @param formParameters
     * @param requestDTO
     * @param configurationSettings
     */
    public ExtensionResultStatusType createTransparentRedirectForm(
            Map<String, Map<String,String>> formParameters,
            PaymentRequestDTO requestDTO,
            Map<String, String> configurationSettings) throws PaymentException;

    public ExtensionResultStatusType setFormActionKey(StringBuilder key);

    public ExtensionResultStatusType setFormHiddenParamsKey(StringBuilder key);

}
