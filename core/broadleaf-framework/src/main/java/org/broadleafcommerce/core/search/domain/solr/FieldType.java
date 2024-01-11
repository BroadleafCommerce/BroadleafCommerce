/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2024 Broadleaf Commerce
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
package org.broadleafcommerce.core.search.domain.solr;

import org.apache.commons.lang3.ArrayUtils;
import org.broadleafcommerce.common.BroadleafEnumerationType;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An extensible enumeration of entities that are used for searching and reporting
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class FieldType implements Serializable, BroadleafEnumerationType {

    private static final long serialVersionUID = 1L;

    private static final Map<String, FieldType> TYPES = new LinkedHashMap<String, FieldType>();

    public static final FieldType ID = new FieldType("id", "ID");
    public static final FieldType CATEGORY = new FieldType("category", "Category");
    
    public static final FieldType INT = new FieldType("i", "Integer");
    public static final FieldType INTS = new FieldType("is", "Integer (Multi)");
    public static final FieldType STRING = new FieldType("s", "String");
    public static final FieldType STRINGS = new FieldType("ss", "String (Multi)");
    public static final FieldType LONG = new FieldType("l", "Long");
    public static final FieldType LONGS = new FieldType("ls", "Long (Multi)");
    public static final FieldType TEXT = new FieldType("t", "Text");
    public static final FieldType TEXTS = new FieldType("txt", "Text (Multi)");
    public static final FieldType BOOLEAN = new FieldType("b", "Boolean");
    public static final FieldType BOOLEANS = new FieldType("bs", "Boolean (Multi)");
    public static final FieldType DOUBLE = new FieldType("d", "Double");
    public static final FieldType DOUBLES = new FieldType("ds", "Double (Multi)");
    public static final FieldType PRICE = new FieldType("p", "Price");
    public static final FieldType DATE = new FieldType("dt", "Date");
    public static final FieldType DATES = new FieldType("dts", "Date (Multi)");
    public static final FieldType TRIEINT = new FieldType("ti", "Trie Integer");
    public static final FieldType TRIELONG = new FieldType("tl", "Trie Long");
    public static final FieldType TRIEDOUBLE = new FieldType("td", "Trie Double");
    public static final FieldType TRIEDATE = new FieldType("tdt", "Trie Date");
    public static final FieldType COORDINATE = new FieldType("c", "Coordinate");
    public static final FieldType SORT = new FieldType("sort", "SORT");
    
    public static boolean isMultiValued(FieldType type) {
        return ArrayUtils.contains(new FieldType[] {
            INTS,
            STRINGS,
            LONGS,
            TEXTS,
            BOOLEANS,
            DOUBLES,
            DATES
        }, type);
    }

    public static FieldType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;

    public FieldType() {
        //do nothing
    }

    public FieldType(final String type, final String friendlyType) {
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
        if (!getClass().isAssignableFrom(obj.getClass()))
            return false;
        FieldType other = (FieldType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
}
