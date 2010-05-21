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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;

import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.catalog.web.taglib.GetProductsByCategoryIdTag;
import org.broadleafcommerce.time.SystemTime;
import org.easymock.classextension.EasyMock;

public class GetProductsByCategoryIdTagTest extends BaseTagLibTest {
    private GetProductsByCategoryIdTag getProductsByCategoryIdTag;

    public void setUp() {
        getProductsByCategoryIdTag = new GetProductsByCategoryIdTag();
    }

    @SuppressWarnings("unchecked")
    public void test_GetProductsByCategoryIdTag() throws JspException {

        getProductsByCategoryIdTag.setJspContext(pageContext);
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
        EasyMock.expect(catalogService.findActiveProductsByCategory(c, SystemTime.asDate())).andReturn(productList);

        EasyMock.replay(p1, p2, c);

        super.replayAdditionalMockObjects();

        getProductsByCategoryIdTag.doTag();

        List<Product> list = (List<Product>) pageContext.getAttribute("productListVar");

        assert(list.get(0).equals(p1));
        assert(list.get(1).equals(p2));

        super.verifyBaseMockObjects();
    }
}
