/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.catalog.web.taglib;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.broadleafcommerce.catalog.domain.Product;

/**
 * The SearchFilterTag sets up an environment for it's children {@link SearchFilterItemTag}s. If a queryString
 * is passed in, the tag will render a textbox named queryString populated with the string passed in. It will also
 * render a hidden input containing the same value to aid in deciding whether to do ajax loads while filtering
 * search results or to do a full page refresh for new searches.
 *
 */
public class SearchFilterTag extends TagSupport {

    private static final long serialVersionUID = 1L;

    private List<Product> products;
    private String queryString;

    @Override
    public int doStartTag() throws JspException {
        JspWriter out = this.pageContext.getOut();
        if (products == null || products.size() == 0) { return SKIP_BODY; }

        if (queryString != null && !"".equals(queryString)) {
            try {
                out.println("<h3>Your Search</h3>");
                out.println("<input type=\"text\" size=\"30\" class=\"searchQuery\" name=\"queryString\" id=\"queryString\" value='"+queryString+"' />");
                out.println("<input type=\"hidden\" size=\"30\" name=\"originalQueryString\" id=\"originalQueryString\" value='"+queryString+"' />");
            } catch (IOException e) {
            }
        }

        return EVAL_BODY_INCLUDE;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }
}
