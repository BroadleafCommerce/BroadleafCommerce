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

import org.broadleafcommerce.common.extension.AbstractExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.payment.PaymentGatewayRequestType;
import org.broadleafcommerce.common.payment.PaymentGatewayType;
import org.broadleafcommerce.common.payment.TransparentRedirectConstants;
import org.broadleafcommerce.common.payment.dto.PaymentRequestDTO;
import org.broadleafcommerce.common.payment.dto.PaymentResponseDTO;
import org.broadleafcommerce.common.payment.service.PaymentGatewayConfiguration;
import org.broadleafcommerce.common.payment.service.PaymentGatewayResolver;
import org.broadleafcommerce.common.payment.service.PaymentGatewayTransparentRedirectService;
import org.broadleafcommerce.common.vendor.service.exception.PaymentException;
import java.util.Map;
import javax.annotation.Resource;

/**
 * <p>An Abstract implementation of the TRCreditCardExtensionHandler.
 * PaymentGateway Handlers will just need to extend this class and implement
 * the declared abstract methods.</p>
 *
 * @author Elbert Bautista (elbertbautista)
 */
public abstract class AbstractTRCreditCardExtensionHandler extends AbstractExtensionHandler
        implements TRCreditCardExtensionHandler {

    @Resource(name = "blPaymentGatewayResolver")
    protected PaymentGatewayResolver paymentGatewayResolver;

    @Override
    public ExtensionResultStatusType setFormActionKey(StringBuilder key) {
        if (paymentGatewayResolver.isHandlerCompatible(getHandlerType())) {
            key.delete(0, key.length());
            key.append(getFormActionURLKey());
            return ExtensionResultStatusType.HANDLED;
        }

        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType setFormHiddenParamsKey(StringBuilder key) {
        if (paymentGatewayResolver.isHandlerCompatible(getHandlerType())) {
            key.delete(0, key.length());
            key.append(getHiddenParamsKey());
            return ExtensionResultStatusType.HANDLED;
        }

        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType createTransparentRedirectForm(
            Map<String, Map<String, String>> formParameters,
            PaymentRequestDTO requestDTO,
            Map<String, String> configurationSettings) throws PaymentException {

        if (paymentGatewayResolver.isHandlerCompatible(getHandlerType())) {
            if (formParameters != null && requestDTO != null &&  configurationSettings != null) {
                //Populate any additional configs on the RequestDTO
                for (String config:configurationSettings.keySet()){
                    requestDTO.additionalField(config, configurationSettings.get(config));
                }

                PaymentResponseDTO responseDTO;

                if (PaymentGatewayRequestType.CREATE_CUSTOMER_PAYMENT_TR.equals(requestDTO.getGatewayRequestType())) {
                    responseDTO = getTransparentRedirectService().createCustomerPaymentTokenForm(requestDTO);
                } else if (PaymentGatewayRequestType.UPDATE_CUSTOMER_PAYMENT_TR.equals(requestDTO.getGatewayRequestType())) {
                    responseDTO = getTransparentRedirectService().updateCustomerPaymentTokenForm(requestDTO);
                } else if (getConfiguration().isPerformAuthorizeAndCapture()) {
                    responseDTO = getTransparentRedirectService().createAuthorizeAndCaptureForm(requestDTO);
                } else {
                    responseDTO = getTransparentRedirectService().createAuthorizeForm(requestDTO);
                }

                overrideCustomerPaymentReturnURLs(requestDTO, responseDTO);
                populateFormParameters(formParameters, responseDTO);
            }

            return ExtensionResultStatusType.HANDLED_CONTINUE;
        }

        return ExtensionResultStatusType.NOT_HANDLED;
    }

    public PaymentGatewayType getHandlerType() {
        return getConfiguration().getGatewayType();
    }

    public abstract String getFormActionURLKey();

    public abstract String getHiddenParamsKey();

    public abstract PaymentGatewayConfiguration getConfiguration();

    public abstract PaymentGatewayTransparentRedirectService getTransparentRedirectService();

    public abstract void populateFormParameters(Map<String, Map<String, String>> formParameters,
                                                PaymentResponseDTO responseDTO);

    /**
     * If the request contains information about an override return URL, use the one specified on the request dto.
     * e.g. some modules like OMS may use the transparent redirect mechanism to create payment tokens,
     * if the request is originating from a module, then it may override the return url,
     * else the request would be coming from a normal flow, like adding a customer payment token from a customer's profile page.
     */
    protected void overrideCustomerPaymentReturnURLs(PaymentRequestDTO requestDTO, PaymentResponseDTO responseDTO) {
        if (requestDTO.getAdditionalFields().containsKey(TransparentRedirectConstants.OVERRIDE_CREATE_TOKEN_RETURN_URL)) {
            String createReturnKey = getTransparentRedirectService().getCreateCustomerPaymentTokenReturnURLFieldKey(responseDTO);
            String override = (String)requestDTO.getAdditionalFields().get(TransparentRedirectConstants.OVERRIDE_CREATE_TOKEN_RETURN_URL);
            responseDTO.getResponseMap().put(createReturnKey, override);
            responseDTO.getResponseMap().remove(TransparentRedirectConstants.OVERRIDE_CREATE_TOKEN_RETURN_URL);
        }

        if (requestDTO.getAdditionalFields().containsKey(TransparentRedirectConstants.OVERRIDE_CREATE_TOKEN_CANCEL_URL)) {
            String createCancelKey = getTransparentRedirectService().getCreateCustomerPaymentTokenCancelURLFieldKey(responseDTO);
            String override = (String)requestDTO.getAdditionalFields().get(TransparentRedirectConstants.OVERRIDE_CREATE_TOKEN_CANCEL_URL);
            responseDTO.getResponseMap().put(createCancelKey, override);
            responseDTO.getResponseMap().remove(TransparentRedirectConstants.OVERRIDE_CREATE_TOKEN_CANCEL_URL);
        }

        if (requestDTO.getAdditionalFields().containsKey(TransparentRedirectConstants.OVERRIDE_UPDATE_TOKEN_RETURN_URL)) {
            String updateReturnKey = getTransparentRedirectService().getUpdateCustomerPaymentTokenReturnURLFieldKey(responseDTO);
            String override = (String)requestDTO.getAdditionalFields().get(TransparentRedirectConstants.OVERRIDE_UPDATE_TOKEN_RETURN_URL);
            responseDTO.getResponseMap().put(updateReturnKey, override);
            responseDTO.getResponseMap().remove(TransparentRedirectConstants.OVERRIDE_UPDATE_TOKEN_RETURN_URL);
        }

        if (requestDTO.getAdditionalFields().containsKey(TransparentRedirectConstants.OVERRIDE_UPDATE_TOKEN_CANCEL_URL)) {
            String updateCancelKey = getTransparentRedirectService().getUpdateCustomerPaymentTokenCancelURLFieldKey(responseDTO);
            String override = (String)requestDTO.getAdditionalFields().get(TransparentRedirectConstants.OVERRIDE_UPDATE_TOKEN_CANCEL_URL);
            responseDTO.getResponseMap().put(updateCancelKey, override);
            responseDTO.getResponseMap().remove(TransparentRedirectConstants.OVERRIDE_UPDATE_TOKEN_CANCEL_URL);
        }

    }

}
