package org.broadleafcommerce.openadmin.server.dao.provider.property;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.common.presentation.AdminPresentationCollection;
import org.broadleafcommerce.common.presentation.AdminPresentationMap;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.openadmin.client.dto.CollectionMetadata;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.ForeignKey;
import org.broadleafcommerce.openadmin.client.dto.MergedPropertyType;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.hibernate.mapping.Property;
import org.hibernate.type.Type;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

/**
 * @author Jeff Fischer
 */
@Component("blAdvancedCollectionPropertyProvider")
@Scope("prototype")
public class AdvancedPropertyProvider extends AbstractPropertyProvider {

    public boolean canHandleField(Field field) {
        AdminPresentationMap map = field.getAnnotation(AdminPresentationMap.class);
        AdminPresentationCollection collection = field.getAnnotation(AdminPresentationCollection.class);
        return map != null || collection != null;
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
        CollectionMetadata fieldMetadata = (CollectionMetadata) presentationAttribute;
        if (StringUtils.isEmpty(fieldMetadata.getCollectionCeilingEntity())) {
            ParameterizedType listType = (ParameterizedType) field.getGenericType();
            Class<?> listClass = (Class<?>) listType.getActualTypeArguments()[0];
            fieldMetadata.setCollectionCeilingEntity(listClass.getName());
        }
        if (targetClass != null) {
            if (StringUtils.isEmpty(fieldMetadata.getInheritedFromType())) {
                fieldMetadata.setInheritedFromType(targetClass.getName());
            }
            if (ArrayUtils.isEmpty(fieldMetadata.getAvailableToTypes())) {
                fieldMetadata.setAvailableToTypes(new String[]{targetClass.getName()});
            }
        }
        fields.put(propertyName, fieldMetadata);
    }

}
