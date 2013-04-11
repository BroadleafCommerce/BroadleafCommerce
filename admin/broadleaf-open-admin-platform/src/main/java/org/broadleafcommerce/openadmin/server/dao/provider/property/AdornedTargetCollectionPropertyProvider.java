package org.broadleafcommerce.openadmin.server.dao.provider.property;

import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.common.presentation.AdminPresentationAdornedTargetCollection;
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
import org.broadleafcommerce.openadmin.client.dto.AdornedTargetCollectionMetadata;
import org.broadleafcommerce.openadmin.client.dto.AdornedTargetList;
import org.broadleafcommerce.openadmin.server.dao.provider.property.request.PropertyRequest;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

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
    public void buildProperty(PropertyRequest propertyRequest) {
        super.buildProperty(propertyRequest);
        //add additional adorned target support
        AdornedTargetCollectionMetadata fieldMetadata = (AdornedTargetCollectionMetadata) propertyRequest.getPresentationAttribute();
        if (StringUtils.isEmpty(fieldMetadata.getCollectionCeilingEntity())) {
            fieldMetadata.setCollectionCeilingEntity(propertyRequest.getType().getReturnedClass().getName());
            AdornedTargetList targetList = ((AdornedTargetList) fieldMetadata.getPersistencePerspective().
                    getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.ADORNEDTARGETLIST));
            targetList.setAdornedTargetEntityClassname(fieldMetadata.getCollectionCeilingEntity());
        }
    }

}
