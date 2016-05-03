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

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * <p>Several Gateways have a method of communication that uses the HTTP Request/Response model
 * and the client's browser to transmit transaction result information back to the system. In some cases,
 * these gateways don't return a tokenized response, but plain-text parameters.
 * For example: In cases where the application needs to complete an order asynchronously (via a Webhook or SilentPost)
 * we need to send information about the customer id and the order id to the gateway in the original request so that
 * a call back to the server from the gateway will return those values and the system will know how to
 * associate the completed transaction with an order.</p>
 *
 * <p>This mechanism is susceptible to forgery if an attacker wishes to recreate the response result manually.
 * That is, if the attacker knows the endpoint of the callback URL, they can manually change the Customer ID and the
 * Order ID that is returned.</p>
 *
 * <p>Using this service allows the implementor to create a Tamper Proof Seal of the Customer and Order ID
 * using a secret key (one option being the merchant secret key of the gateway).
 * On any web response from a gateway that has plain-text Order ID and Customer ID parameters returned, the
 * verifySeal() method should be called to check if the values have been tampered with.</p>
 *
 * @author Elbert Bautista (elbertbautista)
 */
public interface PaymentGatewayTamperProofSealService {

    public String createTamperProofSeal(String secretKey, String customerId, String orderId)
            throws NoSuchAlgorithmException, InvalidKeyException;

    public Boolean verifySeal(String seal, String secretKey, String customerId, String orderId)
            throws InvalidKeyException, NoSuchAlgorithmException;

}
