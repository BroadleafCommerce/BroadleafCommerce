/*
 * #%L
 * BroadleafCommerce Admin Module
 * %%
 * Copyright (C) 2009 - 2022 Broadleaf Commerce
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.presentation.client.OperationType;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.RelatedProduct;
import org.broadleafcommerce.core.catalog.domain.UpSaleProduct;
import org.broadleafcommerce.core.catalog.domain.UpSaleProductImpl;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.ValidationException;
import org.broadleafcommerce.openadmin.server.service.handler.ClassCustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

@Component("blUpSaleProductCustomPersistenceHandler")
public class UpSaleProductCustomPersistenceHandler extends ClassCustomPersistenceHandlerAdapter {

    private static final Log LOG = LogFactory.getLog(UpSaleProductCustomPersistenceHandler.class);
    protected static final String PRODUCT_ID = "product.id";
    protected static final String RELATED_SALE_PRODUCT_ID = "relatedSaleProduct.id";

    @Resource(name = "blCatalogService")
    protected CatalogService catalogService;

    public UpSaleProductCustomPersistenceHandler() {
        super(UpSaleProduct.class, UpSaleProductImpl.class);
    }

    @Override
    public Boolean canHandleAdd(PersistencePackage persistencePackage) {
        return super.classMatches(persistencePackage);
    }

    @Override
    public Entity add(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        Entity entity = this.validateUpSaleProduct(persistencePackage.getEntity());
        try {
            if (!entity.isValidationFailure()) {
                OperationType updateType = persistencePackage.getPersistencePerspective().getOperationTypes().getUpdateType();
                entity = helper.getCompatibleModule(updateType).add(persistencePackage);
            }
            return entity;
        } catch (Exception e) {
            LOG.error("Unable to add entity (execute persistence activity) ", e);
            throw new ServiceException("Unable to add entity", e);
        }
    }

    protected Entity validateUpSaleProduct(final Entity entity) throws ServiceException {
        final Entity entityAfterValidateSelfLink = this.validateSelfLink(entity);
        return this.validateRecursiveRelationship(entityAfterValidateSelfLink);
    }

    protected Entity validateSelfLink(final Entity entity) throws ServiceException {
        try {
            Property productIdProperty = entity.findProperty(PRODUCT_ID);
            Property relatedSaleProductIdProperty = entity.findProperty(RELATED_SALE_PRODUCT_ID);
            if (relatedSaleProductIdProperty != null && relatedSaleProductIdProperty.getValue() != null
                    && productIdProperty != null) {
                String relatedSaleProductId = relatedSaleProductIdProperty.getValue();
                String productId = productIdProperty.getValue();
                if (relatedSaleProductId.equals(productId)) {
                    entity.addValidationError(RELATED_SALE_PRODUCT_ID, "validateSelfLink");
                }
            }
            return entity;
        } catch (Exception e) {
            String message = "Unable to execute persistence " + entity.getType()[0];
            LOG.error(message, e);
            throw new ServiceException(message, e);
        }
    }

    protected Entity validateRecursiveRelationship(final Entity entity) throws ServiceException {
        try {
            final Property productIdProperty = entity.findProperty(PRODUCT_ID);
            final Property relatedSaleProductId = entity.findProperty(RELATED_SALE_PRODUCT_ID);
            if (relatedSaleProductId != null && relatedSaleProductId.getValue() != null && productIdProperty != null) {
                final String productId = relatedSaleProductId.getValue();
                final Set<Long> ids = this.allProductIds(productId);
                if (ids.contains(Long.parseLong(productIdProperty.getValue()))) {
                    entity.getGlobalValidationErrors().add("This linked upSale product cannot be used because it is a recursive relationship");
                }
            }
            return entity;
        } catch (Exception e) {
            String message = "Unable to execute persistence " + entity.getType()[0];
            LOG.error(message, e);
            throw new ServiceException(message, e);
        }
    }

    protected Set<Long> allProductIds(final String productId) {
        final Set<Long> ids = new HashSet<>();
        if (productId != null) {
            final Product product = this.catalogService.findProductById(Long.parseLong(productId));
            if (product != null) {
                ids.addAll(this.upSaleProductIds(product, new HashSet<>()));
            }
        }
        return ids;
    }

    protected Set<Long> upSaleProductIds(final Product upSaleProduct, final Set<Long> ids) {
        final Long upSaleProductId = upSaleProduct.getId();
        final Product originProduct = this.catalogService.findProductById(upSaleProductId);
        for (RelatedProduct relatedProduct : originProduct.getUpSaleProducts()) {
            final Product product = relatedProduct.getRelatedProduct();
            if (product != null && !ids.contains(product.getId())) {
                ids.add(product.getId());
                final Set<Long> longs = this.upSaleProductIds(product, ids);
                ids.addAll(longs);
            }
        }
        return ids;
    }

}
