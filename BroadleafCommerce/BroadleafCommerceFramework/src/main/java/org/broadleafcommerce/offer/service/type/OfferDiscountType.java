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
package org.broadleafcommerce.offer.service.type;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Map;

/**
 * An extendible enumeration of discount types.
 *
 */
public class OfferDiscountType implements Serializable
{
    private static final long serialVersionUID = 1L;

    private static final Map<String, OfferDiscountType> types = new Hashtable<String, OfferDiscountType>();

    public static OfferDiscountType PERCENT_OFF = new OfferDiscountType("PERCENT_OFF");
    public static OfferDiscountType AMOUNT_OFF = new OfferDiscountType("AMOUNT_OFF");
    public static OfferDiscountType FIX_PRICE = new OfferDiscountType("FIX_PRICE");

    public static OfferDiscountType getInstance(String type) {
        return types.get(type);
    }

    private String type;

    public OfferDiscountType() {
        //do nothing
    }

    public OfferDiscountType(String type) {
        setType(type);
    }

    public void setType(String type) {
        this.type = type;
        if (!types.containsKey(type)) {
            types.put(type, this);
        }
    }

    public String getType() {
        return type;
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
        OfferDiscountType other = (OfferDiscountType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

}
