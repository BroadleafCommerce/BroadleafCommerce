/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License” located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License” located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.web.catalog.taglib;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.Product;

import java.util.List;

import javax.servlet.jsp.JspException;

/**
 * This class is for demonstration purposes only. It contains a call to
 * catalogService.findActiveProductsByCategory, which may return a large list. A
 * more performant solution would be to utilize data paging techniques.
 */
public class GetProductsByCategoryIdTag extends AbstractCatalogTag {

    private static final Log LOG = LogFactory.getLog(GetProductsByCategoryIdTag.class);
    private static final long serialVersionUID = 1L;
    private String var;
    private long categoryId;

    @Override
    public void doTag() throws JspException {
        catalogService = super.getCatalogService();

        Category c = catalogService.findCategoryById(categoryId);

        if(c == null){
            getJspContext().setAttribute(var, null);

            if(LOG.isDebugEnabled()){
                LOG.debug("The category returned was null for categoryId: " + categoryId);
            }
        }

        List<Product> productList = catalogService.findActiveProductsByCategory(c);

        if(CollectionUtils.isEmpty(productList) && LOG.isDebugEnabled()){
            LOG.debug("The productList returned was null for categoryId: " + categoryId);
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
