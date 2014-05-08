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
package org.broadleafcommerce.common.sandbox.domain;


import org.broadleafcommerce.common.BroadleafEnumerationType;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by bpolster.
 */
public class SandBoxType implements Serializable, BroadleafEnumerationType {

    private static final long serialVersionUID = 1L;

    private static final Map<String, SandBoxType> TYPES = new LinkedHashMap<String, SandBoxType>();

    public static final SandBoxType USER = new SandBoxType("USER", "User", 3);
    public static final SandBoxType APPROVAL = new SandBoxType("APPROVAL", "Approval", 2);
    public static final SandBoxType DEFAULT = new SandBoxType("DEFAULT", "Default", 2);
    public static final SandBoxType PRODUCTION = new SandBoxType("PRODUCTION", "Production", 1);


    public static SandBoxType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;
    private Integer priority;

    public SandBoxType() {
        //do nothing
    }

    public SandBoxType(final String type, final String friendlyType, final Integer priority) {
        this.friendlyType = friendlyType;
        this.priority = priority;
        setType(type);
    }

    public String getType() {
        return type;
    }

    public String getFriendlyType() {
        return friendlyType;
    }

    public Integer getPriority() {
        return priority;
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
        if (!(obj instanceof SandBoxType))
            return false;
        SandBoxType other = (SandBoxType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

}
