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
import javax.servlet.jsp.JspWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CategoryBreadCrumbTag extends CategoryLinkTag {

    private static final Log LOG = LogFactory.getLog(CategoryBreadCrumbTag.class);
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

            if (category == null && LOG.isDebugEnabled()){
                LOG.debug("The category returned was null for categoryId: " + categoryId);
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
