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
package org.broadleafcommerce.payment.service.workflow;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Map;

/**
 * An extendible enumeration of payment action types.
 * 
 * @author jfischer
 *
 */
public class PaymentActionType implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Map<String, PaymentActionType> types = new Hashtable<String, PaymentActionType>();

    public static PaymentActionType AUTHORIZE = new PaymentActionType("AUTHORIZE");
    public static PaymentActionType DEBIT = new PaymentActionType("DEBIT");
    public static PaymentActionType AUTHORIZEANDDEBIT = new PaymentActionType("AUTHORIZEANDDEBIT");
    public static PaymentActionType CREDIT = new PaymentActionType("CREDIT");
    public static PaymentActionType VOID = new PaymentActionType("VOID");
    public static PaymentActionType BALANCE = new PaymentActionType("BALANCE");

    public static PaymentActionType getInstance(String type) {
        return types.get(type);
    }

    private String type;

    public PaymentActionType() {
        //do nothing
    }

    public PaymentActionType(String type) {
        setType(type);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        if (!types.containsKey(type)) {
            types.put(type, this);
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
        PaymentActionType other = (PaymentActionType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
}
