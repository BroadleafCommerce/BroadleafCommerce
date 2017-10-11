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
package org.broadleafcommerce.common.util.dao;

import org.hibernate.boot.SessionFactoryBuilder;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.boot.spi.SessionFactoryBuilderFactory;
import org.hibernate.boot.spi.SessionFactoryBuilderImplementor;
import org.hibernate.jpa.boot.internal.StandardJpaScanEnvironmentImpl;
import org.hibernate.mapping.PersistentClass;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Jeff Fischer
 */
public class MappingProvider implements SessionFactoryBuilderFactory {

    private static final Map<String, MetadataImplementor> metadataMap = new ConcurrentHashMap<>();

    @Override
    public SessionFactoryBuilder getSessionFactoryBuilder(MetadataImplementor metadata, SessionFactoryBuilderImplementor defaultBuilder) {
        List<String> classes = metadata.getMetadataBuildingOptions().getScanEnvironment().getExplicitlyListedClassNames();
        for (String clazz : classes) {
            if (!metadataMap.containsKey(clazz)) {
                metadataMap.put(clazz, metadata);
            }
        }
        return defaultBuilder;
    }

    public static PersistentClass getMapping(String entityClass) {
        MetadataImplementor implementor = metadataMap.get(entityClass);
        PersistentClass response = null;
        if (implementor != null) {
            response = implementor.getEntityBinding(entityClass);
        }
        return response;
    }

}
