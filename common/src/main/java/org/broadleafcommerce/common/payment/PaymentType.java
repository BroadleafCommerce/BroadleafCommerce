/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.common.payment;

import org.broadleafcommerce.common.BroadleafEnumerationType;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>This represents types of payments that can be applied to an order. There might be multiple order payments with the
 * same type on an order if the customer can pay with multiple cards (like 2 credit cards or 3 gift cards).</p>
 * 
 * @see {@link OrderPayment}
 * @author Phillip Verheyden (phillipuniverse)
 */
public class PaymentType implements Serializable, BroadleafEnumerationType {

    private static final long serialVersionUID = 1L;

    private static final Map<String, PaymentType> TYPES = new LinkedHashMap<String, PaymentType>();

    public static final PaymentType GIFT_CARD = new PaymentType("GIFT_CARD", "Gift Card");
    public static final PaymentType CREDIT_CARD = new PaymentType("CREDIT_CARD", "Credit Card");
    public static final PaymentType BANK_ACCOUNT = new PaymentType("BANK_ACCOUNT", "Bank Account");
    public static final PaymentType CHECK = new PaymentType("CHECK", "Check");
    public static final PaymentType ELECTRONIC_CHECK = new PaymentType("ELECTRONIC_CHECK", "Electronic Check");
    public static final PaymentType WIRE = new PaymentType("WIRE", "Wire Transfer");
    public static final PaymentType MONEY_ORDER = new PaymentType("MONEY_ORDER", "Money Order");
    public static final PaymentType CUSTOMER_CREDIT = new PaymentType("CUSTOMER_CREDIT", "Customer Credit");
    public static final PaymentType COD = new PaymentType("COD", "Collect On Delivery");
    /**
     * Intended for modules like PayPal Express Checkout
     *
     * It is important to note that in this system an `UNCONFIRMED` `THIRD_PARTY_ACCOUNT` has a specific use case.
     * The Order Payment amount can be variable. That means, when you confirm that `UNCONFIRMED` transaction, you can pass in a different amount
     * than what was sent as the initial transaction amount. see (AdjustOrderPaymentsActivity)
     *
     * Note that not all third party gateways support this feature described above.
     * Make sure to the gateway does before assigning this type to your Order Payment.
     */
    public static final PaymentType THIRD_PARTY_ACCOUNT = new PaymentType("THIRD_PARTY_ACCOUNT", "3rd-Party Account");

    public static PaymentType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;

    public PaymentType() {
        //do nothing
    }

    public PaymentType(final String type, final String friendlyType) {
        this.friendlyType = friendlyType;
        setType(type);
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
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
        if (getClass() != obj.getClass())
            return false;
        PaymentType other = (PaymentType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
}
