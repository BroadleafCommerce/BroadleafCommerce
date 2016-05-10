/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.web.catalog.taglib;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.catalog.domain.Category;

import javax.servlet.jsp.JspException;

public class CategoryTag extends AbstractCatalogTag {

    private static final Log LOG = LogFactory.getLog(CategoryTag.class);
    private static final long serialVersionUID = 1L;
    private String var;

    private long categoryId;

    @Override
    public void doTag() throws JspException {
        catalogService = super.getCatalogService();

        Category category = catalogService.findCategoryById(categoryId);

        if(category == null && LOG.isDebugEnabled()){
            LOG.debug("The category returned was null for categoryId: " + categoryId);
        }

        getJspContext().setAttribute(var, category);
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
