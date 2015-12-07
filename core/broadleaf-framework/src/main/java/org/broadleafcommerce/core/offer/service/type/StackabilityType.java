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
package org.broadleafcommerce.core.offer.service.type;

import org.broadleafcommerce.common.BroadleafEnumerationType;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An extendible enumeration of offer types.
 *
 */
public class StackabilityType implements Serializable, BroadleafEnumerationType, Comparable<StackabilityType> {

    private static final long serialVersionUID = 1L;

    private static final Map<String, StackabilityType> TYPES = new LinkedHashMap<String, StackabilityType>();
    public static final StackabilityType ORDER_ITEM = new StackabilityType("NO", "No", 1000);
    public static final StackabilityType ORDER = new StackabilityType("YES", "Yes, with other stackable offers", 2000);
    public static final StackabilityType FULFILLMENT_GROUP = new StackabilityType("YES_ALWAYS", "Yes, always", 3000);


    public static StackabilityType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;
    private int order;

    public StackabilityType() {
        //do nothing
    }

    public StackabilityType(final String type, final String friendlyType, int order) {
        this.friendlyType = friendlyType;
        setType(type);
        setOrder(order);
    }

    public void setType(final String type) {
        this.type = type;
        if (!TYPES.containsKey(type)) {
            TYPES.put(type, this);
        }
    }

    public String getType() {
        return type;
    }

    public String getFriendlyType() {
        return friendlyType;
    }
    
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
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
        StackabilityType other = (StackabilityType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
    
    @Override
    public int compareTo(StackabilityType arg0) {
        return this.order - arg0.order;
    }

}
