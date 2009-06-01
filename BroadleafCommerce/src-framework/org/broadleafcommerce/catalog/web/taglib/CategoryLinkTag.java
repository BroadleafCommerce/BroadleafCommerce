package org.broadleafcommerce.catalog.web.taglib;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;
import org.broadleafcommerce.catalog.domain.Category;

public class CategoryLinkTag extends AbstractCatalogTag {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(CategoryLinkTag.class);

    private Category category;

    @Override
    public void doTag() throws JspException, IOException {
        JspWriter out = getJspContext().getOut();

        if (category != null) out.println(getUrl(category));

        super.doTag();
    }

    public Category getCategory() {
        return this.category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    protected String getUrl(Category category) {
        PageContext pageContext = (PageContext)getJspContext();
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
        StringBuffer sb = new StringBuffer();
        sb.append("<a href=\"");
        sb.append(request.isSecure() ? "https://" : "http://");
        sb.append(request.getServerName());
        sb.append(request.getLocalPort() != 80 ? ":" + request.getLocalPort() : "");
        sb.append(request.getContextPath());
        sb.append("/");
        sb.append(category.getGeneratedUrl());
        sb.append("\">");
        sb.append(category.getName());
        sb.append("</a>");

        return sb.toString();
    }

}
