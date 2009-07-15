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
import java.util.Hashtable;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.catalog.domain.Sku;
import org.broadleafcommerce.util.money.Money;

public class SearchFilterTag extends AbstractCatalogTag {

    private List<Product> products;
    private String queryString;

    @Override
    public void doTag() throws JspException, IOException {
        JspWriter out = getJspContext().getOut();
        Hashtable<Category, Integer> categories = new Hashtable<Category, Integer>();
        if (products == null || products.size() == 0) { return; }
        Money minPrice = null;
        Money maxPrice = null;
        for (Product product : products) {
            for (Sku sku : product.getSkus()) {
                if (sku.getSalePrice() != null) {
                    minPrice = sku.getSalePrice().min(minPrice);
                    maxPrice = sku.getSalePrice().max(maxPrice);
                }
            }
            Integer integer = categories.get(product.getDefaultCategory());
            if (integer == null) {
                categories.put(product.getDefaultCategory(), new Integer(1));
            } else {
                categories.put(product.getDefaultCategory(), new Integer(integer + 1));
            }
        }
        out.println("<h3>Your Search</h3>");
        out.println("<input type=\"text\" size=\"30\" class=\"searchQuery\" name=\"queryString\" id=\"queryString\" value='"+queryString+"' />");
        out.println("<input type=\"hidden\" size=\"30\" name=\"originalQueryString\" id=\"originalQueryString\" value='"+queryString+"' />");
        out.println("<h3>Categories</h3>");
        out.println("<ul class='skuFilterCategories'>");
        for (Category category : categories.keySet()) {
            out.println("<li value='"+ category.getId() +"'><input type='checkbox' class='skuFilterCategoryCheckbox' name='categoryId' value='" + category.getId() + "'/> " + category.getName()
                    + " (" + categories.get(category).toString() + ")</li>");
        }
        out.println("</ul>");

        out.println("<h3>Price</h3>");
        out.println("<div id='skuFilterPrice'></div>");
        out.println("Price Range:");
        out.println("<input type=\"text\" id=\"minPrice\" name='minPrice' value='$"+minPrice.getAmount().toPlainString()+"'/> - ");
        out.println("<input type=\"text\" id=\"maxPrice\" name='maxPrice' value='$"+maxPrice.getAmount().toPlainString()+"'/> <br/>");

        out.println("        <script type=\"text/javascript\">\r\n" +
                "        $(function() {\r\n" +
                "            $(\"#skuFilterPrice\").slider({\r\n" +
                "                range: true,\r\n" +
                "                min: "+ minPrice.getAmount().toPlainString() +", max: "+ maxPrice.getAmount().toPlainString() + "," +
                "                values: ["+ minPrice.getAmount().toPlainString() +","+ maxPrice.getAmount().toPlainString() +"]," +
                "                slide: function(event, ui) {\r\n" +
                "                    $(\"#minPrice\").val('$' + ui.values[0] );\r\n" +
                "                    $(\"#maxPrice\").val('$' + ui.values[1]);\r\n" +
                "                }\r\n" +
                "            });\r\n" +
                "        });\r\n" +
        "        </script>");

        //"            $(\"#amount\").val('$' + $(\"#skuFilterPrice\").slider(\"values\", 0) + ' - $' + $(\"#slider-range\").slider(\"values\", 1));\r\n" +


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
