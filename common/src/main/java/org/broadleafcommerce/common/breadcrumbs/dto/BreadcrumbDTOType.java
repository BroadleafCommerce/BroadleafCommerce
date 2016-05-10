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

package org.broadleafcommerce.common.breadcrumbs.dto;

import org.broadleafcommerce.common.BroadleafEnumerationType;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An extensible enumeration of breadcrumb types.   Useful in the view layer to vary the presentation
 * based on the Breadcrumb type.
 * 
 * @author bpolster 
 *
 */
public class BreadcrumbDTOType implements Serializable, BroadleafEnumerationType {

    private static final long serialVersionUID = 1L;

    private static final Map<String, BreadcrumbDTOType> TYPES = new LinkedHashMap<String, BreadcrumbDTOType>();

    public static final BreadcrumbDTOType HOME = new BreadcrumbDTOType("HOME", "Home");
    public static final BreadcrumbDTOType CATEGORY = new BreadcrumbDTOType("CATEGORY", "Category");
    public static final BreadcrumbDTOType PRODUCT = new BreadcrumbDTOType("PRODUCT", "Product");
    public static final BreadcrumbDTOType SEARCH = new BreadcrumbDTOType("SEARCH", "Search");
    public static final BreadcrumbDTOType PAGE = new BreadcrumbDTOType("PAGE", "Page");

    public static BreadcrumbDTOType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;

    public BreadcrumbDTOType() {
        //do nothing
    }

    public BreadcrumbDTOType(final String type, final String friendlyType) {
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
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!getClass().isAssignableFrom(obj.getClass())) {
            return false;
        }

        BreadcrumbDTOType other = (BreadcrumbDTOType) obj;
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        return true;
    }

}
