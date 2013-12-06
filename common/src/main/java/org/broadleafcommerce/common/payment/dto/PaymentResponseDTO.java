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

package org.broadleafcommerce.common.payment.dto;

import org.broadleafcommerce.common.money.Money;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>The DTO object that represents the response coming back from any call to the Gateway.
 * This can either wrap an API result call or a translated HTTP Web response.
 * This can not only be the results of a transaction, but also a request for a Secure Token etc...</p>
 *
 * <p>Note: the success and validity flags are set to true by default, unless otherwise overridden by specific
 * gateway implementations</p>
 *
 * @author Elbert Bautista (elbertbautista)
 */
public class PaymentResponseDTO {

    protected String orderId;
    
    /**
     * The amount that was sent back from the gateway
     */
    protected Money amount;
    
    /**
     * Whether or not the transaction on the gateway was successful. This should be provided by the gateway alone.
     */
    protected Boolean successful = true;
    
    /**
     * Whether or not this response was tampered with. This used to verify that the response that was received on the
     * endpoint (which is intended to only be invoked from the payment gateway) actually came from the gateway and was not
     * otherwise maliciously invoked by a 3rd-party. 
     */
    protected Boolean valid = true;
    
    /**
     * for sale/authorize transactions, this will be the Credit Card object that was charged. This data is useful for showing
     * on an order confirmation screen.
     */
    protected CreditCardDTO creditCard;

    /**
     * A string representation of the response that came from the gateway. This should be a string serialization of
     * {@link #responseMap}.
     */
    protected String rawResponse;
    
    /**
     * A more convenient representation of {@link #rawResponse} to hold the response from the gateway.
     */
    protected Map<String, Object> responseMap = new HashMap<String, Object>();

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Money getAmount() {
        return amount;
    }

    public void setAmount(Money amount) {
        this.amount = amount;
    }

    public Boolean getSuccessful() {
        return successful;
    }

    public void setSuccessful(Boolean successful) {
        this.successful = successful;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public CreditCardDTO getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(CreditCardDTO creditCard) {
        this.creditCard = creditCard;
    }

    public String getRawResponse() {
        return rawResponse;
    }

    public void setRawResponse(String rawResponse) {
        this.rawResponse = rawResponse;
    }

    public Map<String, Object> getResponseMap() {
        return responseMap;
    }

    public void setResponseMap(Map<String, Object> responseMap) {
        this.responseMap = responseMap;
    }
}
