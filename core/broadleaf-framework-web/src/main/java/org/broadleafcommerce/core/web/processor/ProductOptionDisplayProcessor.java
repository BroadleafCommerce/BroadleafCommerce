/*-
 * #%L
 * BroadleafCommerce Framework Web
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

package org.broadleafcommerce.core.web.processor;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductOption;
import org.broadleafcommerce.core.catalog.domain.ProductOptionXref;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.OrderItemAttribute;
import org.broadleafcommerce.presentation.condition.ConditionalOnTemplating;
import org.broadleafcommerce.presentation.dialect.AbstractBroadleafVariableModifierProcessor;
import org.broadleafcommerce.presentation.model.BroadleafTemplateContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Priyesh Patel
 */
@Component("blProductOptionDisplayProcessor")
@ConditionalOnTemplating
public class ProductOptionDisplayProcessor extends AbstractBroadleafVariableModifierProcessor {

    protected static final Logger logger = LoggerFactory.getLogger(ProductOptionDisplayProcessor.class);

    @Resource(name = "blCatalogService")
    protected CatalogService catalogService;

    @Override
    public String getName() {
        return "product_option_display";
    }

    @Override
    public int getPrecedence() {
        return 100;
    }

    @Override
    public boolean useGlobalScope() {
        return false;
    }

    @Override
    public Map<String, Object> populateModelVariables(final String tagName, final Map<String, String> tagAttributes, final BroadleafTemplateContext context) {

        final Map<String, String> productOptionDisplayValues = new LinkedHashMap<>();
        final Object item = context.parseExpression(tagAttributes.get("orderItem"));

        if (item instanceof DiscreteOrderItem) {
            // The search of ProductOptions on Sku was removed because we cannot properly sort the items based on their displayOrder / sequence
            final DiscreteOrderItem orderItem = (DiscreteOrderItem)item;
            final Product product = orderItem.getProduct();
            final Map<String, OrderItemAttribute> orderItemAttributes = orderItem.getOrderItemAttributes();

            // Do a search only on ProductOptionXrefs, and find the translation by matching user's input to one of the ProductOptionValues present on ProductOption.getAllowedValues().
            for (ProductOptionXref productOptionXref : product.getProductOptionXrefs()) {
                final ProductOption productOption = productOptionXref.getProductOption();
                final OrderItemAttribute itemAttribute = orderItemAttributes.get(productOption.getAttributeName());

                if (itemAttribute != null && !StringUtils.isEmpty(itemAttribute.getValue())) {
                    final String translatedLabel = productOption.getLabel();
                    final String translatedValue = catalogService.translateItemAttributeValue(itemAttribute, productOption);

                    // Two product options can have same label. If so, only the first product option will be collected in the result.
                    if (productOptionDisplayValues.containsKey(translatedLabel)) {
                        logger.warn("Product (name={}, id={}) has product options with same label ({}), product option labels should be corrected",
                                product.getName(), product.getId(), translatedLabel);
                    } else {
                        productOptionDisplayValues.put(translatedLabel, translatedValue);
                    }
                }
            }
            // Ignore any other attributes that might be present on orderItem, and do not have a related ProductOption configured for them
        }
        return ImmutableMap.of("productOptionDisplayValues", (Object) productOptionDisplayValues);
    }

}
