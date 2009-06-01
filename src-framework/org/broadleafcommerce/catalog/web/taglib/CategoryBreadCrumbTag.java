package org.broadleafcommerce.catalog.web.taglib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.apache.log4j.Logger;
import org.broadleafcommerce.catalog.domain.Category;

public class CategoryBreadCrumbTag extends CategoryLinkTag {
    private Logger log = Logger.getLogger(this.getClass());
    private static final long serialVersionUID = 1L;

    private Long categoryId;
    private List<Category> categoryList = new ArrayList<Category>();

    @Override
    public void doTag() throws JspException, IOException {
        if (categoryId == null && categoryList == null) {
            throw new RuntimeException("Either categoryId or categoryList is required for this tag");
        }

        if (categoryId != null) {
            Category category = this.getCatalogService().findCategoryById(categoryId);

            if (category == null && log.isDebugEnabled()){
                log.debug("The category returned was null for categoryId: " + categoryId);
            }

            while (category != null) {
                categoryList.add(category);
                category = category.getDefaultParentCategory();
            }

            Collections.reverse(categoryList);
        }

        JspWriter out = getJspContext().getOut();
        int count = 0;
        for (Category cat : categoryList) {
            out.println(getUrl(cat));

            if (count < categoryList.size() - 1) {
                out.println(" > ");
            }

            ++count;
        }
    }

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

}
