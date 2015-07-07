/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.presentation.client;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author ppatel/bpolster
 *
 */
public class RuleType implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Map<String, RuleType> TYPES = new HashMap<String, RuleType>();

    public static final RuleType CUSTOMER  = new RuleType("1", "Customer");
    public static final RuleType REQUEST  = new RuleType("2", "Request");
    public static final RuleType TIME  = new RuleType("3", "Time");
    public static final RuleType PRODUCT  = new RuleType("4", "Product");
    public static final RuleType ORDER_ITEM  = new RuleType("5", "OrderItem");
    public static final RuleType LOCALE  = new RuleType("6", "Locale");
    public static final RuleType ORDER_ITEM_HISTORY  = new RuleType("7", "OrderItemHistory");
   

    public static RuleType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;

    public RuleType() {
        //do nothing
    }

    public RuleType(final String type, final String friendlyType) {
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
        } else {
            throw new RuntimeException("Cannot add the type: (" + type + "). It already exists as a type via " + getInstance(type).getClass().getName());
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
        RuleType other = (RuleType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
}
