/*-
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.catalog.service;

import org.broadleafcommerce.common.copy.MultiTenantCloneable;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.persistence.AbstractEntityDuplicationHelper;
import org.broadleafcommerce.common.persistence.EntityDuplicatorExtensionManager;
import org.broadleafcommerce.common.service.GenericEntityService;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.CategoryImpl;
import org.broadleafcommerce.core.catalog.domain.CategoryProductXref;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductImpl;
import org.broadleafcommerce.core.catalog.domain.ProductOption;
import org.broadleafcommerce.core.catalog.domain.ProductOptionImpl;
import org.broadleafcommerce.core.catalog.domain.ProductOptionXref;
import org.broadleafcommerce.core.catalog.service.extension.ProductUrlDuplicatorExtensionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;

import static org.broadleafcommerce.common.copy.MultiTenantCopyContext.PROPAGATION;

@Component("blProductDuplicateModifier")
public class ProductDuplicateModifier extends AbstractEntityDuplicationHelper<Product> {

    private static final String COPY_NUMBER_SEPARATOR = "#";

    @Resource(name = "blEntityDuplicatorExtensionManager")
    protected EntityDuplicatorExtensionManager extensionManager;

    @Resource(name = "blGenericEntityService")
    protected GenericEntityService genericEntityService;

    @Resource(name = "blProductUrlDuplicatorExtensionManager")
    protected ProductUrlDuplicatorExtensionManager productUrlDuplicatorExtensionManager;

    @Autowired
    public ProductDuplicateModifier(final Environment environment) {

        super(environment);

        addCopyHint(ProductImpl.EXCLUDE_PRODUCT_CODE_COPY_HINT, Boolean.TRUE.toString());
    }

    @Override
    public boolean canHandle(final MultiTenantCloneable candidate) {
        return Product.class.isAssignableFrom(candidate.getClass());
    }

    @Override
    public void modifyInitialDuplicateState(
            final Product original,
            final Product copy,
            final MultiTenantCopyContext context
    ) throws CloneNotSupportedException {
        if (context.getCopyHints().get(PROPAGATION) != null
                && "TRUE".equalsIgnoreCase(context.getCopyHints().get(PROPAGATION))) {
            for (CategoryProductXref allParentCategoryXref : original.getAllParentCategoryXrefs()) {
                final CategoryProductXref clone = allParentCategoryXref.createOrRetrieveCopyInstance(context).getClone();
                clone.setProduct(copy);
                ExtensionResultHolder<Map<Long, Map<Long, Long>>> resultHolder = new ExtensionResultHolder<>();
                Long categoryId = clone.getCategory().getId();
                extensionManager.getClonesByCatalogs("BLC_CATEGORY", categoryId, context, resultHolder);
                Long aLong = resultHolder.getResult().get(categoryId).get(context.getToCatalog().getId());
                Category category = (Category) genericEntityService.readGenericEntity(
                        genericEntityService.getCeilingImplClass(CategoryImpl.class.getName()),
                        aLong
                );
                clone.setCategory(category);
                copy.getAllParentCategoryXrefs().add(clone);
            }
            List<ProductOptionXref> productOptionXrefs = new ArrayList<>();
            for (ProductOptionXref productOptionXref : original.getProductOptionXrefs()) {
                final ProductOptionXref clone = productOptionXref.createOrRetrieveCopyInstance(context).getClone();
                ExtensionResultHolder<Map<Long, Map<Long, Long>>> resultHolder = new ExtensionResultHolder<>();
                Long optionId = clone.getProductOption().getId();
                extensionManager.getClonesByCatalogs("BLC_PRODUCT_OPTION", optionId, context, resultHolder);
                Long aLong = resultHolder.getResult().get(optionId).get(context.getToCatalog().getId());
                ProductOption productOption = (ProductOption) genericEntityService.readGenericEntity(
                        genericEntityService.getCeilingImplClass(ProductOptionImpl.class.getName()),
                        aLong
                );
                clone.setProductOption(productOption);
                clone.setProduct(copy);
                productOptionXrefs.add(clone);
            }
            copy.setProductOptionXrefs(productOptionXrefs);

        } else {
            if (!context.getToCatalog().getId().equals(context.getFromCatalog().getId())) {
                copy.setAllParentCategoryXrefs(new ArrayList<>());
                copy.setProductOptionXrefs(new ArrayList<>());
            } else {
                for (CategoryProductXref allParentCategoryXref : original.getAllParentCategoryXrefs()) {
                    final CategoryProductXref clone = allParentCategoryXref.createOrRetrieveCopyInstance(context)
                            .getClone();
                    clone.setProduct(copy);
                    copy.getAllParentCategoryXrefs().add(clone);
                }
                List<ProductOptionXref> productOptionXrefs = new ArrayList<>();
                for (ProductOptionXref productOptionXref : original.getProductOptionXrefs()) {
                    final ProductOptionXref clone = productOptionXref.createOrRetrieveCopyInstance(context).getClone();
                    clone.setProduct(copy);
                    productOptionXrefs.add(clone);
                }
                copy.setProductOptionXrefs(productOptionXrefs);
            }
        }
        final Date currentDate = new Date();
        copy.setActiveStartDate(currentDate);
        copy.setActiveEndDate(currentDate);

        setNameAndUrl(copy, context);
    }

    protected void setNameAndUrl(Product copy, MultiTenantCopyContext context) {
        String suffix = getCopySuffix();
        String name = copy.getName();
        if (!context.getCopyHints().containsKey("PROPAGATION")) {
            int index = 0;
            if (name.contains(suffix)) {
                index = name.indexOf(COPY_NUMBER_SEPARATOR);
                if (index > 0) {
                    final String copyNumber = name.split(suffix)[1];
                    suffix = String.format("%d", Long.parseLong(copyNumber.split(COPY_NUMBER_SEPARATOR)[1]) + 1);
                } else {
                    suffix = COPY_NUMBER_SEPARATOR + 1;
                }
                if (index > 0) {
                    name = name.substring(0, index + 1) + suffix;
                } else {
                    name = name + suffix;
                }
            } else {
                name = name + suffix;
            }
        }
        copy.setName(name);
        String url = "/" + copy.getName().replace("-", "").replace(" ", "-")
                .replace("#", "_").toLowerCase();
        ExtensionResultStatusType extensionResultStatusType = productUrlDuplicatorExtensionManager.getProxy().modifyUrl(
                url, copy, new ExtensionResultHolder<>()
        );
        if (extensionResultStatusType == ExtensionResultStatusType.NOT_HANDLED) {
            copy.setUrl(url);
        }
    }

}
