/*-
 * #%L
 * BroadleafCommerce Framework Web
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
package org.broadleafcommerce.core.web.seo;

import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.web.expression.SeoElementVariableExpression;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Verifies that the default SEO title/description are resolved to plain text in Java, rather than
 * returned as Thymeleaf/SpEL expression patterns for the storefront template to evaluate (HS #59175).
 */
public class SeoDefaultPropertyServiceImplTest {

    private SeoDefaultPropertyServiceImpl service;

    @Before
    public void setUp() {
        SeoElementVariableExpression seoElement = new SeoElementVariableExpression() {
            @Override
            public String getSiteSimpleURL() {
                return "TheHeatClinic.com";
            }
        };

        service = new SeoDefaultPropertyServiceImpl() {
            @Override
            protected String getSiteName() {
                return "The Heat Clinic";
            }
        };
        service.seoElement = seoElement;
    }

    @Test
    public void productTitleIsResolvedToPlainText() {
        Category defaultCategory = EasyMock.createNiceMock(Category.class);
        EasyMock.expect(defaultCategory.getMetaTitle()).andReturn(null).anyTimes();
        EasyMock.expect(defaultCategory.getName()).andReturn("Hot Sauces").anyTimes();
        EasyMock.replay(defaultCategory);

        Product product = EasyMock.createNiceMock(Product.class);
        EasyMock.expect(product.getMetaTitle()).andReturn(null).anyTimes();
        EasyMock.expect(product.getName()).andReturn("Sudden Death Sauce").anyTimes();
        EasyMock.expect(product.getDefaultCategory()).andReturn(defaultCategory).anyTimes();
        EasyMock.replay(product);

        String title = service.getProductTitle(product);

        assertEquals("Sudden Death Sauce - Hot Sauces - The Heat Clinic", title);
        assertNotAnExpression(title);
    }

    @Test
    public void productDescriptionIsResolvedToPlainText() {
        Product product = EasyMock.createNiceMock(Product.class);
        EasyMock.expect(product.getName()).andReturn("Sudden Death Sauce").anyTimes();
        EasyMock.expect(product.getMetaDescription()).andReturn(null).anyTimes();
        EasyMock.expect(product.getLongDescription()).andReturn("Blazing hot.").anyTimes();
        EasyMock.replay(product);

        String description = service.getProductDescription(product);

        assertEquals("Shop for Sudden Death Sauce at TheHeatClinic.com. Blazing hot.", description);
        assertNotAnExpression(description);
    }

    @Test
    public void categoryTitleIsResolvedToPlainText() {
        Category category = EasyMock.createNiceMock(Category.class);
        EasyMock.expect(category.getMetaTitle()).andReturn(null).anyTimes();
        EasyMock.expect(category.getName()).andReturn("Hot Sauces").anyTimes();
        EasyMock.replay(category);

        String title = service.getCategoryTitle(category);

        assertEquals("Hot Sauces - The Heat Clinic", title);
        assertNotAnExpression(title);
    }

    @Test
    public void categoryDescriptionIsResolvedToPlainText() {
        Category category = EasyMock.createNiceMock(Category.class);
        EasyMock.expect(category.getName()).andReturn("Hot Sauces").anyTimes();
        EasyMock.expect(category.getMetaDescription()).andReturn(null).anyTimes();
        EasyMock.expect(category.getLongDescription()).andReturn("Everything spicy.").anyTimes();
        EasyMock.replay(category);

        String description = service.getCategoryDescription(category);

        assertEquals("Shop Hot Sauces at TheHeatClinic.com. Everything spicy.", description);
        assertNotAnExpression(description);
    }

    @Test
    public void adminSuppliedMetaTitleIsReturnedAsLiteralNotAnExpressionPattern() {
        // The merchant-controlled metaTitle flows through verbatim; it is NOT turned into, or wrapped in,
        // an expression. (The template escapes it on output — see the ssti-test regression project.)
        Category defaultCategory = EasyMock.createNiceMock(Category.class);
        EasyMock.replay(defaultCategory);

        Product product = EasyMock.createNiceMock(Product.class);
        EasyMock.expect(product.getMetaTitle()).andReturn("@environment.getProperty('x')").anyTimes();
        EasyMock.expect(product.getDefaultCategory()).andReturn(null).anyTimes();
        EasyMock.replay(product);

        String title = service.getProductTitle(product);

        // Present verbatim as data; evaluation never happens here (and no longer in the template).
        assertEquals("@environment.getProperty('x') - The Heat Clinic", title);
    }

    private static void assertNotAnExpression(String value) {
        assertFalse("must not contain a Thymeleaf expression object", value.contains("#seoElement"));
        assertFalse("must not contain a variable expression", value.contains("${"));
        assertFalse("must not contain a preprocessing expression", value.contains("__$"));
    }
}
