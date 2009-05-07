package org.broadleafcommerce.taglib.test;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;

import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.catalog.web.taglib.GetProductsByCategoryIdTag;
import org.easymock.classextension.EasyMock;

public class GetProductsByCategoryIdTagTest extends BaseTagLibTest {
    private GetProductsByCategoryIdTag getProductsByCategoryIdTag;

    public void setUp() {
        getProductsByCategoryIdTag = new GetProductsByCategoryIdTag();
    }

    public void test_GetProductsByCategoryIdTag() throws JspException {

        getProductsByCategoryIdTag.setPageContext(pageContext);
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
        EasyMock.expect(catalogService.findActiveProductsByCategory(c)).andReturn(productList);

        EasyMock.replay(p1, p2, c);

        super.replayAdditionalMockObjects();

        getProductsByCategoryIdTag.doStartTag();

        List<Product> list = (List<Product>) pageContext.getAttribute("productListVar");

        assert(list.get(0).equals(p1));
        assert(list.get(1).equals(p2));

        super.verifyBaseMockObjects();
    }
}
