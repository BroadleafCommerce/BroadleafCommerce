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

package org.broadleafcommerce.core.order.service.type;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.broadleafcommerce.common.BroadleafEnumerationType;

/**
 * An extendible enumeration of fulfillment group status types.
 * 
 * @author aangus
 *
 */
public class FulfillmentGroupStatusType implements Serializable, BroadleafEnumerationType {

    private static final long serialVersionUID = 1L;

    private static final Map<String, FulfillmentGroupStatusType> TYPES = new HashMap<String, FulfillmentGroupStatusType>();

    public static final FulfillmentGroupStatusType PROCESSING = new FulfillmentGroupStatusType("PROCESSING", "Processing");
    public static final FulfillmentGroupStatusType SHIPPED = new FulfillmentGroupStatusType("SHIPPED", "Shipped");
    public static final FulfillmentGroupStatusType DELIVERED = new FulfillmentGroupStatusType("DELIVERED", "Delivered");

    public static FulfillmentGroupStatusType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;

    public FulfillmentGroupStatusType() {
        //do nothing
    }

    public FulfillmentGroupStatusType(final String type, final String friendlyType) {
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
        FulfillmentGroupStatusType other = (FulfillmentGroupStatusType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
}
