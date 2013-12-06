/*
 * Copyright 2008-2013 the original author or authors.
 *
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
 */

package org.broadleafcommerce.common.payment;

import org.broadleafcommerce.common.BroadleafEnumerationType;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * <p>This is designed such that individual payment modules will extend this to add their own type. For instance, while
 * this class does not explicitly have a 'Braintree' payment gateway type, the Braintree module will provide an extension
 * to this class and add itself in the list of types.</p>
 * 
 * <p>This is especially useful in auditing scenarios so that, at a glance, you can easily see what gateway a particular
 * order payment was processed by.</p>
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
public class PaymentGatewayType implements Serializable, BroadleafEnumerationType {

    private static final long serialVersionUID = 1L;

    private static final Map<String, PaymentGatewayType> TYPES = new LinkedHashMap<String, PaymentGatewayType>();

    public static PaymentGatewayType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;

    public PaymentGatewayType() {
        // do nothing
    }

    public PaymentGatewayType(String type, String friendlyType) {
        this.friendlyType = friendlyType;
        setType(type);
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
        if (getClass() != obj.getClass())
            return false;
        PaymentGatewayType other = (PaymentGatewayType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
}
