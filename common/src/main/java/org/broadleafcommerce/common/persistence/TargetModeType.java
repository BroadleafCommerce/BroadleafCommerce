/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
package org.broadleafcommerce.common.persistence;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * An extendible enumeration of target mode types.
 * 
 * @author jfischer
 */
public class TargetModeType implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Map<String, TargetModeType> TYPES = new HashMap<String, TargetModeType>();

    public static final TargetModeType SANDBOX  = new TargetModeType("sandbox", "entity manager target for the sandbox datasource");
    public static final TargetModeType STAGE  = new TargetModeType("stage", "entity manager target for the stage datasource");
    public static final TargetModeType PRODUCTION  = new TargetModeType("production", "entity manager target for the production datasource");

    public static TargetModeType getInstance(final String type) {
        return TYPES.get(type);
    }

    public static Map<String, TargetModeType> getTypes() {
        return TYPES;
    }

    private String type;
    private String friendlyType;

    public TargetModeType() {
        //do nothing
    }

    public TargetModeType(final String type, final String friendlyType) {
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
        TargetModeType other = (TargetModeType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
}
