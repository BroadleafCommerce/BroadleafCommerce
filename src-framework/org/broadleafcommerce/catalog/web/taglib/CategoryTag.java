package org.broadleafcommerce.catalog.web.taglib;

import javax.servlet.jsp.JspException;

import org.apache.log4j.Logger;
import org.broadleafcommerce.catalog.domain.Category;

public class CategoryTag extends AbstractCatalogTag {
    private Logger log = Logger.getLogger(this.getClass());
    private static final long serialVersionUID = 1L;
    private String var;

    private long categoryId;

    @Override
    public int doStartTag() throws JspException {
        catalogService = super.getCatalogService(pageContext);

        Category category = catalogService.findCategoryById(categoryId);

        if(category == null && log.isDebugEnabled()){
            log.debug("The category returned was null for categoryId: " + categoryId);
        }

        pageContext.setAttribute(var, category);
        return EVAL_PAGE;
    }

    public String getVar() {
        return var;
    }

    public void setVar(String var) {
        this.var = var;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public void release(){
        var = null;
        categoryId = 0L;
    }

}
