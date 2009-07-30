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
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.util.money.Money;

public class SearchFilterItemTag extends SimpleTagSupport {

    protected String property;
    protected String propertyDisplay;
    protected String propertyValue;

    protected String displayTitle;
    protected String displayType = "multiSelect";

    @Override
    public void doTag() throws JspException, IOException {

        JspWriter out = getJspContext().getOut();
        out.println("<h3>"+getDisplayTitle()+"</h3>");

        if (displayType.equals("multiSelect")) {
            doMultiSelect(out);
        } else if (displayType.equals("sliderRange")) {
            doSliderRange(out);
        }
        super.doTag();
    }

    private void doMultiSelect(JspWriter out) throws JspException, IOException {
        List<Product> products = ((SearchFilterTag) getParent()).getProducts();
        Class<Product> productClass = Product.class;

        Method propertyMethod;
        try {
            propertyMethod = productClass.getMethod(getterName(property), (Class[])null);
        } catch (NoSuchMethodException e1) {
            throw new JspException(e1);
        }
        Class<?> propertyClass = propertyMethod.getReturnType();

        HashMap<Object, Integer> countMap = new HashMap<Object, Integer>();
        for (Product product : products) {
            Object propertyObject;
            try {
                propertyObject = propertyMethod.invoke(product, (Object[])null);
            } catch (Exception e) {
                throw new JspException("Invalid propertyValue", e);
            }
            Integer integer = countMap.get(propertyObject);
            if (integer == null) {
                countMap.put(propertyObject, new Integer(1));
            } else {
                countMap.put(propertyObject, new Integer(integer + 1));
            }
        }

        Method displayMethod = null;
        Method valueMethod = null;
        try {
            displayMethod = propertyClass.getMethod(getterName(propertyDisplay), (Class[])null);
        } catch (NoSuchMethodException e) {
            throw new JspException("Invalid propertyDisplay", e);
        }
        try {
            valueMethod = propertyClass.getMethod(getterName(propertyValue), (Class[])null);
        } catch (NoSuchMethodException e) {
            throw new JspException("Invalid propertyValue", e);
        }
        out.println("<ul class='searchFilter-"+property+"'>");
        for (Object propertyObject : countMap.keySet()) {
            String display;
            String value;
            try {
                display = displayMethod.invoke(propertyObject, (Object[])null).toString();
                value = valueMethod.invoke(propertyObject, (Object[])null).toString();
            } catch (Exception e) {
                // This would happen if either a getter method or toString were to actually take an argument
                throw new JspException(e);
            }

            out.println("<li value='"+ value +"'><input type='checkbox' class='searchFilter-"+property+"Checkbox' name='"+property+"' value='" + value + "'/> " +
                    display + " <span class='searchFilter"+property+"-count'>(" + countMap.get(propertyObject).toString() + ")</span></li>");
        }
        out.println("</ul>");

        out.println("<script>" +
                " var " + property + "Checked = 0;\r\n" +
                "    \r\n" +
                "    $('.searchFilter-" + property + " li').click(function() {\r\n" +
                "        var value = $(this).attr('value');\r\n" +
                "        var checkbox = $(this).find(':checkbox');\r\n" +
                "        if (" + property + "Checked == 0) {\r\n" +
                "            $('.searchFilter-" + property + " li').each(function(){$(this).addClass('searchFilterDisabledSelect')});\r\n" +
                "            $(this).removeClass('searchFilterDisabledSelect');\r\n" +
                "            checkbox.attr('checked',true);\r\n" +
                "            " + property + "Checked++;\r\n" +
                "        } else if (checkbox.attr('checked') == true) {\r\n" +
                "            $(this).addClass('searchFilterDisabledSelect');\r\n" +
                "            if (" + property + "Checked == 1) {\r\n" +
                "                // unchecking the only checked category, so reactivate all categories\r\n" +
                "                $('.searchFilter-"+property+" li').each(function(){$(this).removeClass('searchFilterDisabledSelect')});\r\n" +
                "            } \r\n" +
                "            checkbox.attr('checked',false);\r\n" +
                "            " + property + "Checked--;\r\n" +
                "        } else {\r\n" +
                "            $(this).removeClass('searchFilterDisabledSelect');\r\n" +
                "            checkbox.attr('checked',true);\r\n" +
                "            " + property + "Checked++;\r\n" +
                "        }\r\n" +
                "        updateSearchFilterResults();\r\n" +
                "    } );" +
        "</script>");
    }

    private void doSliderRange(JspWriter out)  throws JspException, IOException {
        List<Product> products = ((SearchFilterTag) getParent()).getProducts();
        Class<Product> productClass = Product.class;

        Method propertyMethod;
        try {
            propertyMethod = productClass.getMethod(getterName(property), (Class[])null);
        } catch (NoSuchMethodException e1) {
            throw new JspException(e1);
        }
        Class<?> propertyClass = propertyMethod.getReturnType();
        if (!propertyClass.equals(Money.class)) {
            throw new JspException ("invalid property specified for SearchFilterItemTag, must be of type Money");
        }

        Money min = null;
        Money max = null;

        for (Product product : products) {
            Money propertyObject;
            try {
                propertyObject = (Money)propertyMethod.invoke(product, (Object[])null);
            } catch (Exception e) {
                throw new JspException("Invalid propertyValue", e);
            }
            min = propertyObject.min(min);
            max = propertyObject.max(max);
        }

        out.println("<div id='searchFilter-"+property+"'></div>");
        out.println("Range:");
        out.println("<input type=\"text\" id=\"min-" + property + "\" name='min-" + property + "' value='$"+min.getAmount().toPlainString()+"'/> - ");
        out.println("<input type=\"text\" id=\"max-" + property + "\" name='max-" + property + "' value='$"+max.getAmount().toPlainString()+"'/> <br/>");

        out.println("        <script type=\"text/javascript\">\r\n" +
                "        $(function() {\r\n" +
                "            $(\"#searchFilter-" + property + "\").slider({\r\n" +
                "                range: true,\r\n" +
                "                min: "+ min.getAmount().toPlainString() +", max: "+ max.getAmount().toPlainString() + "," +
                "                values: ["+ min.getAmount().toPlainString() +","+ max.getAmount().toPlainString() +"]," +
                "                slide: function(event, ui) {\r\n" +
                "                    $(\"#min-" + property + "\").val('$' + ui.values[0] );\r\n" +
                "                    $(\"#max-" + property + "\").val('$' + ui.values[1]);\r\n" +
                "                }\r\n" +
                "            });\r\n" +
                "        });\r\n" +
                "        $('#searchFilter-"+property+"').bind('slidechange',  updateSearchFilterResults); \r\n" +
        "        </script>");
    }

    private String getterName(String propertyName) {
        if (propertyName == null) return "toString";
        return "get" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
    }

    public String getProperty() {
        return property;
    }
    public void setProperty(String property) {
        this.property = property;
    }
    public String getDisplayType() {
        return displayType;
    }
    public void setDisplayType(String displayType) {
        this.displayType = displayType;
    }
    public String getDisplayTitle() {
        if (displayTitle==null) return property;
        return displayTitle;
    }
    public void setDisplayTitle(String displayTitle) {
        this.displayTitle = displayTitle;
    }
    public String getPropertyDisplay() {
        return propertyDisplay;
    }
    public void setPropertyDisplay(String propertyDisplay) {
        this.propertyDisplay = propertyDisplay;
    }
    public String getPropertyValue() {
        return propertyValue;
    }
    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }
}
