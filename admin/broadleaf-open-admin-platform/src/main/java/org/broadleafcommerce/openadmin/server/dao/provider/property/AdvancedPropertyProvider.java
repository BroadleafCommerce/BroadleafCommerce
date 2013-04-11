package org.broadleafcommerce.openadmin.server.dao.provider.property;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.common.presentation.AdminPresentationCollection;
import org.broadleafcommerce.common.presentation.AdminPresentationMap;
import org.broadleafcommerce.openadmin.client.dto.CollectionMetadata;
import org.broadleafcommerce.openadmin.server.dao.provider.property.request.PropertyRequest;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

/**
 * @author Jeff Fischer
 */
@Component("blAdvancedCollectionPropertyProvider")
@Scope("prototype")
public class AdvancedPropertyProvider extends PropertyProviderAdapter {

    public boolean canHandleField(Field field) {
        AdminPresentationMap map = field.getAnnotation(AdminPresentationMap.class);
        AdminPresentationCollection collection = field.getAnnotation(AdminPresentationCollection.class);
        return map != null || collection != null;
    }

    @Override
    public void buildProperty(PropertyRequest propertyRequest) {
        CollectionMetadata fieldMetadata = (CollectionMetadata) propertyRequest.getPresentationAttribute();
        if (StringUtils.isEmpty(fieldMetadata.getCollectionCeilingEntity())) {
            ParameterizedType listType = (ParameterizedType) propertyRequest.getRequestedField().getGenericType();
            Class<?> listClass = (Class<?>) listType.getActualTypeArguments()[0];
            fieldMetadata.setCollectionCeilingEntity(listClass.getName());
        }
        if (propertyRequest.getTargetClass() != null) {
            if (StringUtils.isEmpty(fieldMetadata.getInheritedFromType())) {
                fieldMetadata.setInheritedFromType(propertyRequest.getTargetClass().getName());
            }
            if (ArrayUtils.isEmpty(fieldMetadata.getAvailableToTypes())) {
                fieldMetadata.setAvailableToTypes(new String[]{propertyRequest.getTargetClass().getName()});
            }
        }
        propertyRequest.getRequestedProperties().put(propertyRequest.getRequestedPropertyName(), fieldMetadata);
    }

}
