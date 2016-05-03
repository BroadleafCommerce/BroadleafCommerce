/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
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

