/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.taglib.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;

import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.catalog.web.taglib.CategoryBreadCrumbTag;
import org.easymock.classextension.EasyMock;

public class CategoryBreadcrumbTagTest extends BaseTagLibTest {
    private CategoryBreadCrumbTag categoryBreadcrumbTag;
    private Category category;

    public void setUp() {
        categoryBreadcrumbTag = new CategoryBreadCrumbTag();
        category = EasyMock.createMock(Category.class);
    }

    public void test_Breadcrumb() throws JspException, IOException {
        List<Category> categoryList = new ArrayList<Category>();

        Category defaultParentCategory = EasyMock.createMock(Category.class);
        categoryList.add(category);
        categoryList.add(defaultParentCategory);

        pageContext.setAttribute("crumbVar", categoryList);

        categoryBreadcrumbTag.setCategoryId(0L);
        EasyMock.expect(catalogService.findCategoryById(0L)).andReturn(category);

        EasyMock.expect(category.getDefaultParentCategory()).andReturn(defaultParentCategory);
        EasyMock.expect(defaultParentCategory.getDefaultParentCategory()).andReturn(null);

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

}
