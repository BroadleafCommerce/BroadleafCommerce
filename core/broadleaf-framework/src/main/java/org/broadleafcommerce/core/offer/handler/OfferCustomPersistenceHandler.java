/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.core.offer.handler;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.presentation.client.OperationType;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferAdminPresentation;
import org.broadleafcommerce.openadmin.dto.*;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.InspectHelper;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jon on 11/23/15.
 */
@Component("blOfferCustomPersistenceHandler")
public class OfferCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {

    private static final Log LOG = LogFactory.getLog(OfferCustomPersistenceHandler.class);

    private Boolean isAssignableFromOffer(PersistencePackage persistencePackage) {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        return Offer.class.getName().equals(ceilingEntityFullyQualifiedClassname);
    }

    @Override
    public Boolean canHandleInspect(PersistencePackage persistencePackage) {
       return isAssignableFromOffer(persistencePackage);
    }

    @Override
    public Boolean canHandleFetch(PersistencePackage persistencePackage) {
        return isAssignableFromOffer(persistencePackage);
    }

    @Override
    public DynamicResultSet inspect(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, InspectHelper helper) throws ServiceException {
        Map<String, FieldMetadata> md = getMetadata(persistencePackage, helper);

        PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
        Map<MergedPropertyType, Map<String, FieldMetadata>> allMergedProperties = new HashMap<MergedPropertyType, Map<String, FieldMetadata>>();

        //retrieve the default properties for WorkflowEvents
        Map<String, FieldMetadata> properties = helper.getSimpleMergedProperties(Offer.class.getName(), persistencePerspective);

        BasicFieldMetadata advancedLabelMetadata = new BasicFieldMetadata();
        advancedLabelMetadata.setFieldType(SupportedFieldType.BOOLEAN_LINK);
        advancedLabelMetadata.setForeignKeyCollection(false);
        advancedLabelMetadata.setMergedPropertyType(MergedPropertyType.PRIMARY);
        advancedLabelMetadata.setName("test");
        advancedLabelMetadata.setFriendlyName("OfferImpl_View_Visibility_Options");
        advancedLabelMetadata.setGroup(OfferAdminPresentation.GroupName.ActivityRange);
        advancedLabelMetadata.setOrder(5000);
        advancedLabelMetadata.setDefaultValue("true");
        properties.put("showAdvancedVisibilityOptions", advancedLabelMetadata);

        allMergedProperties.put(MergedPropertyType.PRIMARY, properties);
        Class<?>[] entityClasses = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(Offer.class);
        ClassMetadata mergedMetadata = helper.buildClassMetadata(entityClasses, persistencePackage, allMergedProperties);

        return new DynamicResultSet(mergedMetadata, null, null);
    }

    @Override
    public DynamicResultSet fetch(PersistencePackage persistencePackage, CriteriaTransferObject cto, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        DynamicResultSet resultSet = helper.getCompatibleModule(OperationType.BASIC).fetch(persistencePackage, cto);
        String customCriteria = "";
        if (persistencePackage.getCustomCriteria().length > 0) {
            customCriteria = persistencePackage.getCustomCriteria()[0];
        }

        for (Entity entity : resultSet.getRecords()) {
            Property discountType = entity.findProperty("discountType");
            Property discountValue = entity.findProperty("value");

            String value = discountValue.getValue();
            if (discountType.getValue().equals("PERCENT_OFF")) {
                value = value.indexOf(".") < 0 ? value : value.replaceAll("0*$", "").replaceAll("\\.$", "");
                discountValue.setValue(value + "%");
            } else if (discountType.getValue().equals("AMOUNT_OFF")) {
                NumberFormat nf = NumberFormat.getCurrencyInstance();
                discountValue.setValue(nf.format(new BigDecimal(value)));
            } else if (discountType.getValue().equals("")) {
                discountValue.setValue("");
            }

            Property timeRule = entity.findProperty("offerMatchRules---TIME");

            Property advancedLabel = new Property();
            advancedLabel.setName("showAdvancedVisibilityOptions");
            advancedLabel.setValue((timeRule.getValue() == null) ? "true" : "false");
            entity.addProperty(advancedLabel);

            if (!customCriteria.equals("listGridView")) {
                String setValue = discountValue.getValue();
                setValue = setValue.replaceAll("\\%", "").replaceAll("\\$", "");
                discountValue.setValue(setValue);
            }
        }
        return resultSet;
    }
}
