/*-
 * #%L
 * BroadleafCommerce CMS Module
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.cms.field.type;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Supported field types within the Broadleaf CMS admin.
 *
 * @author bpolster
 */
public class FieldType implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private static final Map<String, FieldType> TYPES = new HashMap<>();

    public static final FieldType BOOLEAN = new FieldType("BOOLEAN", "Boolean");
    public static final FieldType DATE = new FieldType("DATE", "Date");
    public static final FieldType TIME = new FieldType("TIME", "Time");
    public static final FieldType INTEGER = new FieldType("INTEGER", "Integer");
    public static final FieldType DECIMAL = new FieldType("DECIMAL", "Decimal");
    public static final FieldType STRING = new FieldType("STRING", "String");
    public static final FieldType RICH_TEXT = new FieldType("RICH_TEXT", "Rich Text");
    public static final FieldType HTML = new FieldType("HTML", "HTML");
    public static final FieldType ENUMERATION = new FieldType("ENUMERATION", "Enumeration");

    private String type;
    private String friendlyType;

    public FieldType() {
        //do nothing
    }

    public FieldType(final String type, final String friendlyType) {
        this.friendlyType = friendlyType;
        setType(type);
    }

    public static FieldType getInstance(final String type) {
        return TYPES.get(type);
    }

    public String getType() {
        return type;
    }

    protected void setType(final String type) {
        this.type = type;
        if (!TYPES.containsKey(type)) {
            TYPES.put(type, this);
        }
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
        if (!getClass().isAssignableFrom(obj.getClass()))
            return false;
        FieldType other = (FieldType) obj;
        if (type == null) {
            return other.type == null;
        } else
            return type.equals(other.type);
    }

}
