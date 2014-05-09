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
package org.broadleafcommerce.core.payment.service.type;

import org.broadleafcommerce.common.BroadleafEnumerationType;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An extendible enumeration of payment info types.
 * 
 * @author jfischer
 *
 */
public class PaymentInfoType implements Serializable, BroadleafEnumerationType {

    private static final long serialVersionUID = 1L;

    private static final Map<String, PaymentInfoType> TYPES = new LinkedHashMap<String, PaymentInfoType>();

    public static final PaymentInfoType GIFT_CARD = new PaymentInfoType("GIFT_CARD", "Gift Card");
    public static final PaymentInfoType CREDIT_CARD = new PaymentInfoType("CREDIT_CARD", "Credit Card");
    public static final PaymentInfoType BANK_ACCOUNT = new PaymentInfoType("BANK_ACCOUNT", "Bank Account");
    public static final PaymentInfoType PAYPAL = new PaymentInfoType("PAYPAL", "PayPal");
    public static final PaymentInfoType CHECK = new PaymentInfoType("CHECK", "Check");
    public static final PaymentInfoType ELECTRONIC_CHECK = new PaymentInfoType("ELECTRONIC_CHECK", "Electronic Check");
    public static final PaymentInfoType WIRE = new PaymentInfoType("WIRE", "Wire Transfer");
    public static final PaymentInfoType MONEY_ORDER = new PaymentInfoType("MONEY_ORDER", "Money Order");
    public static final PaymentInfoType CUSTOMER_CREDIT = new PaymentInfoType("CUSTOMER_CREDIT", "Customer Credit");
    public static final PaymentInfoType COD = new PaymentInfoType("COD", "Collect On Delivery");
    @Deprecated
    public static final PaymentInfoType ACCOUNT = new PaymentInfoType("ACCOUNT", "Account");

    public static PaymentInfoType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;

    public PaymentInfoType() {
        //do nothing
    }

    public PaymentInfoType(final String type, final String friendlyType) {
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
        PaymentInfoType other = (PaymentInfoType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
}
