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
 * @author Elbert Bautista (elbertbautista)
 *
 * The DTO object that represents the response coming back from any call to the Gateway.
 * This can either wrap an API result call or a translated HTTP Web response.
 * This can not only be the results of a transaction, but also a request for a Secure Token etc...
 *
 * The following values can be set on this object.
 *
 * amount - the amount that was sent back on the transaction
 * successful - whether or not the transaction on the gateway was successful
 * valid - whether the response was valid (e.g. the tamper proof seal,
 *   or the web response was actually from the gateway and wasn't manipulated)
 * credit card - for sale/authorize transactions, this will be the Credit Card object that was charged
 *   (Useful for showing on the Order Confirmation Screen)
 * rawResponse = a String representation of the response coming back
 * responseMap - a convenience Map representation of the fields coming back
 *
 * Note: the success and validtity flags are set to true by default, unless otherwise overriten by the
 * gateway implementations
 *
 */
public class PaymentResponseDTO {

    protected String orderId;
    protected Money amount;
    protected Boolean successful = true;
    protected Boolean valid = true;
    protected CreditCardDTO creditCard;
    protected String rawResponse;
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
