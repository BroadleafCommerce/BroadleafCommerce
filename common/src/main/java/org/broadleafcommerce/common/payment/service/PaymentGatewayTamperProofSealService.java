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

package org.broadleafcommerce.common.payment.service;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Several Gateways have a method of communication that uses the HTTP Request/Response model
 * and the client's browser to transmit transaction result information back to the system. In some cases,
 * these gateways don't return a tokenized response, but plain-text parameters.
 * For example: In cases where the application needs to complete an order asynchronously (via a Webhook or SilentPost)
 * we need to send information about the customer id and the order id to the gateway in the original request so that
 * a call back to the server from the gateway will return those values and the system will know how to
 * associate the completed transaction with an order.
 *
 * This mechanism is susceptible to forgery if an attacker wishes to recreate the response result manually.
 * That is, if the attacker knows the endpoint of the callback URL, they can manually change the Customer ID and the
 * Order ID that is returned.
 *
 * Using this service allows the implementor to create a Tamper Proof Seal of the Customer and Order ID
 * using a secret key (one option being the merchant secret key of the gateway).
 * On any web response from a gateway that has plain-text Order ID and Customer ID parameters returned, the
 * verifySeal() method should be called to check if the values have been tampered with.
 *
 * @author Elbert Bautista (elbertbautista)
 *
 */
public interface PaymentGatewayTamperProofSealService {

    public String createTamperProofSeal(String secretKey, String customerId, String orderId)
            throws NoSuchAlgorithmException, InvalidKeyException;

    public Boolean verifySeal(String seal, String secretKey, String customerId, String orderId)
            throws InvalidKeyException, NoSuchAlgorithmException;

}
