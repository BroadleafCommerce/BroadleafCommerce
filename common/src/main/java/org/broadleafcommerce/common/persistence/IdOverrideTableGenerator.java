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
package org.broadleafcommerce.common.persistence;

import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.enhanced.TableGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import jakarta.persistence.Id;

/**
 * @author Jeff Fischer
 */
public class IdOverrideTableGenerator extends TableGenerator {

    public static final String ENTITY_NAME_PARAM = "entity_name";

    public static final String DEFAULT_TABLE_NAME = "SEQUENCE_GENERATOR";
    public static final String DEFAULT_SEGMENT_COLUMN_NAME = "ID_NAME";
    public static final String DEFAULT_VALUE_COLUMN_NAME = "ID_VAL";
    public static final int DEFAULT_INCREMENT_SIZE = 50;

    private static final Map<String, Field> FIELD_CACHE = Collections.synchronizedMap(new HashMap<>());

    private String entityName;

    protected Field getIdField(Class<?> clazz) {
        Field response = null;
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getAnnotation(Id.class) != null) {
                response = field;
                break;
            }
        }
        if (response == null && clazz.getSuperclass() != null) {
            response = getIdField(clazz.getSuperclass());
        }

        return response;
    }

    @Override
    public Serializable generate(final SharedSessionContractImplementor session, final Object obj) {
        /*
        This works around an issue in Hibernate where if the entityPersister is retrieved
        from the session and used to get the Id, the entity configuration can be recycled,
        which is messing with the load persister and current persister on some collections.
        This may be a jrebel thing, but this workaround covers all environments
         */
        String objName = obj.getClass().getName();
        if (!FIELD_CACHE.containsKey(objName)) {
            Field field = getIdField(obj.getClass());
            if (field == null) {
                throw new IllegalArgumentException("Cannot specify IdOverrideTableGenerator for an entity (" + objName
                        + ") that does not have an Id field declared using the @Id annotation.");
            }
            field.setAccessible(true);
            FIELD_CACHE.put(objName, field);
        }
        Field field = FIELD_CACHE.get(objName);
        final Serializable id;
        try {
            id = (Serializable) field.get(obj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        if (id != null) {
            return id;
        }
        return (Serializable) super.generate(session, obj);
    }

    @Override
    public void configure(Type type, Properties params, ServiceRegistry registry) throws MappingException {
        params.putIfAbsent("table_name", "SEQUENCE_GENERATOR");
        params.putIfAbsent("segment_column_name", DEFAULT_SEGMENT_COLUMN_NAME);
        params.putIfAbsent("value_column_name", DEFAULT_VALUE_COLUMN_NAME);
        params.putIfAbsent("increment_size", DEFAULT_INCREMENT_SIZE);
        super.configure(type, params, registry);
        entityName = (String) params.get(ENTITY_NAME_PARAM);
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

}
