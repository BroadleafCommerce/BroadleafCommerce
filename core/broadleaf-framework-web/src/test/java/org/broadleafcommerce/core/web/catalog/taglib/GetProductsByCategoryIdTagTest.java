/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License” located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License” located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.web.catalog.taglib;

import javax.servlet.jsp.JspException;

public class GetProductsByCategoryIdTagTest extends BaseTagLibTest {
    
    private GetProductsByCategoryIdTag getProductsByCategoryIdTag;

    public void test_GetProductsByCategoryIdTag() throws JspException {
        //TODO Fix this test - there are expectation failures
        /*getProductsByCategoryIdTag.setJspContext(pageContext);
        getProductsByCategoryIdTag.setCatalogService(catalogService);

        List<Product> productList = new ArrayList<Product>();
        Product p1 = EasyMock.createStrictMock(Product.class);
        Product p2 = EasyMock.createStrictMock(Product.class);
        productList.add(p1);
        productList.add(p2);

        pageContext.setAttribute("productListVar", productList);

        Category c = EasyMock.createStrictMock(Category.class);

        getProductsByCategoryIdTag.setCategoryId(0L);
        getProductsByCategoryIdTag.setVar("productListVar");

        EasyMock.expect(pageContext.getAttribute("productListVar")).andReturn(productList);

        EasyMock.expect(catalogService.findCategoryById(0L)).andReturn(c);
        EasyMock.expect(catalogService.findActiveProductsByCategory(c, new Date())).andReturn(productList);

        EasyMock.replay(p1, p2, c);

        super.replayAdditionalMockObjects();

        getProductsByCategoryIdTag.doTag();

        List<Product> list = (List<Product>) pageContext.getAttribute("productListVar");

        assert(list.get(0).equals(p1));
        assert(list.get(1).equals(p2));

        super.verifyBaseMockObjects();*/
    }

    @Override
    public void setup() {
        getProductsByCategoryIdTag = new GetProductsByCategoryIdTag();
    }
}
