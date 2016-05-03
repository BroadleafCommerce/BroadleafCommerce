/*
 * #%L
 * BroadleafCommerce Advanced CMS
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
package org.broadleafcommerce.admin.persistence.validation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.sandbox.SandBoxHelper;
import org.broadleafcommerce.core.catalog.domain.ProductBundle;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.server.service.persistence.validation.PropertyValidationResult;
import org.broadleafcommerce.openadmin.server.service.persistence.validation.UriPropertyValidator;
import org.broadleafcommerce.openadmin.server.service.persistence.validation.ValidationConfigurationBasedPropertyValidator;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.Resource;

/**
 * Validates that a ProductBundle cannot have its own Default Sku selected as a Sku Bundle Item
 * 
 * 
 * @author Chris Kittrell (ckittrell)
 */
@Component("blProductBundleSkuBundleItemValidator")
public class ProductBundleSkuBundleItemValidator extends ValidationConfigurationBasedPropertyValidator {

    protected static final Log LOG = LogFactory.getLog(UriPropertyValidator.class);
    private static final String ERROR_MESSAGE = "A Product Bundle's Sku Bundle Items are not allowed to include the Product Bundle's Default Sku.";

    @Resource(name = "blCatalogService")
    public CatalogService catalogService;

    @Resource(name="blSandBoxHelper")
    protected SandBoxHelper sandBoxHelper;

    @Override
    public PropertyValidationResult validate(Entity entity, Serializable instance, Map<String, FieldMetadata> entityFieldMetadata,
            Map<String, String> validationConfiguration, BasicFieldMetadata propertyMetadata, String propertyName,
            String value) {
        String skuId = entity.findProperty("sku") == null ? null : entity.findProperty("sku").getValue();
        String bundleId = entity.findProperty("bundle") == null ? null : entity.findProperty("bundle").getValue();

        if (skuId != null && bundleId != null) {
            ProductBundle productBundle = (ProductBundle) catalogService.findProductById(Long.valueOf(bundleId));
            Long defaultSkuOrigId = sandBoxHelper.getOriginalId(Sku.class, productBundle.getDefaultSku().getId()).getOriginalId();

            if (Long.valueOf(skuId).equals(defaultSkuOrigId)) {
                return new PropertyValidationResult(false, ERROR_MESSAGE);
            }
        }

        return new PropertyValidationResult(true);
    }
}
