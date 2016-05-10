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

/**
 * <p>Certain Payment Integrations allow you to use Fraud Services like Address Verification and Buyer Authentication,
 * such as PayPal Payments Pro (PayFlow Edition)</p>
 *
 * <p>This API allows you to call certain fraud prevention APIs exposed from the gateway.</p>
 *
 * @author Elbert Bautista (elbertbautista)
 */
public interface PaymentGatewayFraudService {

    /**
     * Certain Gateways integrate with Visa's Verified by Visa and MasterCard's SecureCode API
     * If the buyer is enrolled in such a service, we will need to redirect the buyer's browser
     * to the ACS ( Access Control Server, eg. users' bank) for verification.
     * See: http://en.wikipedia.org/wiki/3-D_Secure
     *
     * This method is intended to retrieve a URL to the ACS from the gateway.
     *
     * @param paymentRequestDTO
     * @return
     */
    public PaymentResponseDTO requestPayerAuthentication(PaymentRequestDTO paymentRequestDTO);

}
