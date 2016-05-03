/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
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
     * PaymentGatewayConfiguration.isPerformAuthorizeAndCapture();
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
