/*
 * #%L
 * BroadleafCommerce Advanced Offer
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
package org.broadleafcommerce.core.offer.service.type;

import org.broadleafcommerce.common.BroadleafEnumerationType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OfferProrationType implements Serializable, BroadleafEnumerationType {

    private static final long serialVersionUID = 1L;

    private static final Map<String, OfferProrationType> TYPES = new LinkedHashMap<String, OfferProrationType>();

    public static final OfferProrationType TARGET_ONLY = new OfferProrationType("TARGET_ONLY", "Record discount to targets");
    public static final OfferProrationType TARGET_AND_QUALIFIER = new OfferProrationType("TARGET_AND_QUALIFIER", "Distribute discount between qualifiers and targets");

    public static OfferProrationType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;

    public OfferProrationType() {
        //do nothing
    }

    public OfferProrationType(final String type, final String friendlyType) {
        this.friendlyType = friendlyType;
        setType(type);
    }

    public static List<OfferProrationType> getTypes() {
        List<OfferProrationType> list = new ArrayList<OfferProrationType>(TYPES.size());
        for (Map.Entry<String, OfferProrationType> entry : TYPES.entrySet()) {
            list.add(entry.getValue());
        }
        return list;
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
        OfferProrationType other = (OfferProrationType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

}

