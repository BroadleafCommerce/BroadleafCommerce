/*
 * #%L
 * BroadleafCommerce Framework Web
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
package org.broadleafcommerce.core.web.catalog.taglib;

import org.broadleafcommerce.core.catalog.domain.Category;
import org.easymock.classextension.EasyMock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

public class CategoryBreadcrumbTagTest extends BaseTagLibTest {
    
    private CategoryBreadCrumbTag categoryBreadcrumbTag;
    private Category category;
    private JspWriter writer;

    public void test_Breadcrumb() throws JspException, IOException {
        List<Category> categoryList = new ArrayList<Category>();

        Category defaultParentCategory = EasyMock.createMock(Category.class);
        categoryList.add(category);
        categoryList.add(defaultParentCategory);

        //pageContext.setAttribute("crumbVar", categoryList);

        categoryBreadcrumbTag.setCategoryId(0L);
        EasyMock.expect(catalogService.findCategoryById(0L)).andReturn(category);
        EasyMock.expect(category.getDefaultParentCategory()).andReturn(defaultParentCategory);
        EasyMock.expect(defaultParentCategory.getDefaultParentCategory()).andReturn(null);
        EasyMock.expect(pageContext.getRequest()).andReturn(request).anyTimes();
        EasyMock.expect(pageContext.getOut()).andReturn(writer).anyTimes();
        EasyMock.expect(request.isSecure()).andReturn(false).anyTimes();
        EasyMock.expect(request.getServerName()).andReturn("test").anyTimes();
        EasyMock.expect(request.getLocalPort()).andReturn(80).anyTimes();
        EasyMock.expect(request.getContextPath()).andReturn("myApp").anyTimes();
        EasyMock.expect(category.getGeneratedUrl()).andReturn("url").anyTimes();
        EasyMock.expect(category.getName()).andReturn("name").anyTimes();
        EasyMock.expect(defaultParentCategory.getGeneratedUrl()).andReturn("url").anyTimes();
        EasyMock.expect(defaultParentCategory.getName()).andReturn("name").anyTimes();

        categoryBreadcrumbTag.setCategoryList(categoryList);
        categoryBreadcrumbTag.setJspContext(pageContext);
        categoryBreadcrumbTag.setCatalogService(catalogService);

        super.replayAdditionalMockObjects(category, defaultParentCategory);

        assert(categoryList.get(1).equals(defaultParentCategory));
        assert(categoryList.get(0).equals(category));

        categoryBreadcrumbTag.doTag();

        assert(categoryList.get(0).equals(defaultParentCategory));
        assert(categoryList.get(1).equals(category));

        super.verifyBaseMockObjects(category, defaultParentCategory);
    }

    @Override
    public void setup() {
        categoryBreadcrumbTag = new CategoryBreadCrumbTag();
        category = EasyMock.createMock(Category.class);
        writer = EasyMock.createNiceMock(JspWriter.class);
    }

}
