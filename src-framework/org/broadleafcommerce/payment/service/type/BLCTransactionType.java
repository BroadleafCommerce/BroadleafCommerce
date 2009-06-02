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


public class BLCTransactionType {

    private static final Map<String, BLCTransactionType> types = new Hashtable<String, BLCTransactionType>();

    public static BLCTransactionType AUTHORIZE  = new BLCTransactionType("AUTHORIZE");
    public static BLCTransactionType DEBIT = new BLCTransactionType("DEBIT");
    public static BLCTransactionType AUTHORIZEANDDEBIT = new BLCTransactionType("AUTHORIZEANDDEBIT");
    public static BLCTransactionType CREDIT = new BLCTransactionType("CREDIT");
    public static BLCTransactionType VOIDPAYMENT = new BLCTransactionType("VOIDPAYMENT");
    public static BLCTransactionType BALANCE = new BLCTransactionType("BALANCE");

    //TODO make any other type in BLC behave like this
    public static BLCTransactionType getInstance(String type) {
        return types.get(type);
    }

    private final String type;

    protected BLCTransactionType(String type) {
        this.type = type;
        types.put(type, this);
    }

    public String getType() {
        return type;
    }

    public static void main(String[] items) {
        System.out.println(BLCTransactionType.AUTHORIZE.getType());
    }
}
