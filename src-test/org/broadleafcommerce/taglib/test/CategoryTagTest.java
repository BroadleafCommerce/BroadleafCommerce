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
