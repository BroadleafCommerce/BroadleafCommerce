package org.broadleafcommerce.catalog.web.taglib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.jsp.JspException;

import org.apache.log4j.Logger;
import org.broadleafcommerce.catalog.domain.Category;

public class CategoryBreadcrumbTag extends AbstractCatalogTag {
    private Logger log = Logger.getLogger(this.getClass());
    private static final long serialVersionUID = 1L;

    private String var;
    private long categoryId;
    private List<Category> categoryList = new ArrayList<Category>();

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    @Override
    public int doStartTag() throws JspException {
        // TODO: Add debug logging
        catalogService = super.getCatalogService(pageContext);

        Category category = catalogService.findCategoryById(categoryId);

        if(category == null && log.isDebugEnabled()){
            log.debug("The category returned was null for categoryId: " + categoryId);
        }

        while (category != null) {
            categoryList.add(category);
            category = category.getDefaultParentCategory();
        }

        Collections.reverse(categoryList);
        pageContext.setAttribute(var, categoryList);

        return EVAL_PAGE;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public String getVar() {
        return var;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public void setVar(String var) {
        this.var = var;
    }
}
