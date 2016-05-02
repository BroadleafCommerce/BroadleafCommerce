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

import org.broadleafcommerce.core.catalog.domain.Product;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

public class ProductLinkTag extends CategoryLinkTag {

    private static final long serialVersionUID = 1L;

    private Product product;

    @Override
    public void doTag() throws JspException, IOException {
        JspWriter out = getJspContext().getOut();
        out.println(getUrl(product));
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    protected String getUrl(Product product) {
        PageContext pageContext = (PageContext)getJspContext();
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
        StringBuffer sb = new StringBuffer();
        sb.append("<a class=\"noTextUnderline\"  href=\"");
        sb.append(request.getContextPath());
        sb.append("/");
        sb.append(getCategory() == null ? product.getDefaultCategory().getGeneratedUrl() : getCategory().getGeneratedUrl());
        sb.append("?productId=" + product.getId());
        sb.append("\">");
        sb.append(product.getName());
        sb.append("</a>");

        return sb.toString();
    }

}
