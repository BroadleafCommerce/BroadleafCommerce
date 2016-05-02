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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

public class ShareTag extends SimpleTagSupport {

    private static final long serialVersionUID = 1L;

    @Override
    public void doTag() throws JspException, IOException {
        JspWriter out = getJspContext().getOut();
        out.println(share());
        super.doTag();
    }

    protected String share() {
        StringBuffer sb = new StringBuffer();
        sb.append("<a id=\"fbLink\" href=\"\">");
        sb.append("<img src=\"/broadleafdemo/images/share/link-facebook.gif\" />");
        sb.append("</a>");

        sb.append("<a id=\"diggLink\" href=\"\">");
        sb.append("<img src=\"/broadleafdemo/images/share/link-digg.gif\" />");
        sb.append("</a>");

        sb.append("<a id=\"deliciousLink\" href=\"\">");
        sb.append("<img src=\"/broadleafdemo/images/share/link-delicious.gif\" />");
        sb.append("</a>");

        return sb.toString();
    }

}
