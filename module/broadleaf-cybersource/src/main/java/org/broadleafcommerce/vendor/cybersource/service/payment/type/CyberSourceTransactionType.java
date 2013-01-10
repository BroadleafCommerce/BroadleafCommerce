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

package org.broadleafcommerce.vendor.cybersource.service.payment.type;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.broadleafcommerce.presentation.BroadleafEnumerationType;

/**
 * An extendible enumeration of transaction types.
 * 
 * @author jfischer
 */
public class CyberSourceTransactionType implements Serializable, BroadleafEnumerationType {

    private static final long serialVersionUID = 1L;

    private static final Map<String, CyberSourceTransactionType> TYPES = new HashMap<String, CyberSourceTransactionType>();

    public static final CyberSourceTransactionType AUTHORIZE  = new CyberSourceTransactionType("AUTHORIZE", "Authorize");
    public static final CyberSourceTransactionType CAPTURE = new CyberSourceTransactionType("CAPTURE", "Capture");
    public static final CyberSourceTransactionType AUTHORIZEANDCAPTURE  = new CyberSourceTransactionType("AUTHORIZEANDCAPTURE", "Authorize and Capture");
    public static final CyberSourceTransactionType CREDIT = new CyberSourceTransactionType("CREDIT", "Credit");
    public static final CyberSourceTransactionType VOIDTRANSACTION = new CyberSourceTransactionType("VOIDTRANSACTION", "Void Transaction");
    public static final CyberSourceTransactionType REVERSEAUTHORIZE = new CyberSourceTransactionType("REVERSEAUTHORIZE", "Reverse Authorize");

    public static CyberSourceTransactionType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;

    public CyberSourceTransactionType() {
        //do nothing
    }

    public CyberSourceTransactionType(final String type, final String friendlyType) {
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
        CyberSourceTransactionType other = (CyberSourceTransactionType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
}
