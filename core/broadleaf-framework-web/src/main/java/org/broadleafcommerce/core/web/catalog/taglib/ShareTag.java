/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
