package org.broadleafcommerce.openadmin.server.dao.provider.property;

import org.apache.commons.lang.ArrayUtils;
import org.broadleafcommerce.common.presentation.AdminPresentationMapFields;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.openadmin.client.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.ForeignKey;
import org.broadleafcommerce.openadmin.client.dto.MergedPropertyType;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldManager;
import org.hibernate.internal.TypeLocatorImpl;
import org.hibernate.mapping.Property;
import org.hibernate.type.Type;
import org.hibernate.type.TypeResolver;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

/**
 * @author Jeff Fischer
 */
@Component("blMapFieldPropertyProvider")
@Scope("prototype")
public class MapFieldPropertyProvider extends BasicPropertyProvider {

    @Override
    public boolean canHandleField(Field field) {
        AdminPresentationMapFields mapFields = field.getAnnotation(AdminPresentationMapFields.class);
        return mapFields != null;
    }

    @Override
    public void buildProperty(
            Field field,
            Class<?> targetClass,
            ForeignKey foreignField,
            ForeignKey[] additionalForeignFields,
            MergedPropertyType mergedPropertyType,
            List<Property> componentProperties,
            Map<String, FieldMetadata> fields,
            String idProperty,
            String prefix,
            String propertyName,
            Type type,
            boolean isPropertyForeignKey,
            int additionalForeignKeyIndexPosition,
            Map<String, FieldMetadata> presentationAttributes,
            FieldMetadata presentationAttribute,
            SupportedFieldType explicitType,
            Class<?> returnedClass,
            DynamicEntityDao dynamicEntityDao
    ) {
        for (Map.Entry<String, FieldMetadata> entry : presentationAttributes.entrySet()) {
            if (entry.getKey().startsWith(propertyName + FieldManager.MAPFIELDSEPARATOR)) {
                TypeLocatorImpl typeLocator = new TypeLocatorImpl(new TypeResolver());

                Type myType = null;
                //first, check if an explicit type was declared
                String valueClass = ((BasicFieldMetadata) entry.getValue()).getMapFieldValueClass();
                if (valueClass != null) {
                    myType = typeLocator.entity(valueClass);
                }
                if (myType == null) {
                    SupportedFieldType fieldType = ((BasicFieldMetadata) entry.getValue()).getExplicitFieldType();
                    Class<?> basicJavaType = getBasicJavaType(fieldType);
                    if (basicJavaType != null) {
                        myType = typeLocator.basic(basicJavaType);
                    }
                }
                if (myType == null) {
                    java.lang.reflect.Type genericType = field.getGenericType();
                    if (genericType instanceof ParameterizedType) {
                        ParameterizedType pType = (ParameterizedType) genericType;
                        Class<?> clazz = (Class<?>) pType.getActualTypeArguments()[1];
                        Class<?>[] entities = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(clazz);
                        if (!ArrayUtils.isEmpty(entities)) {
                            myType = typeLocator.entity(entities[entities.length-1]);
                        }
                    }
                }
                if (myType == null) {
                   throw new IllegalArgumentException("Unable to establish the type for the property (" + entry
                           .getKey() + ")");
                }
                //add property for this map field as if it was a normal field
                super.buildProperty(field, targetClass, foreignField, additionalForeignFields, mergedPropertyType, componentProperties,
                        fields, idProperty, prefix, entry.getKey(), myType, isPropertyForeignKey, additionalForeignKeyIndexPosition,
                        presentationAttributes, entry.getValue(), ((BasicFieldMetadata) entry.getValue()).getExplicitFieldType(), myType.getReturnedClass(), dynamicEntityDao);
            }
        }
    }

}
