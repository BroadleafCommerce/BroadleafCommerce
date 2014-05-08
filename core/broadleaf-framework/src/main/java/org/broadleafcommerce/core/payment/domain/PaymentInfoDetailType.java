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
package org.broadleafcommerce.core.payment.domain;

import org.broadleafcommerce.common.BroadleafEnumerationType;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The PaymentInfoDetailType is used to represent in Broadleaf the the type of action used by the payment provider.
 *
 * The following types are used for transactions.
 *
 * CAPTURE - Funds have been charged, submitted or debited.
 * REFUND - Funds have been refunded or credited.
 * REVERSE_AUTH - Funds have been reverse authorized; this concept is used by credit card payment processors where funds
 * are first authorized to later be captured.
 *
 * @author Jerry Ocanas (jocanas)
 */
public class PaymentInfoDetailType implements Serializable, BroadleafEnumerationType {

    private static final long serialVersionUID = 1L;

    private static final Map<String, PaymentInfoDetailType> TYPES = new LinkedHashMap<String, PaymentInfoDetailType>();

    public static final PaymentInfoDetailType CAPTURE = new PaymentInfoDetailType("CAPTURE", "Capture");
    public static final PaymentInfoDetailType REFUND = new PaymentInfoDetailType("REFUND", "Refund");
    public static final PaymentInfoDetailType REVERSE_AUTH = new PaymentInfoDetailType("REVERSE_AUTH", "Reverse Auth");

    public static PaymentInfoDetailType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;

    public PaymentInfoDetailType() {
        // do nothing
    }

    public PaymentInfoDetailType(String type, String friendlyType) {
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
        if (!TYPES.containsKey(type)){
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
        if (!(obj instanceof PaymentInfoDetailType))
            return false;
        PaymentInfoDetailType other = (PaymentInfoDetailType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
}
