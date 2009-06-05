/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.payment.service.type;

import java.util.Hashtable;
import java.util.Map;

/**
 * An extendible enumeration of payment info types.
 * 
 * @author jfischer
 *
 */
public class PaymentInfoType {

    private static final Map<String, PaymentInfoType> types = new Hashtable<String, PaymentInfoType>();

    public static PaymentInfoType GIFT_CARD = new PaymentInfoType("GIFT_CARD");
    public static PaymentInfoType CREDIT_CARD = new PaymentInfoType("CREDIT_CARD");
    public static PaymentInfoType BANK_ACCOUNT = new PaymentInfoType("BANK_ACCOUNT");

    public static PaymentInfoType getInstance(String type) {
        return types.get(type);
    }

    private final String type;

    protected PaymentInfoType(String type) {
        this.type = type;
        types.put(type, this);
    }

    public String getType() {
        return type;
    }

}
