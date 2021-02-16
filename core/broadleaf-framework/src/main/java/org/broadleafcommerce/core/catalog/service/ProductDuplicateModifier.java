/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2021 Broadleaf Commerce
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
import org.broadleafcommerce.common.persistence.AbstractEntityDuplicationHelper;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component("blProductDuplicateModifier")
public class ProductDuplicateModifier extends AbstractEntityDuplicationHelper<Product> {

    private static final String COPY_NUMBER_SEPARATOR = "#";

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
    public void modifyInitialDuplicateState(final Product copy) {
        setNameAndUrl(copy);
        copy.setDefaultSku(copy.getDefaultSku());
    }

    private void setNameAndUrl(Product copy) {
        String suffix = getCopySuffix();
        String name = copy.getName();

        if (name.contains(suffix)) {
            if (name.contains(COPY_NUMBER_SEPARATOR)) {
                final String copyNumber = name.split(suffix)[1];
                suffix = String.format("%d", Long.parseLong(copyNumber.split(COPY_NUMBER_SEPARATOR)[1]) + 1);
            } else {
                suffix = COPY_NUMBER_SEPARATOR + 1;
            }
            name = name.substring(0, name.length() - 1) + suffix;
        } else {
            name = name + suffix;
        }
        copy.setName(name);
        copy.setUrl(copy.getName().replace("-", "").replace(" ", "-").toLowerCase());
    }
}
