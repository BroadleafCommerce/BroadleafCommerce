package org.broadleafcommerce.openadmin.server.dao.provider.property;

import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.ForeignKey;
import org.broadleafcommerce.openadmin.client.dto.MergedPropertyType;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.hibernate.mapping.Property;
import org.hibernate.type.Type;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * @author Jeff Fischer
 */
@Component("blMapPropertyProvider")
@Scope("prototype")
public class MapPropertyProvider extends AdvancedPropertyProvider {

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

        //do nothing but add the property without manipulation
        fields.put(propertyName, presentationAttribute);
    }
}
