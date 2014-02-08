/*
 * #%L
 * BroadleafCommerce Custom Field
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
 * %%
 * NOTICE:  All information contained herein is, and remains
 * the property of Broadleaf Commerce, LLC
 * The intellectual and technical concepts contained
 * herein are proprietary to Broadleaf Commerce, LLC
 * and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Broadleaf Commerce, LLC.
 * #L%
 */

package com.broadleafcommerce.customfield.service.type;

import org.broadleafcommerce.common.BroadleafEnumerationType;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * An extensible enumeration for a Custom Field Type.
 * Represents the possible custom field types available to admin users.
 *
 * @author Jeff Fischer
 *
 */
public class CustomFieldType implements Serializable, BroadleafEnumerationType {

    private static final long serialVersionUID = 1L;

    private static final Map<String, CustomFieldType> TYPES = new HashMap<String, CustomFieldType>();

    //standard persistence field types, which can be persisted as custom fields in an attribute map
    public static final CustomFieldType BOOLEAN = new CustomFieldType("BOOLEAN", "Boolean");
    public static final CustomFieldType INTEGER = new CustomFieldType("INTEGER", "Integer");
    public static final CustomFieldType MONEY = new CustomFieldType("MONEY", "Money");
    public static final CustomFieldType DECIMAL = new CustomFieldType("DECIMAL", "Decimal");
    public static final CustomFieldType DATE = new CustomFieldType("DATE", "Date");
    public static final CustomFieldType STRING = new CustomFieldType("STRING", "Text");
    public static final CustomFieldType HTML = new CustomFieldType("HTML", "Rich Text (HTML)");

    //advanced usage - not supported for persistence, and therefore cannot be a form field.
    //however, can be used in the context of a rule builder when the data to test is not coming from
    //an entity value (e.g. testing a dto submitted as part of a service call)
    public static final CustomFieldType STRING_LIST = new CustomFieldType("STRING_LIST", "List of Text Items");

    public static CustomFieldType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;

    public CustomFieldType() {
        //do nothing
    }

    public CustomFieldType(final String type, final String friendlyType) {
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
        CustomFieldType other = (CustomFieldType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
}
