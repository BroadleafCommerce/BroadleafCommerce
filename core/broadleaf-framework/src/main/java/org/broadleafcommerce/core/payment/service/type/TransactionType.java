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
 * An extendible enumeration of payment transaction types.
 * 
 * @author jfischer
 *
 */
public class TransactionType implements Serializable, BroadleafEnumerationType {

    private static final long serialVersionUID = 1L;

    private static final Map<String, TransactionType> TYPES = new LinkedHashMap<String, TransactionType>();

    public static final TransactionType AUTHORIZE = new TransactionType("AUTHORIZE", "Authorize");
    public static final TransactionType DEBIT = new TransactionType("DEBIT", "Debit");
    public static final TransactionType AUTHORIZEANDDEBIT = new TransactionType("AUTHORIZEANDDEBIT", "Authorize and Debit");
    public static final TransactionType CREDIT = new TransactionType("CREDIT", "Credit");
    public static final TransactionType VOIDPAYMENT = new TransactionType("VOIDPAYMENT", "Void Payment");
    public static final TransactionType BALANCE = new TransactionType("BALANCE", "Balance");
    public static final TransactionType REVERSEAUTHORIZE = new TransactionType("REVERSEAUTHORIZE", "Reverse Authorize");
    public static final TransactionType PARTIALPAYMENT = new TransactionType("PARTIALPAYMENT", "Partial Payment");

    public static TransactionType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;

    public TransactionType() {
        //do nothing
    }

    public TransactionType(final String type, final String friendlyType) {
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
        TransactionType other = (TransactionType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
}
