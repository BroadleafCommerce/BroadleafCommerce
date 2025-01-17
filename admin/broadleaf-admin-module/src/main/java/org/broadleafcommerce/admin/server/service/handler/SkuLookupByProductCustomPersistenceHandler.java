/*-
 * #%L
 * BroadleafCommerce Admin Module
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.openadmin.dto.CriteriaTransferObject;
import org.broadleafcommerce.openadmin.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.FilterAndSortCriteria;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandler;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

/**
 * This persistence handler handles all {@link Sku} inspects and fetches where the `toOneLookup` custom criteria is on the
 * persistence package and the lookup is product specific.  This allows for special product-based filtering and display
 * functionality for specific sku look-ups.
 *
 * @author Marie Standeven (marieStandeven)
 */
@Component("blSkuLookupByProductCustomPersistenceHandler")
public class SkuLookupByProductCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {

    private static final Log LOG = LogFactory.getLog(SkuLookupByProductCustomPersistenceHandler.class);

    protected static final String TO_ONE_LOOKUP_CRITERIA = "toOneLookup";

    // ability to return skus lookup by product
    protected static final String FILTER_SKUS_BY_PRODUCT = "productFilterForSkus";

    @Resource(name="blCatalogService")
    protected CatalogService catalogService;

    @Resource(name = "blSkuCustomPersistenceHandler")
    protected SkuCustomPersistenceHandler skuPersistenceHandler;

    @Override
    public Boolean canHandleFetch(PersistencePackage persistencePackage) {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        String[] customCriteria = persistencePackage.getCustomCriteria();
        try {
            Class<?> testClass = Class.forName(ceilingEntityFullyQualifiedClassname);
            return !ArrayUtils.isEmpty(customCriteria)
                   && TO_ONE_LOOKUP_CRITERIA.equals(customCriteria[0])
                   && isRequestForSkusFilteredByProduct(persistencePackage)
                   && Sku.class.isAssignableFrom(testClass);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public DynamicResultSet fetch(PersistencePackage persistencePackage, CriteriaTransferObject cto, DynamicEntityDao
            dynamicEntityDao, RecordHelper helper) throws ServiceException {

        // find criteria for productId
        FilterAndSortCriteria productIdCriteria = cto.getCriteriaMap().get("productId");
        List<Serializable> skusFromProducts = new ArrayList<>();
        if (productIdCriteria != null && CollectionUtils.isNotEmpty(productIdCriteria.getFilterValues())) {
            List<String> products = productIdCriteria.getFilterValues();
            for (String productIdString : products) {
                Long productId = Long.parseLong(productIdString);
                Product product = catalogService.findProductById(productId);
                skusFromProducts.addAll(product.getAllSellableSkus());
            }
        }

        PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
        Map<String, FieldMetadata> SkuMetadata = helper.getSimpleMergedProperties(Sku.class.getName(), persistencePerspective);
        Entity[] entities = helper.getRecords(SkuMetadata, skusFromProducts);

        skuPersistenceHandler.updateProductOptionFieldsForFetch(skusFromProducts, entities);
        return new DynamicResultSet(entities, entities.length);
    }

    protected boolean isRequestForSkusFilteredByProduct(PersistencePackage persistencePackage) {
        return ArrayUtils.contains(persistencePackage.getCustomCriteria(), FILTER_SKUS_BY_PRODUCT);
    }

    @Override
    public int getOrder() {
        return CustomPersistenceHandler.DEFAULT_ORDER - 2;
    }
}
