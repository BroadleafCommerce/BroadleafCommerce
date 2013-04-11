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

package org.broadleafcommerce.core.inventory.service.type;

import org.broadleafcommerce.common.BroadleafEnumerationType;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An extendible enumeration of availability status types.
 * 
 * @author jfischer
 * 
 * @deprecated This is no longer required and is instead implemented as a third-party inventory module
 */
@Deprecated
public class AvailabilityStatusType implements Serializable, BroadleafEnumerationType {

    private static final long serialVersionUID = 1L;
    private static final Map<String, AvailabilityStatusType> TYPES = new LinkedHashMap<String, AvailabilityStatusType>();

    public static final AvailabilityStatusType AVAILABLE  = new AvailabilityStatusType("AVAILABLE", "Available");
    public static final AvailabilityStatusType UNAVAILABLE  = new AvailabilityStatusType("UNAVAILABLE", "Unavailable");
    public static final AvailabilityStatusType BACKORDERED  = new AvailabilityStatusType("BACKORDERED", "Back Ordered");
    
    public static AvailabilityStatusType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;

    public AvailabilityStatusType() {
        //do nothing
    }

    public AvailabilityStatusType(final String type, final String friendlyType) {
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
        if (getClass() != obj.getClass())
            return false;
        AvailabilityStatusType other = (AvailabilityStatusType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
}
