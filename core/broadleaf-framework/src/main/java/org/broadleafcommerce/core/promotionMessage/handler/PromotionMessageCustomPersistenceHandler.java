/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
package org.broadleafcommerce.core.promotionMessage.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.presentation.client.OperationType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.promotionMessage.domain.PromotionMessage;
import org.broadleafcommerce.core.promotionMessage.domain.type.PromotionMessageType;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.dto.SectionCrumb;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.InspectHelper;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * @author Chris Kittrell (ckittrell)
 */
@Component("blPromotionMessageCustomPersistenceHandler")
public class PromotionMessageCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {

    private static final Log LOG = LogFactory.getLog(PromotionMessageCustomPersistenceHandler.class);

    @Override
    public Boolean canHandleInspect(PersistencePackage persistencePackage) {
        return canHandle(persistencePackage);
    }

    @Override
    public Boolean canHandleAdd(PersistencePackage persistencePackage) {
        return canHandle(persistencePackage);
    }

    protected Boolean canHandle(PersistencePackage persistencePackage) {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        try {
            return PromotionMessage.class.isAssignableFrom(Class.forName(ceilingEntityFullyQualifiedClassname));
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public DynamicResultSet inspect(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, InspectHelper helper) throws ServiceException {
        Map<String, FieldMetadata> md = getMetadata(persistencePackage, helper);

        if (isRequestFromOfferEntityForm(persistencePackage)) {
            md.remove("excludeFromDisplay");
            md.remove("overriddenPromotionMessage");
        } else if (isRequestFromProductEntityForm(persistencePackage)) {
            BasicFieldMetadata type = (BasicFieldMetadata) md.get("type");
            type.setVisibility(VisibilityEnum.HIDDEN_ALL);
        }

        return getResultSet(persistencePackage, helper, md);
    }

    @Override
    public Entity add(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        if (isRequestFromProductEntityForm(persistencePackage)) {
            ArrayList<Property> properties = new ArrayList<>();
            properties.addAll(Arrays.asList(persistencePackage.getProperties()));
            Property property = new Property();
            property.setName("type");
            property.setValue(PromotionMessageType.TARGETS_AND_QUALIFIERS.getType());
            properties.add(property);
            Property[] propertyArray = new Property[properties.size()];
            properties.toArray(propertyArray);
            persistencePackage.getEntity().setProperties(propertyArray);
        }

        OperationType addType = persistencePackage.getPersistencePerspective().getOperationTypes().getAddType();
        return helper.getCompatibleModule(addType).add(persistencePackage);
    }

    protected boolean isRequestFromOfferEntityForm(PersistencePackage persistencePackage) {
        SectionCrumb sectionCrumb = persistencePackage.getSectionCrumbs()[0];
        Class sectionClass = getClassForName(sectionCrumb.getSectionIdentifier());
        return Offer.class.isAssignableFrom(sectionClass);
    }

    protected boolean isRequestFromProductEntityForm(PersistencePackage persistencePackage) {
        SectionCrumb sectionCrumb = persistencePackage.getSectionCrumbs()[0];
        Class sectionClass = getClassForName(sectionCrumb.getSectionIdentifier());
        return Product.class.isAssignableFrom(sectionClass);
    }
}
