/*
 * #%L
 * BroadleafCommerce Admin Module
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.presentation.client.OperationType;
import org.broadleafcommerce.common.sandbox.SandBoxHelper;
import org.broadleafcommerce.core.catalog.domain.ProductOptionImpl;
import org.broadleafcommerce.core.catalog.domain.ProductOptionValue;
import org.broadleafcommerce.openadmin.dto.CriteriaTransferObject;
import org.broadleafcommerce.openadmin.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.FieldPathBuilder;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.FilterMapping;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.Restriction;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.predicate.PredicateProvider;
import org.springframework.stereotype.Component;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

/**
 * @author Jeff Fischer
 */
@Component("blProductOptionValuesCustomPersistenceHandler")
public class ProductOptionValuesCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {

    @Resource(name="blSandBoxHelper")
    protected SandBoxHelper sandBoxHelper;

    @Override
    public Boolean canHandleFetch(PersistencePackage persistencePackage) {
        //Make sure this is not only the right entity, but that it's also a ToOne lookup from AdminBasicOperationsController#showSelectCollectionItem()
        boolean isQualified;
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        try {
            Class testClass = Class.forName(ceilingEntityFullyQualifiedClassname);
            isQualified = ProductOptionValue.class.isAssignableFrom(testClass);
        } catch (ClassNotFoundException e) {
            isQualified = false;
        }
        if (isQualified) {
            isQualified = getOptionKey(persistencePackage) != null;
        }
        return isQualified;
    }

    @Override
    public DynamicResultSet fetch(PersistencePackage persistencePackage, CriteriaTransferObject cto, DynamicEntityDao
            dynamicEntityDao, RecordHelper helper) throws ServiceException {
        Long optionId = getOptionKey(persistencePackage);
        if (optionId != null) {
            FilterMapping filterMapping = new FilterMapping().withDirectFilterValues(sandBoxHelper.mergeCloneIds(ProductOptionImpl.class, optionId)).withRestriction(new Restriction()
                .withPredicateProvider(new PredicateProvider() {
                    public Predicate buildPredicate(CriteriaBuilder builder, FieldPathBuilder fieldPathBuilder, From root,
                                                    String ceilingEntity, String fullPropertyName, Path explicitPath, List directValues) {
                        return root.get("productOption").get("id").in(directValues);
                    }
                }));
            cto.getAdditionalFilterMappings().add(filterMapping);
        }
        return helper.getCompatibleModule(OperationType.BASIC).fetch(persistencePackage, cto);
    }

    protected Long getOptionKey(PersistencePackage persistencePackage) {
        String key = "option=";
        Long response = null;
        for (String criteria : persistencePackage.getCustomCriteria()) {
            if (criteria.startsWith(key)) {
                response = Long.parseLong(criteria.substring(key.length(), criteria.length()));
                break;
            }
        }
        return response;
    }
}
