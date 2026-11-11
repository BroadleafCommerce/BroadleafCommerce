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

import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductImpl;
import org.broadleafcommerce.core.catalog.domain.ProductOption;
import org.broadleafcommerce.core.catalog.domain.ProductOptionImpl;
import org.broadleafcommerce.core.catalog.domain.ProductOptionValue;
import org.broadleafcommerce.core.catalog.domain.ProductOptionValueImpl;
import org.broadleafcommerce.core.catalog.domain.ProductOptionXref;
import org.broadleafcommerce.core.catalog.domain.ProductOptionXrefImpl;
import org.broadleafcommerce.core.catalog.service.type.ProductOptionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

/**
 * Unit tests for {@link ProductOptionValidationServiceImpl#validateProductOptionAttributes(Product, Map)}.
 */
public class ProductOptionValidationServiceImplTest extends TestCase {

    private ProductOptionValidationServiceImpl service;

    @Override
    protected void setUp() {
        service = new ProductOptionValidationServiceImpl();
    }

    /**
     * An enumerated option (allowed values Red, Blue) and a free-text option (no allowed values).
     */
    private Product buildProduct() {
        ProductImpl product = new ProductImpl();
        product.setId(1L);

        ProductOptionImpl color = new ProductOptionImpl();
        color.setAttributeName("color");
        color.setAllowedValues(new ArrayList<>(Arrays.asList(optionValue("Red"), optionValue("Blue"))));

        ProductOptionImpl monogram = new ProductOptionImpl();
        monogram.setAttributeName("monogram"); // free text, no allowed values

        List<ProductOptionXref> xrefs = new ArrayList<>();
        xrefs.add(xref(color));
        xrefs.add(xref(monogram));
        product.setProductOptionXrefs(xrefs);
        return product;
    }

    private ProductOptionValue optionValue(String value) {
        ProductOptionValue optionValue = new ProductOptionValueImpl();
        optionValue.setAttributeValue(value);
        return optionValue;
    }

    private ProductOptionXref xref(ProductOption option) {
        ProductOptionXref xref = new ProductOptionXrefImpl();
        xref.setProductOption(option);
        return xref;
    }

    private Map<String, String> attributes(String... keyValues) {
        Map<String, String> attributes = new HashMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            attributes.put(keyValues[i], keyValues[i + 1]);
        }
        return attributes;
    }

    public void testUnknownAttributeIsSkipped() {
        // not a product option on the product; it is a custom order item attribute and must not be rejected
        service.validateProductOptionAttributes(buildProduct(), attributes("size", "L"));
    }

    public void testAllowedOptionValuePasses() {
        service.validateProductOptionAttributes(buildProduct(), attributes("color", "Red"));
    }

    public void testDisallowedOptionValueIsRejected() {
        try {
            service.validateProductOptionAttributes(buildProduct(), attributes("color", "Purple"));
            fail("Expected a value outside the allowed values to be rejected");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Purple"));
        }
    }

    public void testUnknownAttributeSkippedButKnownOptionStillValidated() {
        try {
            service.validateProductOptionAttributes(buildProduct(), attributes("size", "L", "color", "Purple"));
            fail("Expected the disallowed value for a known option to be rejected");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Purple"));
        }
    }

    public void testFreeTextOptionValuePasses() {
        service.validateProductOptionAttributes(buildProduct(), attributes("monogram", "ABC"));
    }

    public void testEmptyValueOnEnumeratedOptionPasses() {
        service.validateProductOptionAttributes(buildProduct(), attributes("color", ""));
    }

    public void testNonEnumeratedOptionTypeIsExcludedEvenWithAllowedValues() {
        ProductImpl product = new ProductImpl();
        product.setId(1L);
        ProductOptionImpl engraving = new ProductOptionImpl();
        engraving.setAttributeName("engraving");
        engraving.setType(ProductOptionType.TEXT);
        engraving.setAllowedValues(new ArrayList<>(Arrays.asList(optionValue("Red"), optionValue("Blue"))));
        List<ProductOptionXref> xrefs = new ArrayList<>();
        xrefs.add(xref(engraving));
        product.setProductOptionXrefs(xrefs);

        service.validateProductOptionAttributes(product, attributes("engraving", "Anything"));
    }

    public void testNullProductIsIgnored() {
        service.validateProductOptionAttributes(null, attributes("color", "Purple"));
    }
}
