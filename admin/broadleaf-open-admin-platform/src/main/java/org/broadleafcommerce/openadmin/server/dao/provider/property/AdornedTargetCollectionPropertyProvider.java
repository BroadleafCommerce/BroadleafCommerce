/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
