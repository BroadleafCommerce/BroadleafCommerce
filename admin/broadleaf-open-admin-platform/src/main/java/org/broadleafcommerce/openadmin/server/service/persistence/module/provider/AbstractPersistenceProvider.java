package org.broadleafcommerce.openadmin.server.service.persistence.module.provider;

import org.apache.commons.lang.ArrayUtils;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldManager;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

/**
 * @author Jeff Fischer
 */
public abstract class AbstractPersistenceProvider implements PersistenceProvider {

    protected Class<?> getListFieldType(Serializable instance, FieldManager fieldManager, Property property, PersistenceManager persistenceManager) {
        Class<?> returnType = null;
        Field field = fieldManager.getField(instance.getClass(), property.getName());
        java.lang.reflect.Type type = field.getGenericType();
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            Class<?> clazz = (Class<?>) pType.getActualTypeArguments()[0];
            Class<?>[] entities = persistenceManager.getDynamicEntityDao().getAllPolymorphicEntitiesFromCeiling(clazz);
            if (!ArrayUtils.isEmpty(entities)) {
                returnType = entities[entities.length-1];
            }
        }
        return returnType;
    }
}
