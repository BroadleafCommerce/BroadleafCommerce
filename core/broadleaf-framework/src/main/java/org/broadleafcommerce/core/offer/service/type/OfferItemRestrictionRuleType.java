/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.offer.service.type;

import org.broadleafcommerce.common.BroadleafEnumerationType;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An extendible enumeration of offer item restriction types. Determines how items in the order can be used across multiple promotions
 * 
 * NONE - Cannot be used in more than one promotion
 * QUALIFIER - Can be used as a qualifier for multiple promotions, but cannot be a target for multiple promotions
 * TARGET - Can be used as a target for multiple promotions, but cannot be used as a qualifier for multiple promotions
 * QUALIFIER_TARGET - Can be used as a qualifier and target in multiple promotions
 */
public class OfferItemRestrictionRuleType implements Serializable, BroadleafEnumerationType {
    
    private static final long serialVersionUID = 1L;

    private static final Map<String, OfferItemRestrictionRuleType> TYPES = new LinkedHashMap<String, OfferItemRestrictionRuleType>();

    public static final OfferItemRestrictionRuleType NONE = new OfferItemRestrictionRuleType("NONE", "None");
    public static final OfferItemRestrictionRuleType QUALIFIER = new OfferItemRestrictionRuleType("QUALIFIER", "Qualifier Only");
    public static final OfferItemRestrictionRuleType TARGET = new OfferItemRestrictionRuleType("TARGET", "Target Only");
    public static final OfferItemRestrictionRuleType QUALIFIER_TARGET = new OfferItemRestrictionRuleType("QUALIFIER_TARGET", "Qualifier And Target");

    public static OfferItemRestrictionRuleType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;

    public OfferItemRestrictionRuleType() {
        //do nothing
    }

    public OfferItemRestrictionRuleType(final String type, final String friendlyType) {
        this.friendlyType = friendlyType;
        setType(type);
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
        OfferItemRestrictionRuleType other = (OfferItemRestrictionRuleType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

}
