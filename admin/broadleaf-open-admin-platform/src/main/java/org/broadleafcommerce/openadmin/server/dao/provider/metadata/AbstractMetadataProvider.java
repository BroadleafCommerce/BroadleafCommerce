/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.server.dao.provider.metadata;

import org.broadleafcommerce.openadmin.dto.override.MetadataOverride;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;

import java.util.Map;

import javax.annotation.Resource;

/**
 * @author Chris Kittrell (ckittrell)
 */
public abstract class AbstractMetadataProvider {

    protected Map<String, Map<String, MetadataOverride>> metadataOverrides;

    @Resource(name="blMetadataOverrides")
    public void setMetadataOverrides(Map metadataOverrides) {
        try {
            this.metadataOverrides = metadataOverrides;
        } catch (Throwable e) {
            throw new IllegalArgumentException(
                    "Unable to assign metadataOverrides. You are likely using an obsolete spring application context " +
                    "configuration for this value. Please utilize the xmlns:mo=\"http://schema.broadleafcommerce.org/mo\" namespace " +
                    "and http://schema.broadleafcommerce.org/mo http://schema.broadleafcommerce.org/mo/mo.xsd schemaLocation " +
                    "in the xml schema config for your app context. This will allow you to use the appropriate <mo:override> element to configure your overrides.", e);
        }
    }

    protected Map<String, MetadataOverride> getTargetedOverride(DynamicEntityDao dynamicEntityDao, String configurationKey, String ceilingEntityFullyQualifiedClassname) {
        if (metadataOverrides != null && (configurationKey != null || ceilingEntityFullyQualifiedClassname != null)) {
            if (metadataOverrides.containsKey(configurationKey)) {
                return metadataOverrides.get(configurationKey);
            }
            if (metadataOverrides.containsKey(ceilingEntityFullyQualifiedClassname)) {
                return metadataOverrides.get(ceilingEntityFullyQualifiedClassname);
            }
            Class<?> test;
            try {
                test = Class.forName(ceilingEntityFullyQualifiedClassname);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            if (test.isInterface()) {
                //if it's an interface, get the least derive polymorphic concrete implementation
                Class<?>[] types = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(test);
                return metadataOverrides.get(types[types.length-1].getName());
            } else {
                //if it's a concrete implementation, try the interfaces
                Class<?>[] types = test.getInterfaces();
                for (Class<?> type : types) {
                    if (metadataOverrides.containsKey(type.getName())) {
                        return metadataOverrides.get(type.getName());
                    }
                }
            }
        }
        return null;
    }
}
