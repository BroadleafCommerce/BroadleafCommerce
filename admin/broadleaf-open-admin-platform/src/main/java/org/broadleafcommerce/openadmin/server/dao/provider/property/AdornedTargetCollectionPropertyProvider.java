package org.broadleafcommerce.openadmin.server.dao.provider.property;

import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.common.presentation.AdminPresentationAdornedTargetCollection;
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.openadmin.client.dto.AdornedTargetCollectionMetadata;
import org.broadleafcommerce.openadmin.client.dto.AdornedTargetList;
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
@Component("blAdornedTargetPropertyProvider")
@Scope("prototype")
public class AdornedTargetCollectionPropertyProvider extends AdvancedPropertyProvider{

    @Override
    public boolean canHandleField(Field field) {
        AdminPresentationAdornedTargetCollection collection = field.getAnnotation(AdminPresentationAdornedTargetCollection.class);
        return collection != null;
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
        super.buildProperty(field, targetClass, foreignField, additionalForeignFields, mergedPropertyType, componentProperties,
                fields, idProperty, prefix, propertyName, type, isPropertyForeignKey, additionalForeignKeyIndexPosition,
                presentationAttributes, presentationAttribute, explicitType, returnedClass, dynamicEntityDao);
        //add additional adorned target support
        AdornedTargetCollectionMetadata fieldMetadata = (AdornedTargetCollectionMetadata) presentationAttribute;
        if (StringUtils.isEmpty(fieldMetadata.getCollectionCeilingEntity())) {
            fieldMetadata.setCollectionCeilingEntity(type.getReturnedClass().getName());
            AdornedTargetList targetList = ((AdornedTargetList) fieldMetadata.getPersistencePerspective().
                    getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.ADORNEDTARGETLIST));
            targetList.setAdornedTargetEntityClassname(fieldMetadata.getCollectionCeilingEntity());
        }
    }

}
