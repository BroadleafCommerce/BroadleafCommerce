package org.broadleafcommerce.taglib.test;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;

import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.catalog.web.taglib.CategoryBreadcrumbTag;
import org.easymock.classextension.EasyMock;

public class CategoryBreadcrumbTagTest extends BaseTagLibTest {
    private CategoryBreadcrumbTag categoryBreadcrumbTag;
    private Category category;

    public void setUp() {
        categoryBreadcrumbTag = new CategoryBreadcrumbTag();
        category = EasyMock.createMock(Category.class);
    }

    public void test_Breadcrumb() throws JspException {
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
        categoryBreadcrumbTag.setPageContext(pageContext);
        categoryBreadcrumbTag.setVar("crumbVar");
        categoryBreadcrumbTag.setCatalogService(catalogService);

        super.replayAdditionalMockObjects(category, defaultParentCategory);

        assert(categoryList.get(1).equals(defaultParentCategory));
        assert(categoryList.get(0).equals(category));

        categoryBreadcrumbTag.doStartTag();

        assert(categoryList.get(0).equals(defaultParentCategory));
        assert(categoryList.get(1).equals(category));

        super.verifyBaseMockObjects(category, defaultParentCategory);
    }

}
