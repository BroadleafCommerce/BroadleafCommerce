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
package org.broadleafcommerce.pricing.service.workflow.type;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * An extendible enumeration of shipping service types.
 * 
 * @author jfischer
 *
 */
public class ShippingServiceType implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Map<String, ShippingServiceType> TYPES = new HashMap<String, ShippingServiceType>();

    public static final ShippingServiceType BANDED_SHIPPING = new ShippingServiceType("BANDED_SHIPPING");
    public static final ShippingServiceType USPS = new ShippingServiceType("USPS");
    public static final ShippingServiceType FED_EX = new ShippingServiceType("FED_EX");
    public static final ShippingServiceType UPS = new ShippingServiceType("UPS");
    public static final ShippingServiceType DHL = new ShippingServiceType("DHL");

    public static ShippingServiceType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;

    public ShippingServiceType() {
        //do nothing
    }

    public ShippingServiceType(final String type) {
        setType(type);
    }

    public String getType() {
        return type;
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
        ShippingServiceType other = (ShippingServiceType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
}
