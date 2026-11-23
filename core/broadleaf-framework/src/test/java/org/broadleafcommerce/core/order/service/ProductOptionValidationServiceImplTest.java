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
package org.broadleafcommerce.core.order.service;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.broadleafcommerce.core.catalog.domain.ProductOption;
import org.broadleafcommerce.core.catalog.domain.ProductOptionImpl;
import org.broadleafcommerce.core.catalog.service.type.ProductOptionValidationType;
import org.broadleafcommerce.core.order.service.exception.ProductOptionValidationException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.env.StandardEnvironment;

/**
 * Tests the regex validation guard in {@link ProductOptionValidationServiceImpl}: a REGEX option whose validation
 * string is empty must not be treated as a real pattern (an empty regex matches only empty input, so it would
 * otherwise reject every value), while a real regex still rejects a non-matching value.
 */
public class ProductOptionValidationServiceImplTest {

    private ProductOptionValidationServiceImpl service;

    @Before
    public void setUp() {
        service = new ProductOptionValidationServiceImpl();
        // validate() reads blc.site.enable.xssWrapper from the environment; StandardEnvironment returns the "false"
        // default, so the ESAPI decode branch is skipped and no Spring context is needed. The field is protected and
        // this test shares its package.
        service.environment = new StandardEnvironment();
    }

    @Test
    public void emptyValidationStringAcceptsAnyValue() {
        assertTrue(service.validate(regexOption(""), "Soccer"));
    }

    @Test
    public void nonMatchingValueIsRejectedForRealRegex() {
        try {
            service.validate(regexOption("[0-9]+"), "Soccer");
            fail("Expected a ProductOptionValidationException: 'Soccer' does not match [0-9]+");
        } catch (ProductOptionValidationException expected) {
            // expected
        }
    }

    @Test
    public void matchingValueIsAcceptedForRealRegex() {
        assertTrue(service.validate(regexOption("[A-Za-z]+"), "Soccer"));
    }

    private ProductOption regexOption(String validationString) {
        ProductOption option = new ProductOptionImpl();
        option.setAttributeName("sport");
        option.setRequired(false);
        option.setProductOptionValidationType(ProductOptionValidationType.REGEX);
        option.setValidationString(validationString);
        option.setErrorCode("INVALID_SPORT");
        option.setErrorMessage("Invalid value");
        return option;
    }

}
