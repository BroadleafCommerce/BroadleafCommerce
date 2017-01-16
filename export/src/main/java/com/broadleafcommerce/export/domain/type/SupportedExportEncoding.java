/*
 * #%L
 * BroadleafCommerce Customer Segments
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
package com.broadleafcommerce.export.domain.type;

import org.broadleafcommerce.common.BroadleafEnumerationType;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class SupportedExportEncoding implements Serializable, BroadleafEnumerationType {
    
    private static final long serialVersionUID = 1L;

    private static final Map<String, SupportedExportEncoding> TYPES = new LinkedHashMap<String, SupportedExportEncoding>();
    
    public static final SupportedExportEncoding UTF8 = new SupportedExportEncoding("UTF8", "UTF-8");
    
    public static SupportedExportEncoding getInstance(final String type) {
        return TYPES.get(type);
    }
    
    public static Set<SupportedExportEncoding> getTypes() {
        Set<SupportedExportEncoding> types = new HashSet<>();
        for (String type : TYPES.keySet()) {
            types.add(TYPES.get(type));
        }
        return types;
    }
    
    private String type;
    private String friendlyType;
    
    public SupportedExportEncoding() {
        //do nothing
    }

    public SupportedExportEncoding(final String type, final String friendlyType) {
        this.friendlyType = friendlyType;
        setType(type);
    }
    
    @Override
    public String getType() {
        return type;
    }
    
    public void setType(final String type) {
        this.type = type;
        if (!TYPES.containsKey(type)) {
            TYPES.put(type, this);
        }
    }

    @Override
    public String getFriendlyType() {
        return friendlyType;
    }
}
