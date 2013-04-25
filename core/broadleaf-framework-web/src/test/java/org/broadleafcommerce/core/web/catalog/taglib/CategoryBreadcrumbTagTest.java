/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.web.catalog.taglib;

import org.broadleafcommerce.core.catalog.domain.Category;
import org.easymock.classextension.EasyMock;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
