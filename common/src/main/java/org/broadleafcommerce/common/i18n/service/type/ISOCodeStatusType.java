/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
package org.broadleafcommerce.common.i18n.service.type;

import org.broadleafcommerce.common.BroadleafEnumerationType;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An extensible enumeration of ISO Code Status Types.
 * See {@link http://en.wikipedia.org/wiki/ISO_3166-1_alpha-2}
 *
 * @author Elbert Bautista (elbertbautista)
 */
public class ISOCodeStatusType implements Serializable, BroadleafEnumerationType {

    private static final long serialVersionUID = 1L;

    private static final Map<String, ISOCodeStatusType> TYPES = new LinkedHashMap<String, ISOCodeStatusType>();

    public static final ISOCodeStatusType OFFICIALLY_ASSIGNED = new ISOCodeStatusType("OFFICIALLY_ASSIGNED", "Officially assigned: assigned to a country, territory, or area of geographical interest.");
    public static final ISOCodeStatusType USER_ASSIGNED = new ISOCodeStatusType("USER_ASSIGNED", "User-assigned: free for assignment at the disposal of users.");
    public static final ISOCodeStatusType EXCEPTIONALLY_RESERVED = new ISOCodeStatusType("EXCEPTIONALLY_RESERVED", "Exceptionally reserved: reserved on request for restricted use.");
    public static final ISOCodeStatusType TRANSITIONALLY_RESERVED = new ISOCodeStatusType("TRANSITIONALLY_RESERVED", "Transitionally reserved: deleted from ISO 3166-1 but reserved transitionally.");
    public static final ISOCodeStatusType INDETERMINATELY_RESERVED = new ISOCodeStatusType("INDETERMINATELY_RESERVED", "Indeterminately reserved: used in coding systems associated with ISO 3166-1.");
    public static final ISOCodeStatusType NOT_USED = new ISOCodeStatusType("NOT_USED", "Not used: not used in ISO 3166-1 in deference to intergovernmental intellectual property organisation names.");
    public static final ISOCodeStatusType UNASSIGNED = new ISOCodeStatusType("UNASSIGNED", "Unassigned: free for assignment by the ISO 3166/MA only.");

    public static ISOCodeStatusType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;

    public ISOCodeStatusType() {
        //do nothing
    }

    public ISOCodeStatusType(final String type, final String friendlyType) {
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
        if (!getClass().isAssignableFrom(obj.getClass()))
            return false;
        ISOCodeStatusType other = (ISOCodeStatusType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

}

