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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.presentation.client.OperationType;
import org.broadleafcommerce.common.util.BLCMessageUtils;
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

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

@Component("blUpSaleProductCustomPersistenceHandler")
public class UpSaleProductCustomPersistenceHandler extends ClassCustomPersistenceHandlerAdapter {

    private static final Log LOG = LogFactory.getLog(UpSaleProductCustomPersistenceHandler.class);
    protected static final String PRODUCT_ID = "product.id";
    protected static final String RELATED_SALE_PRODUCT_ID = "relatedSaleProduct.id";
    protected static final String PRODUCTS_SEPARATOR = " -> ";

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
        this.validateUpSaleProduct(persistencePackage.getEntity());
        try {
            OperationType updateType = persistencePackage.getPersistencePerspective().getOperationTypes().getUpdateType();
            return helper.getCompatibleModule(updateType).add(persistencePackage);
        } catch (Exception e) {
            LOG.error("Unable to add entity (execute persistence activity)");
            throw new ServiceException("Unable to add entity", e);
        }
    }

    protected void validateUpSaleProduct(final Entity entity) throws ValidationException {
        final Property productIdProperty = entity.findProperty(PRODUCT_ID);
        final Property relatedSaleProductIdProperty = entity.findProperty(RELATED_SALE_PRODUCT_ID);
        if (relatedSaleProductIdProperty != null && relatedSaleProductIdProperty.getValue() != null
                && productIdProperty != null && productIdProperty.getValue() != null) {
            this.validateSelfLink(entity, relatedSaleProductIdProperty.getValue(), productIdProperty.getValue());
            final String relatedSaleProductId = relatedSaleProductIdProperty.getValue();
            final String productId = productIdProperty.getValue();
            final Product relatedProduct = this.catalogService.findProductById(Long.parseLong(relatedSaleProductId));
            final Product product = this.catalogService.findProductById(Long.parseLong(productId));
            this.validateDuplicateChild(entity, relatedProduct, product);
            this.validateRecursiveRelationship(entity, relatedProduct, product);
        }
    }

    protected void validateSelfLink(final Entity entity, final String relatedSaleProductId, final String productId)
            throws ValidationException {
        if (relatedSaleProductId.equals(productId)) {
            entity.addGlobalValidationError("validateProductSelfLink");
            throw new ValidationException(entity);
        }
    }

    protected void validateDuplicateChild(final Entity entity, final Product relatedProduct, final Product product)
            throws ValidationException {
        final List<Long> childProductIds = product.getUpSaleProducts().stream()
                .map(upSaleProduct -> upSaleProduct.getRelatedProduct().getId())
                .collect(Collectors.toList());
        if (childProductIds.contains(relatedProduct.getId())) {
            entity.addGlobalValidationError("validateProductDuplicateChild");
            throw new ValidationException(entity);
        }
    }

    protected void validateRecursiveRelationship(final Entity entity, final Product relatedProduct,
                                                 final Product product) throws ValidationException {
        final StringBuilder productLinks = new StringBuilder();
        this.addProductLink(productLinks, product.getName());
        this.addProductLink(productLinks, relatedProduct.getName());
        this.validateUpSaleProducts(entity, relatedProduct, product.getId(), productLinks);
    }

    protected void validateUpSaleProducts(final Entity entity, final Product product, final Long id,
                                          final StringBuilder productLinks) throws ValidationException {
        if (product != null) {
            for (RelatedProduct upSaleProduct : product.getUpSaleProducts()) {
                final Product relatedProduct = upSaleProduct.getRelatedProduct();
                if (relatedProduct != null) {
                    final StringBuilder newProductLinks = new StringBuilder(productLinks);
                    this.addProductLink(newProductLinks, relatedProduct.getName());
                    if (relatedProduct.getId().equals(id)) {
                        newProductLinks.delete(newProductLinks.lastIndexOf(PRODUCTS_SEPARATOR), newProductLinks.length());
                        final String errorMessage = BLCMessageUtils.getMessage(
                                "validateProductRecursiveRelationship", newProductLinks
                        );
                        entity.addGlobalValidationError(errorMessage);
                        throw new ValidationException(entity);
                    }
                    this.validateUpSaleProducts(entity, relatedProduct, id, newProductLinks);
                }
            }
        }
    }

    protected void addProductLink(final StringBuilder productLinks, final String productName) {
        productLinks.append(productName);
        productLinks.append(PRODUCTS_SEPARATOR);
    }

}
