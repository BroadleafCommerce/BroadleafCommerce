/*-
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * Utility class that will search for the entity identifier property and/or property value 
 * @author dcolgrove
 *
 */
public class EntityIdentifierUtil {
    protected static final Log LOG = LogFactory.getLog(EntityIdentifierUtil.class);

    
    /**
     * Determine the field name with the @id annotation (javax.persistence.Id)
     * 
     * @param entity Object reference of the entity/bean where the property is to be searched
     * @return String representing the field name or null if the entity does not have an @Id annotation
     */
    public String getIdentifierFieldName(Object entity) {
        return getIdentifierFieldName(entity.getClass());
    }

    /**
     * Determine the field name with the @id annotation (javax.persistence.Id)
     * 
     * @param clazz Class reference of the entity/bean where the property is to be searched
     * @return String representing the field name or null if the entity does not have an @Id annotation
     */
    public String getIdentifierFieldName(Class<?> clazz) {
        Field field = findIdentifierField(clazz);
        if (field != null) {
            return field.getName();
        }
        return null;
    }

   
    /**
     * Given the entity, finds the identifier property and returns the associated value
     * 
     * @param entity Object reference of the entity/bean where the property is to be searched
     * @return a Serializable instance of the value
     */
    public Serializable getIdentifierFieldValue(Object entity) {
        String pkFieldName = getIdentifierFieldName(entity.getClass());
        if (pkFieldName != null) {
            return getIdentifyFieldValue(entity, pkFieldName);
        }
        return null;
    }

    /**
     * Given the entity and specific field name, returns the value of that field.
     * 
     * @param entity Object reference of the entity/bean where the property is to be searched
     * @param primaryKeyFieldName the name of the field where the value will be taken
     * @return a Serializable instance of the value
     */
    public Serializable getIdentifyFieldValue(Object entity, String primaryKeyFieldName) {
        Field field = findIdentifierField(entity.getClass());
        if (field != null) {
            try {
                field.setAccessible(true);
                Object fieldValue = field.get(entity);
                if (fieldValue != null ) {
                    return (Serializable) fieldValue;
                }
            } catch (Exception e) {
                LOG.warn(String.format("Could not find primaryKeyFieldValue for entity %s", entity.getClass().toString()));
            }
        }
        return null;
    }

    /**
     * Determine the Reflection Field with the @id annotation (javax.persistence.Id)
     * This assumes the @Id annotation will be on the property
     * 
     * @param entity Object reference of the entity/bean where the property is to be searched
     * @return String representing the field name or null if the entity does not have an @Id annotation
     */
    protected Field findIdentifierField(Class<?> clazz) {
        Field fields[] = clazz.getDeclaredFields();
        for( Field field : fields ) {
            Annotation annotations[] = field.getDeclaredAnnotations();
            for( Annotation annotation : annotations ) {
                if( annotation instanceof javax.persistence.Id )
                    return field;
            }
        }
        Class<?> superClass = clazz.getSuperclass();
        if (!superClass.equals(Object.class)) {
            return findIdentifierField(superClass);
        }
        return null; //no field found 
    }
}
