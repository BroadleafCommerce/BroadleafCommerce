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

import javax.servlet.jsp.JspException;

import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.catalog.web.taglib.CategoryTag;
import org.easymock.classextension.EasyMock;

public class CategoryTagTest extends BaseTagLibTest {
    private CategoryTag categoryTag;
    private Category category;

    public void setUp() {
        categoryTag = new CategoryTag();
        category = EasyMock.createMock(Category.class);
    }

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
}
