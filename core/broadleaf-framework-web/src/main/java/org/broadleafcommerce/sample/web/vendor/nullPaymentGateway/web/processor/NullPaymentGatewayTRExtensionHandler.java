/*
 * #%L
 * BroadleafCommerce Framework Web
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

package org.broadleafcommerce.sample.web.vendor.nullPaymentGateway.web.processor;

import org.broadleafcommerce.common.payment.dto.PaymentResponseDTO;
import org.broadleafcommerce.common.payment.service.PaymentGatewayConfigurationService;
import org.broadleafcommerce.common.payment.service.PaymentGatewayTransparentRedirectService;
import org.broadleafcommerce.common.web.payment.processor.AbstractTRCreditCardExtensionHandler;
import org.broadleafcommerce.common.web.payment.processor.TRCreditCardExtensionManager;
import org.broadleafcommerce.sample.web.vendor.nullPaymentGateway.service.payment.NullPaymentGatewayConstants;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * This sample handler will add itself to the {@link TRCreditCardExtensionManager}
 * and will add some default hidden parameters/form POST URL for our fake
 * {@link org.broadleafcommerce.sample.web.vendor.nullPaymentGateway.web.controller.NullPaymentGatewayProcessorController}
 *
 * Note, we don't want this loaded into the extension manager
 * if a real payment gateway is used, so make sure to not scan this class when
 * using a real implementation. This is for demo purposes only.
 *
 * In order to use this sample extension handler, you will need to component scan
 * the package "org.broadleafcommerce.sample.web".
 *
 * This should NOT be used in production, and is meant solely for demonstration
 * purposes only.
 *
 * @author Elbert Bautista (elbertbautista)
 */
@Service("blNullPaymentGatewayTRExtensionHandler")
public class NullPaymentGatewayTRExtensionHandler extends AbstractTRCreditCardExtensionHandler {

    public static final String FORM_ACTION_URL = NullPaymentGatewayConstants.TRANSPARENT_REDIRECT_URL;
    public static final String FORM_HIDDEN_PARAMS = "FORM_HIDDEN_PARAMS";

    @Resource(name = "blTRCreditCardExtensionManager")
    protected TRCreditCardExtensionManager extensionManager;

    @Resource(name = "blNullPaymentGatewayTransparentRedirectService")
    protected PaymentGatewayTransparentRedirectService transparentRedirectService;

    @Resource(name = "blNullPaymentGatewayConfigurationService")
    protected PaymentGatewayConfigurationService configurationService;

    @PostConstruct
    public void init() {
        if (isEnabled()) {
            extensionManager.registerHandler(this);
        }
    }

    @Override
    public String getFormActionURLKey() {
        return FORM_ACTION_URL;
    }

    @Override
    public String getHiddenParamsKey() {
        return FORM_HIDDEN_PARAMS;
    }

    @Override
    public PaymentGatewayConfigurationService getConfigurationService() {
        return configurationService;
    }

    @Override
    public PaymentGatewayTransparentRedirectService getTransparentRedirectService() {
        return transparentRedirectService;
    }

    @Override
    public void populateFormParameters(Map<String, Map<String, String>> formParameters, PaymentResponseDTO responseDTO) {
        String actionUrl = (String) responseDTO.getResponseMap().get(NullPaymentGatewayConstants.TRANSPARENT_REDIRECT_URL);
        Map<String, String> actionValue = new HashMap<String, String>();
        actionValue.put(getFormActionURLKey(), actionUrl);
        formParameters.put(getFormActionURLKey(), actionValue);

        Map<String, String> hiddenFields = new HashMap<String, String>();
        hiddenFields.put(NullPaymentGatewayConstants.TRANSACTION_AMT,
                responseDTO.getResponseMap().get(NullPaymentGatewayConstants.TRANSACTION_AMT).toString());
        hiddenFields.put(NullPaymentGatewayConstants.ORDER_ID,
                responseDTO.getResponseMap().get(NullPaymentGatewayConstants.ORDER_ID).toString());

        if (responseDTO.getResponseMap().get(NullPaymentGatewayConstants.BILLING_FIRST_NAME) != null) {
            hiddenFields.put(NullPaymentGatewayConstants.BILLING_FIRST_NAME,
                responseDTO.getResponseMap().get(NullPaymentGatewayConstants.BILLING_FIRST_NAME).toString());
        }

        if (responseDTO.getResponseMap().get(NullPaymentGatewayConstants.BILLING_LAST_NAME) != null) {
            hiddenFields.put(NullPaymentGatewayConstants.BILLING_LAST_NAME,
                responseDTO.getResponseMap().get(NullPaymentGatewayConstants.BILLING_LAST_NAME).toString());
        }

        if (responseDTO.getResponseMap().get(NullPaymentGatewayConstants.BILLING_ADDRESS_LINE1) != null) {
            hiddenFields.put(NullPaymentGatewayConstants.BILLING_ADDRESS_LINE1,
                responseDTO.getResponseMap().get(NullPaymentGatewayConstants.BILLING_ADDRESS_LINE1).toString());
        }

        if (responseDTO.getResponseMap().get(NullPaymentGatewayConstants.BILLING_ADDRESS_LINE2) != null) {
            hiddenFields.put(NullPaymentGatewayConstants.BILLING_ADDRESS_LINE2,
                responseDTO.getResponseMap().get(NullPaymentGatewayConstants.BILLING_ADDRESS_LINE2).toString());
        }

        if (responseDTO.getResponseMap().get(NullPaymentGatewayConstants.BILLING_CITY) != null) {
            hiddenFields.put(NullPaymentGatewayConstants.BILLING_CITY,
                responseDTO.getResponseMap().get(NullPaymentGatewayConstants.BILLING_CITY).toString());
        }

        if (responseDTO.getResponseMap().get(NullPaymentGatewayConstants.BILLING_STATE) != null) {
            hiddenFields.put(NullPaymentGatewayConstants.BILLING_STATE,
                responseDTO.getResponseMap().get(NullPaymentGatewayConstants.BILLING_STATE).toString());
        }

        if (responseDTO.getResponseMap().get(NullPaymentGatewayConstants.BILLING_ZIP) != null) {
            hiddenFields.put(NullPaymentGatewayConstants.BILLING_ZIP,
                responseDTO.getResponseMap().get(NullPaymentGatewayConstants.BILLING_ZIP).toString());
        }

        if (responseDTO.getResponseMap().get(NullPaymentGatewayConstants.SHIPPING_FIRST_NAME) != null) {
            hiddenFields.put(NullPaymentGatewayConstants.SHIPPING_FIRST_NAME,
                responseDTO.getResponseMap().get(NullPaymentGatewayConstants.SHIPPING_FIRST_NAME).toString());
        }

        if (responseDTO.getResponseMap().get(NullPaymentGatewayConstants.SHIPPING_LAST_NAME) != null) {
            hiddenFields.put(NullPaymentGatewayConstants.SHIPPING_LAST_NAME,
                responseDTO.getResponseMap().get(NullPaymentGatewayConstants.SHIPPING_LAST_NAME).toString());
        }

        if (responseDTO.getResponseMap().get(NullPaymentGatewayConstants.SHIPPING_ADDRESS_LINE1) != null) {
            hiddenFields.put(NullPaymentGatewayConstants.SHIPPING_ADDRESS_LINE1,
                responseDTO.getResponseMap().get(NullPaymentGatewayConstants.SHIPPING_ADDRESS_LINE1).toString());
        }

        if (responseDTO.getResponseMap().get(NullPaymentGatewayConstants.SHIPPING_ADDRESS_LINE2) != null) {
            hiddenFields.put(NullPaymentGatewayConstants.SHIPPING_ADDRESS_LINE2,
                responseDTO.getResponseMap().get(NullPaymentGatewayConstants.SHIPPING_ADDRESS_LINE2).toString());
        }

        if (responseDTO.getResponseMap().get(NullPaymentGatewayConstants.SHIPPING_CITY) != null) {
            hiddenFields.put(NullPaymentGatewayConstants.SHIPPING_CITY,
                responseDTO.getResponseMap().get(NullPaymentGatewayConstants.SHIPPING_CITY).toString());
        }

        if (responseDTO.getResponseMap().get(NullPaymentGatewayConstants.SHIPPING_STATE) != null) {
            hiddenFields.put(NullPaymentGatewayConstants.SHIPPING_STATE,
                responseDTO.getResponseMap().get(NullPaymentGatewayConstants.SHIPPING_STATE).toString());
        }

        if (responseDTO.getResponseMap().get(NullPaymentGatewayConstants.SHIPPING_ZIP) != null) {
            hiddenFields.put(NullPaymentGatewayConstants.SHIPPING_ZIP,
                responseDTO.getResponseMap().get(NullPaymentGatewayConstants.SHIPPING_ZIP).toString());
        }


        formParameters.put(getHiddenParamsKey(), hiddenFields);
    }
}
