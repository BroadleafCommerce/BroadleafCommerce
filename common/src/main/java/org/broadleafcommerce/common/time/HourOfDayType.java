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
package org.broadleafcommerce.common.time;

import org.broadleafcommerce.common.BroadleafEnumerationType;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An extendible enumeration of container shape types.
 * 
 * @author jfischer
 */
public class HourOfDayType implements Serializable, BroadleafEnumerationType {

    private static final long serialVersionUID = 1L;

    private static final Map<String, HourOfDayType> TYPES = new LinkedHashMap<String, HourOfDayType>();

    public static final HourOfDayType ZERO  = new HourOfDayType("0", "00");
    public static final HourOfDayType ONE  = new HourOfDayType("1", "01");
    public static final HourOfDayType TWO  = new HourOfDayType("2", "02");
    public static final HourOfDayType THREE  = new HourOfDayType("3", "03");
    public static final HourOfDayType FOUR  = new HourOfDayType("4", "04");
    public static final HourOfDayType FIVE  = new HourOfDayType("5", "05");
    public static final HourOfDayType SIX  = new HourOfDayType("6", "06");
    public static final HourOfDayType SEVEN  = new HourOfDayType("7", "07");
    public static final HourOfDayType EIGHT  = new HourOfDayType("8", "08");
    public static final HourOfDayType NINE  = new HourOfDayType("9", "09");
    public static final HourOfDayType TEN  = new HourOfDayType("10", "10");
    public static final HourOfDayType ELEVEN  = new HourOfDayType("11", "11");
    public static final HourOfDayType TWELVE  = new HourOfDayType("12", "12");
    public static final HourOfDayType THIRTEEN  = new HourOfDayType("13", "13");
    public static final HourOfDayType FOURTEEN  = new HourOfDayType("14", "14");
    public static final HourOfDayType FIFTEEN  = new HourOfDayType("15", "15");
    public static final HourOfDayType SIXTEEN  = new HourOfDayType("16", "16");
    public static final HourOfDayType SEVENTEEN  = new HourOfDayType("17", "17");
    public static final HourOfDayType EIGHTEEN  = new HourOfDayType("18", "18");
    public static final HourOfDayType NINETEEN  = new HourOfDayType("19", "19");
    public static final HourOfDayType TWENTY  = new HourOfDayType("20", "20");
    public static final HourOfDayType TWENTYONE  = new HourOfDayType("21", "21");
    public static final HourOfDayType TWNETYTWO  = new HourOfDayType("22", "22");
    public static final HourOfDayType TWENTYTHREE  = new HourOfDayType("23", "23");
    
    public static HourOfDayType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;

    public HourOfDayType() {
        //do nothing
    }

    public HourOfDayType(final String type, final String friendlyType) {
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
        HourOfDayType other = (HourOfDayType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
}
