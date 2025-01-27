/*-
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.util.dao;

import org.hibernate.boot.SessionFactoryBuilder;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.boot.spi.SessionFactoryBuilderFactory;
import org.hibernate.boot.spi.SessionFactoryBuilderImplementor;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.type.Type;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.annotation.Nullable;

/**
 * <p>
 * Hibernate SPI implementor that harvests metadata about all of the entity classes
 *
 * <p>
 * This is registered within META-INF/services/org.hibernate.boot.spi.SessionFactoryBuilderFactory and listens
 * to the session factory being created with all of the metadata
 *
 * @author Jeff Fischer
 * @author Phillip Verheyden (phillipuniverse)
 */
public class HibernateMappingProvider implements SessionFactoryBuilderFactory {

    private static final Map<String, PersistentClass> metadataMap = new ConcurrentHashMap<>();

    public HibernateMappingProvider() {
        // empty constructor for normal operation
    }

    /**
     * Initialize with seed data independent of {@link #getSessionFactoryBuilder(MetadataImplementor, SessionFactoryBuilderImplementor)}
     *
     * @param metadataMap seed data
     */
    public HibernateMappingProvider(Map<String, PersistentClass> metadataMap) {
        HibernateMappingProvider.metadataMap.putAll(metadataMap);
    }

    /**
     * Returns the underlying Hibernate metadata about a given entity class across all available persistence units
     *
     * @param entityClass FQN of a Hibernate entity
     * @return the Hibernate metadata for that class, or null if there is no mapping
     */
    @Nullable
    public static PersistentClass getMapping(String entityClass) {
        return metadataMap.get(entityClass);
    }

    /**
     * Retrieve the names of all of the Hibernate mapped properties for the given <b>entityClass</b>
     *
     * @param entityClass mapped class
     * @return all property names or empty list if the class is not mapped by Hibernate or has no properties
     * @see #getPropertyTypes(String)
     */
    @NonNull
    public static List<String> getPropertyNames(String entityClass) {
        List<String> propertyNames = new ArrayList<>();
        PersistentClass metadata = getMapping(entityClass);
        if (metadata == null) {
            return propertyNames;
        }
        List<Property> properties = metadata.getPropertyClosure();
        for (Property property : properties) {
            propertyNames.add(property.getName());
        }
        return propertyNames;
    }

    /**
     * Retrieve all of the types of all of the Hibernate mapped properties for the given <b>entityClass</b>
     *
     * @param entityClass mapped class
     * @return all property types or empty list if the class is not mapped by Hibernate or has no properties
     * @see #getPropertyNames(String)
     */
    @NonNull
    public static List<Type> getPropertyTypes(String entityClass) {
        List<Type> propertyTypes = new ArrayList<>();
        PersistentClass metadata = getMapping(entityClass);
        if (metadata == null) {
            return propertyTypes;
        }
        List<Property> properties = metadata.getPropertyClosure();
        for (Property prop : properties) {
            propertyTypes.add(prop.getType());
        }
        return propertyTypes;
    }

    /**
     * Retrieves all Hibernate metadata for all entities
     *
     * @return all of the tracked {@link PersistentClass} across all registered persistence units
     */
    @NonNull
    public static Collection<PersistentClass> getAllMappings() {
        return metadataMap.values();
    }

    @Override
    public SessionFactoryBuilder getSessionFactoryBuilder(
            MetadataImplementor metadata,
            SessionFactoryBuilderImplementor defaultBuilder
    ) {
        // This aggregates all of the metadata for all persistence untis. No need to discriminate by persistence unit since the map is
        // keyed by class name
        Collection<PersistentClass> classes = metadata.getEntityBindings();
        classes.forEach((clazz) -> {
            if (clazz != null && clazz.getClassName() != null) {
                metadataMap.put(clazz.getClassName(), clazz);
            }
        });
        return defaultBuilder;
    }

}
