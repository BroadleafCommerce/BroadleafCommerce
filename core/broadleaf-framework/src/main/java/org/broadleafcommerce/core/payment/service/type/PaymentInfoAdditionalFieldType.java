/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.payment.service.type;


import org.broadleafcommerce.common.BroadleafEnumerationType;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class PaymentInfoAdditionalFieldType implements Serializable, BroadleafEnumerationType {

    private static final long serialVersionUID = 1L;

    private static final Map<String, PaymentInfoAdditionalFieldType> TYPES = new LinkedHashMap<String, PaymentInfoAdditionalFieldType>();

    public static final PaymentInfoAdditionalFieldType NAME_ON_CARD = new PaymentInfoAdditionalFieldType("NAME_ON_CARD", "Cardholders Name");
    public static final PaymentInfoAdditionalFieldType CARD_TYPE = new PaymentInfoAdditionalFieldType("CARD_TYPE", "Card Type");
    public static final PaymentInfoAdditionalFieldType EXP_MONTH = new PaymentInfoAdditionalFieldType("EXP_MONTH", "Expiration Month");
    public static final PaymentInfoAdditionalFieldType EXP_YEAR = new PaymentInfoAdditionalFieldType("EXP_YEAR", "Expiration Year");
    
    // Generic Fields that can be used for multiple payment types
    public static final PaymentInfoAdditionalFieldType PAYMENT_TYPE = new PaymentInfoAdditionalFieldType("PAYMENT_TYPE", "Type of Payment");
    public static final PaymentInfoAdditionalFieldType NAME_ON_ACCOUNT = new PaymentInfoAdditionalFieldType("NAME_ON_ACCOUNT", "Name on Account");
    public static final PaymentInfoAdditionalFieldType ACCOUNT_TYPE = new PaymentInfoAdditionalFieldType("ACCOUNT_TYPE", "Account Type");
    public static final PaymentInfoAdditionalFieldType LAST_FOUR = new PaymentInfoAdditionalFieldType("LAST_FOUR", "Last Four Digits ofAccount or CC");
    public static final PaymentInfoAdditionalFieldType GIFT_CARD_NUM = new PaymentInfoAdditionalFieldType("GIFT_CARD_NUM", "Gift Card Number");
    public static final PaymentInfoAdditionalFieldType EMAIL = new PaymentInfoAdditionalFieldType("EMAIL", "Email");
    public static final PaymentInfoAdditionalFieldType ACCOUNT_CREDIT_NUM = new PaymentInfoAdditionalFieldType("ACCOUNT_CREDIT_NUM", "Account Credit Number");   
    public static final PaymentInfoAdditionalFieldType AUTH_CODE = new PaymentInfoAdditionalFieldType("AUTH_CODE", "Authorization Code");
    public static final PaymentInfoAdditionalFieldType REQUEST_ID = new PaymentInfoAdditionalFieldType("REQUEST_ID", "Request Id");
    public static final PaymentInfoAdditionalFieldType SUBSCRIPTION_ID = new PaymentInfoAdditionalFieldType("SUBSCRIPTION_ID", "Subscription Id");
    public static final PaymentInfoAdditionalFieldType SUBSCRIPTION_TITLE = new PaymentInfoAdditionalFieldType("SUBSCRIPTION_TITLE", "Subscription Title");

    public static PaymentInfoAdditionalFieldType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;

    public PaymentInfoAdditionalFieldType() {
        //do nothing
    }

    public PaymentInfoAdditionalFieldType(final String type, final String friendlyType) {
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
        if (getClass() != obj.getClass())
            return false;
        PaymentInfoAdditionalFieldType other = (PaymentInfoAdditionalFieldType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
}
