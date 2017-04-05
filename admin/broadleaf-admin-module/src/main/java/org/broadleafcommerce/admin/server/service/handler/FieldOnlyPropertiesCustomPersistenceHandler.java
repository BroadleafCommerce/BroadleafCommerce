/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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

package org.broadleafcommerce.admin.server.service.handler;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.core.search.domain.Field;
import org.broadleafcommerce.core.search.domain.FieldImpl;
import org.broadleafcommerce.openadmin.dto.*;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.InspectHelper;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * @author jfleschler
 */
@Component("blFieldOnlyPropertiesCustomPersistenceHandler")
public class FieldOnlyPropertiesCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {

    private static final Log LOG = LogFactory.getLog(FieldOnlyPropertiesCustomPersistenceHandler.class);

    @Override
    public Boolean canHandleInspect(PersistencePackage persistencePackage) {
        return ArrayUtils.isNotEmpty(persistencePackage.getCustomCriteria()) && Objects.equals(persistencePackage.getCustomCriteria()[0], "fieldImplOnly");
    }

    @Override
    public DynamicResultSet inspect(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, InspectHelper helper) throws ServiceException {

        // Get all properties
        Map<String, FieldMetadata> properties = getMetadata(persistencePackage, helper);
        Iterator it = properties.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            FieldMetadata md = (FieldMetadata) pair.getValue();

            // If this property was not inherited from Field, remove it
            if (!md.getInheritedFromType().equals(FieldImpl.class.getName())) {
                it.remove();
            }
        }

        // Merge changes back
        Map<MergedPropertyType, Map<String, FieldMetadata>> allMergedProperties = new HashMap<MergedPropertyType, Map<String, FieldMetadata>>();
        allMergedProperties.put(MergedPropertyType.PRIMARY, properties);
        Class<?>[] entityClasses = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(Field.class);
        ClassMetadata mergedMetadata = helper.buildClassMetadata(entityClasses, persistencePackage, allMergedProperties);

        return new DynamicResultSet(mergedMetadata, null, null);
    }
}
