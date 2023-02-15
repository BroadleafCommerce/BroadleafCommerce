/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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

    public static final PaymentType GIFT_CARD = new PaymentType("GIFT_CARD", "Gift Card", false);
    public static final PaymentType CREDIT_CARD = new PaymentType("CREDIT_CARD", "Credit Card", true, true);
    public static final PaymentType BANK_ACCOUNT = new PaymentType("BANK_ACCOUNT", "Bank Account", false);
    public static final PaymentType CHECK = new PaymentType("CHECK", "Check", false);
    public static final PaymentType ELECTRONIC_CHECK = new PaymentType("ELECTRONIC_CHECK", "Electronic Check", false);
    public static final PaymentType WIRE = new PaymentType("WIRE", "Wire Transfer", false);
    public static final PaymentType MONEY_ORDER = new PaymentType("MONEY_ORDER", "Money Order", false);
    public static final PaymentType CUSTOMER_CREDIT = new PaymentType("CUSTOMER_CREDIT", "Customer Credit", false);
    public static final PaymentType COD = new PaymentType("COD", "Collect On Delivery", false);
    public static final PaymentType CUSTOMER_PAYMENT = new PaymentType("CUSTOMER_PAYMENT", "Customer Payment", true);
    public static final PaymentType PURCHASE_ORDER = new PaymentType("PURCHASE_ORDER", "Purchase Order", false);
    public static final PaymentType APPLE_PAY = new PaymentType("APPLE_PAY", "Apple Pay", true, true);
    public static final PaymentType GOOGLE_PAY = new PaymentType("GOOGLE_PAY", "Google Pay", true, true);
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
    public static final PaymentType THIRD_PARTY_ACCOUNT = new PaymentType("THIRD_PARTY_ACCOUNT", "3rd-Party Account", true);

    public static PaymentType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;
    private boolean isFinalPayment;
    private boolean isCreditCardType;

    public PaymentType() {
        //do nothing
    }

    public PaymentType(final String type, final String friendlyType) {
        this.friendlyType = friendlyType;
        setType(type);
        this.isFinalPayment = false;
        this.isCreditCardType = false;
    }
    
    public PaymentType(final String type, final String friendlyType, final boolean isFinalPayment) {
        this.friendlyType = friendlyType;
        this.isFinalPayment = isFinalPayment;
        this.isCreditCardType = false;
        setType(type);
    }

    public PaymentType(final String type, final String friendlyType, final boolean isFinalPayment, final boolean isCreditCardType) {
        this.friendlyType = friendlyType;
        this.isFinalPayment = isFinalPayment;
        this.isCreditCardType = isCreditCardType;
        setType(type);
    }
    
    public boolean getIsFinalPayment() {
        return isFinalPayment;
    }

    public boolean isCreditCardType() {
        return isCreditCardType;
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
        if (!getClass().isAssignableFrom(obj.getClass()))
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
