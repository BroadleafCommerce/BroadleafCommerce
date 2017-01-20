/*
 * #%L
 * BroadleafCommerce Export Module
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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Enum used for security purposes when determining if a user has the correct permissions to down load a shareable export
 * When implementing an export for a domain a new enum must be made for that domain otherwise any user with privileges
 * to view the export download page will have the ability to download that domain's exports
 * 
 * @author Jay Aisenbrey (cja769)
 *
 */
public class ExportEntityType implements Serializable, BroadleafEnumerationType {

    private static final long serialVersionUID = 1L;

    private static final Map<String, ExportEntityType> TYPES = new LinkedHashMap<String, ExportEntityType>();
    
    public static ExportEntityType getInstance(final String type) {
        return TYPES.get(type);
    }
    
    public static Set<String> keySet() {
        return TYPES.keySet();
    }
    
    private String type;
    private String friendlyType;
    private String ceilingEntity;
    
    public ExportEntityType() {
        //do nothing
    }

    public ExportEntityType(final String type, final String friendlyType, String ceilingEntity) {
        this.friendlyType = friendlyType;
        setType(type);
        setCeilingEntity(ceilingEntity);
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
    
    public String getCeilingEntity() {
        return ceilingEntity;
    }
    
    public void setCeilingEntity(String ceilingEntity) {
        this.ceilingEntity = ceilingEntity;
    }

}
