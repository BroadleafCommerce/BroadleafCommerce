/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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
package org.broadleafcommerce.common.payment;

import org.broadleafcommerce.common.BroadleafEnumerationType;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>Gateways/processors support a vast array of different features, and in some cases
 * a single API will perform several different functions based on the state of the request.
 * To fully support this and maintain a consistent API, this extensible enumeration of request types
 * are provided to aid the gateway's implementations determination of the correct request to construct
 * (in the case that the current implementation supports it).</p>
 *
 * For example:
 * 1. Certain gateways support the idea of a "Transparent Redirect" request.
 *    Within that set, only certain ones support the idea of a transparent redirect tokenization only request.
 *    In order to utilize the same {@link org.broadleafcommerce.common.payment.service.PaymentGatewayTransparentRedirectService},
 *    a particular request type, (e.g. {@link #CREATE_CUSTOMER_PAYMENT_TR} can be put on the
 *    {@link org.broadleafcommerce.common.payment.dto.PaymentRequestDTO} to distinguish which request to construct.
 * 2. Certain gateways support the idea of a "Detached Credit" also referred to as a "blind credit"
 *    In some cases, the gateways implementation utilizes the same "refund" api as a normal credit.
 *    {@link #DETACHED_CREDIT_REFUND} can be passed to an implementation's
 *    {@link org.broadleafcommerce.common.payment.service.PaymentGatewayTransactionService#refund(org.broadleafcommerce.common.payment.dto.PaymentRequestDTO)}
 *    method to distinguish what type of refund to construct.
 * 3. Some gateway implementations allow you to pass a manual authorization code received from the bank.
 *    For example, a customer service representative can take orders over the phone and call the bank directly
 *    to get an authorization code for the customer's card. In this scenario, if your gateway supports
 *    this, {@link #MANUAL_AUTHORIZATION} can be passed into an implementation's
 *    {@link org.broadleafcommerce.common.payment.service.PaymentGatewayTransactionService#authorize(org.broadleafcommerce.common.payment.dto.PaymentRequestDTO)}
 *    method to distinguish what type of authorization to construct.
 *
 * @author Elbert Bautista (elbertbautista)
 */
public class PaymentGatewayRequestType implements Serializable, BroadleafEnumerationType {

    private static final long serialVersionUID = 1L;

    private static final Map<String, PaymentGatewayRequestType> TYPES = new LinkedHashMap<String, PaymentGatewayRequestType>();

    public static final PaymentGatewayRequestType CREATE_CUSTOMER_PAYMENT_TR = new PaymentGatewayRequestType("CREATE_CUSTOMER_PAYMENT_TR", "Transparent Redirect Create Customer Payment Tokenization Request");
    public static final PaymentGatewayRequestType UPDATE_CUSTOMER_PAYMENT_TR = new PaymentGatewayRequestType("UPDATE_CUSTOMER_PAYMENT_TR", "Transparent Redirect Update Customer Payment Tokenization Request");
    public static final PaymentGatewayRequestType DETACHED_CREDIT_REFUND = new PaymentGatewayRequestType("DETACHED_CREDIT_REFUND", "Detached Credit Refund Request");
    public static final PaymentGatewayRequestType MANUAL_AUTHORIZATION = new PaymentGatewayRequestType("MANUAL_AUTHORIZATION", "Manual Authorization Request");

    public static PaymentGatewayRequestType getInstance(final String type) {
        return TYPES.get(type);
    }

    public static Map<String, PaymentGatewayRequestType> getTypes() {
        return TYPES;
    }

    private String type;
    private String friendlyType;

    public PaymentGatewayRequestType() {
        //do nothing
    }

    public PaymentGatewayRequestType(final String type, final String friendlyType) {
        this.friendlyType = friendlyType;
        setType(type);
    }

    public String getType() {
        return type;
    }

    public String getFriendlyType() {
        return friendlyType;
    }

    private void setType(final String type) {
        this.type = type;
        if (!TYPES.containsKey(type)) {
            TYPES.put(type, this);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!getClass().isAssignableFrom(obj.getClass()))
            return false;
        PaymentGatewayRequestType other = (PaymentGatewayRequestType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
}
