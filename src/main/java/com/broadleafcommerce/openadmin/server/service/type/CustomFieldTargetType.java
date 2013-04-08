/*
 * Copyright 2008-2012 the original author or authors.
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

package com.broadleafcommerce.openadmin.server.service.type;

import org.broadleafcommerce.common.BroadleafEnumerationType;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * An extensible enumeration for a Custom Field Target Type.
 * Represent the attributes map on each of the target entity which can contain
 * a custom field.
 *
 * @author Jeff Fischer
 *
 */
public class CustomFieldTargetType implements Serializable, BroadleafEnumerationType {

    private static final long serialVersionUID = 1L;

    private static final Map<String, CustomFieldTargetType> TYPES = new HashMap<String, CustomFieldTargetType>();

    public static final CustomFieldTargetType CUSTOMER = new CustomFieldTargetType("org.broadleafcommerce.profile.core.domain.CustomerImpl", "Customer");
    public static final CustomFieldTargetType SKU = new CustomFieldTargetType("org.broadleafcommerce.core.catalog.domain.SkuImpl", "Sku");
    public static final CustomFieldTargetType ORDERITEM = new CustomFieldTargetType("org.broadleafcommerce.core.order.domain.OrderItemImpl", "Order Item");

    public static CustomFieldTargetType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;

    public CustomFieldTargetType() {
        //do nothing
    }

    public CustomFieldTargetType(final String type, final String friendlyType) {
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
        CustomFieldTargetType other = (CustomFieldTargetType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
}
