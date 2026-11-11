/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2026 Broadleaf Commerce
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
package org.broadleafcommerce.core.order.service.workflow.add;

import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductImpl;
import org.broadleafcommerce.core.catalog.domain.ProductOption;
import org.broadleafcommerce.core.order.service.ProductOptionValidationService;
import org.broadleafcommerce.core.workflow.ActivityMessages;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

/**
 * Verifies that {@link ValidateAddRequestActivity} delegates product-option validation to the
 * {@link ProductOptionValidationService} only when enabled. The validation logic itself is tested in
 * {@code ProductOptionValidationServiceImplTest}.
 */
public class ValidateAddRequestActivityTest extends TestCase {

    private ValidateAddRequestActivity activity;
    private RecordingProductOptionValidationService service;

    @Override
    protected void setUp() {
        service = new RecordingProductOptionValidationService();
        activity = new ValidateAddRequestActivity();
        activity.productOptionValidationService = service;
    }

    public void testDelegatesWhenEnabled() {
        activity.validateProductOptionsEnabled = true;
        Product product = new ProductImpl();
        Map<String, String> attributes = new HashMap<>();
        attributes.put("color", "Red");

        activity.validateProductOptions(product, attributes);

        assertTrue("validation should be delegated to the service", service.called);
        assertSame(product, service.lastProduct);
        assertSame(attributes, service.lastAttributes);
    }

    public void testSkippedWhenDisabled() {
        activity.validateProductOptionsEnabled = false;

        activity.validateProductOptions(new ProductImpl(), new HashMap<>());

        assertFalse("validation should not run when disabled", service.called);
    }

    private static class RecordingProductOptionValidationService implements ProductOptionValidationService {

        private boolean called;
        private Product lastProduct;
        private Map<String, String> lastAttributes;

        @Override
        public void validateProductOptionAttributes(Product product, Map<String, String> attributeValues) {
            called = true;
            lastProduct = product;
            lastAttributes = attributeValues;
        }

        @Override
        public Boolean validate(ProductOption productOption, String value) {
            return true;
        }

        @Override
        public boolean isSubmitType(ProductOption productOption) {
            return false;
        }

        @Override
        public boolean hasProductOptionValidationStrategy(ProductOption productOption) {
            return false;
        }

        @Override
        public boolean isAddOrNoneType(ProductOption productOption) {
            return false;
        }

        @Override
        public void validateWithoutException(ProductOption productOption, String attributeValue, ActivityMessages messages) {
            // no-op
        }

        @Override
        public List<Long> findSkuIdsForProductOptionValues(Long id, String key, String value, List<Long> possibleSkuIds) {
            return possibleSkuIds;
        }
    }
}
