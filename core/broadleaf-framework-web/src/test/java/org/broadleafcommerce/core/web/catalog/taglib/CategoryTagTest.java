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

import org.broadleafcommerce.core.catalog.domain.Category;
import org.easymock.classextension.EasyMock;

import javax.servlet.jsp.JspException;

public class CategoryTagTest extends BaseTagLibTest {
    
    private CategoryTag categoryTag;
    private Category category;

    public void test_categoryTag() throws JspException {
        categoryTag.setJspContext(pageContext);
        categoryTag.setVar("categoryVar");
        categoryTag.setCatalogService(catalogService);

        pageContext.setAttribute("categoryVar", category);
        EasyMock.expect(pageContext.getAttribute("categoryVar")).andReturn(category);

        categoryTag.setCategoryId(0L);
        EasyMock.expect(catalogService.findCategoryById(0L)).andReturn(category);

        super.replayAdditionalMockObjects(category);

        categoryTag.doTag();

        Category ret = (Category) pageContext.getAttribute("categoryVar");

        assert(category.equals(ret));

        super.verifyBaseMockObjects(category);
    }

    @Override
    public void setup() {
        categoryTag = new CategoryTag();
        category = EasyMock.createMock(Category.class);
    }
}
