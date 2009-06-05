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
package org.broadleafcommerce.payment;

import java.util.Hashtable;
import java.util.Map;

/**
 * An extendible enumeration of credit card types.
 * 
 * @author jfischer
 *
 */
public class CreditCardType {

    private static final Map<String, CreditCardType> types = new Hashtable<String, CreditCardType>();

    public static CreditCardType MASTERCARD  = new CreditCardType("MASTERCARD");
    public static CreditCardType VISA  = new CreditCardType("VISA");
    public static CreditCardType AMEX  = new CreditCardType("AMEX");
    public static CreditCardType DINERSCLUB_CARTEBLANCHE  = new CreditCardType("DINERSCLUB_CARTEBLANCHE");
    public static CreditCardType DISCOVER  = new CreditCardType("DISCOVER");
    public static CreditCardType ENROUTE  = new CreditCardType("ENROUTE");
    public static CreditCardType JCB  = new CreditCardType("JCB");

    public static CreditCardType getInstance(String type) {
        return types.get(type);
    }

    private final String type;

    protected CreditCardType(String type) {
        this.type = type;
        types.put(type, this);
    }

    public String getType() {
        return type;
    }
}
