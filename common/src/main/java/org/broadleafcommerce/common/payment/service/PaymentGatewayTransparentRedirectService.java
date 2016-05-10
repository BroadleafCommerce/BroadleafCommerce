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

package org.broadleafcommerce.common.payment.service;

import org.broadleafcommerce.common.payment.dto.PaymentRequestDTO;
import org.broadleafcommerce.common.payment.dto.PaymentResponseDTO;
import org.broadleafcommerce.common.vendor.service.exception.PaymentException;

/**
 * <p>The purpose of this class, is to provide an API that will create
 * any gateway specific parameters needed for a Transparent Redirect/Silent Order Post etc...</p>
 *
 * <p>Some payment gateways provide this ability and will generate either a Secure Token
 * or some hashed parameters that will be placed as hidden fields on your Credit Card form.
 * These parameters (along with the Credit Card information) will be placed on the ResponseDTO
 * and your HTML should include these fields to be POSTed directly to the
 * implementing gateway for processing.</p>
 *
 * <p>In addition, some gateways also support the creation of a payment token (i.e. a tokenized version of a
 * Credit Card that can be used on subsequent requests) outside the scope of an authorize or sale transaction.</p>
 *
 * @author Elbert Bautista (elbertbautista)
 */
public interface PaymentGatewayTransparentRedirectService {

    public PaymentResponseDTO createAuthorizeForm(PaymentRequestDTO requestDTO) throws PaymentException;

    public PaymentResponseDTO createAuthorizeAndCaptureForm(PaymentRequestDTO requestDTO) throws PaymentException;

    public PaymentResponseDTO createCustomerPaymentTokenForm(PaymentRequestDTO requestDTO) throws PaymentException;

    public PaymentResponseDTO updateCustomerPaymentTokenForm(PaymentRequestDTO requestDTO) throws PaymentException;

    /**
     * Return the {@link org.broadleafcommerce.common.payment.dto.PaymentResponseDTO#responseMap} key
     * that corresponds to creating the customer token return url
     */
    public String getCreateCustomerPaymentTokenReturnURLFieldKey(PaymentResponseDTO responseDTO);

    /**
     * Return the {@link org.broadleafcommerce.common.payment.dto.PaymentResponseDTO#responseMap} key
     * that corresponds to creating the customer token cancel url
     */
    public String getCreateCustomerPaymentTokenCancelURLFieldKey(PaymentResponseDTO responseDTO);

    /**
     * Return the {@link org.broadleafcommerce.common.payment.dto.PaymentResponseDTO#responseMap} key
     * that corresponds to updating the customer token return url
     */
    public String getUpdateCustomerPaymentTokenReturnURLFieldKey(PaymentResponseDTO responseDTO);

    /**
     * Return the {@link org.broadleafcommerce.common.payment.dto.PaymentResponseDTO#responseMap} key
     * that corresponds to updating the customer token cancel url
     */
    public String getUpdateCustomerPaymentTokenCancelURLFieldKey(PaymentResponseDTO responseDTO);

}
