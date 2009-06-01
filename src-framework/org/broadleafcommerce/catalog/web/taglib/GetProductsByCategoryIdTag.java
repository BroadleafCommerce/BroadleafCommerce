package org.broadleafcommerce.catalog.web.taglib;

import java.util.List;

import javax.servlet.jsp.JspException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.catalog.domain.Product;

/*NOTE:
 * Was referred to as the ProductsTag.  Class name changed to be more descriptive
 */
public class GetProductsByCategoryIdTag extends AbstractCatalogTag {
    private Logger log = Logger.getLogger(this.getClass());
    private static final long serialVersionUID = 1L;
    private String var;
    private long categoryId;

    @Override
    public void doTag() throws JspException {
        catalogService = super.getCatalogService();

        Category c = catalogService.findCategoryById(categoryId);

        if(c == null){
            getJspContext().setAttribute(var, null);

            if(log.isDebugEnabled()){
                log.debug("The category returned was null for categoryId: " + categoryId);
            }
        }

        List<Product> productList = catalogService.findActiveProductsByCategory(c);

        if(CollectionUtils.isEmpty(productList) && log.isDebugEnabled()){
            log.debug("The productList returned was null for categoryId: " + categoryId);
        }

        getJspContext().setAttribute(var, productList);

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

}
