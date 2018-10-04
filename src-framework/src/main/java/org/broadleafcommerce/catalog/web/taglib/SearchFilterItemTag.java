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
import java.util.HashMap;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.util.money.Money;

/**
 * <p>The SearchFilterItemTag renders form elements designed to help filter a list of products. There
 * are two different filter options currently implemented: multiSelect and sliderRange.</p>
 * <p>multiSelect, the default displayType, renders an unordered list of the unique values for properties.
 * Each item consists of a checkbox, a string containing either the string representation of the property
 * or, if set, the propertyDisplay property of a product. Javascript is also rendered that makes clicking on
 * the strings check the corresponding checkbox as well as apply the css class 'searchFilterDisabledSelect'
 * to unchecked options.</p>
 * <p>sliderRange relies on the designated property being of type {@link Money} and renders a jQuery slider with
 * minimum and maximum values corresponding to the minimum and maximum values of the property. The slider renders
 * with javascript that causes 2 text input boxes to be updated with the values of the slider after each change.</p>
 * <p>After all changes, the javascript function updateSearchFilterResults will be called, this funciton should
 * be defined before the SearchFilterTag.</p>
 * 
 */
public class SearchFilterItemTag extends SimpleTagSupport {

    protected String property;
    protected String propertyDisplay;

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

        BeanToPropertyValueTransformer valueTransformer = new BeanToPropertyValueTransformer(property, true);
        BeanToPropertyValueTransformer displayTransformer;
        if (propertyDisplay != null) {
            displayTransformer = new BeanToPropertyValueTransformer(propertyDisplay, true);
        } else {
            displayTransformer = valueTransformer;
        }

        HashMap<Object, Integer> countMap = new HashMap<Object, Integer>();
        HashMap<Object, Object> valueDisplayMap = new HashMap<Object, Object>();
        for (Product product : products) {
            Object value = valueTransformer.transform(product);
            Object display = displayTransformer.transform(product);
            valueDisplayMap.put(value, display);
            Integer integer = countMap.get(value);
            if (integer == null) {
                countMap.put(value, new Integer(1));
            } else {
                countMap.put(value, new Integer(integer + 1));
            }
        }

        String propertyCss = property.replaceAll("[\\.\\[\\]]", "_");
        out.println("<ul class='searchFilter-"+propertyCss+"'>");
        for (Object value : countMap.keySet()) {
            Object display = valueDisplayMap.get(value);
            out.println("<li value='"+ value +"'><input type='checkbox' class='searchFilter-"+propertyCss+"Checkbox' name='"+property+"' value='" + value + "'/> " +
                    display + " <span class='searchFilter"+propertyCss+"-count'>(" + countMap.get(value).toString() + ")</span></li>");
        }
        out.println("</ul>");


        out.println("<script>" +
                " var " + propertyCss + "Checked = 0;\r\n" +
                "    \r\n" +
                "    $('.searchFilter-" + propertyCss + " li').click(function() {\r\n" +
                "        var value = $(this).attr('value');\r\n" +
                "        var checkbox = $(this).find(':checkbox');\r\n" +
                "        if (" + propertyCss + "Checked == 0) {\r\n" +
                "            $('.searchFilter-" + propertyCss + " li').each(function(){$(this).addClass('searchFilterDisabledSelect')});\r\n" +
                "            $(this).removeClass('searchFilterDisabledSelect');\r\n" +
                "            checkbox.attr('checked',true);\r\n" +
                "            " + propertyCss + "Checked++;\r\n" +
                "        } else if (checkbox.attr('checked') == true) {\r\n" +
                "            $(this).addClass('searchFilterDisabledSelect');\r\n" +
                "            if (" + propertyCss + "Checked == 1) {\r\n" +
                "                // unchecking the only checked category, so reactivate all categories\r\n" +
                "                $('.searchFilter-"+propertyCss+" li').each(function(){$(this).removeClass('searchFilterDisabledSelect')});\r\n" +
                "            } \r\n" +
                "            checkbox.attr('checked',false);\r\n" +
                "            " + propertyCss + "Checked--;\r\n" +
                "        } else {\r\n" +
                "            $(this).removeClass('searchFilterDisabledSelect');\r\n" +
                "            checkbox.attr('checked',true);\r\n" +
                "            " + propertyCss + "Checked++;\r\n" +
                "        }\r\n" +
                "        updateSearchFilterResults();\r\n" +
                "    } );" +
        "</script>");
    }

    private void doSliderRange(JspWriter out)  throws JspException, IOException {
        List<Product> products = ((SearchFilterTag) getParent()).getProducts();

        Money min = null;
        Money max = null;
        BeanToPropertyValueTransformer valueTransformer = new BeanToPropertyValueTransformer(property, true);

        for (Product product : products) {
            Money propertyObject = (Money) valueTransformer.transform(product);
            min = propertyObject.min(min);
            max = propertyObject.max(max);
        }

        String propertyCss = property.replaceAll("[.\\[\\]]", "_");

        out.println("<div id='searchFilter-"+propertyCss+"'></div>");
        out.println("Range:");
        out.println("<input type=\"text\" id=\"min-" + propertyCss + "\" name='min-" + property + "' value='$"+min.getAmount().toPlainString()+"'/> - ");
        out.println("<input type=\"text\" id=\"max-" + propertyCss + "\" name='max-" + property + "' value='$"+max.getAmount().toPlainString()+"'/> <br/>");

        out.println("        <script type=\"text/javascript\">\r\n" +
                "        $(function() {\r\n" +
                "            $(\"#searchFilter-" + propertyCss + "\").slider({\r\n" +
                "                range: true,\r\n" +
                "                min: "+ min.getAmount().toPlainString() +", max: "+ max.getAmount().toPlainString() + "," +
                "                values: ["+ min.getAmount().toPlainString() +","+ max.getAmount().toPlainString() +"]," +
                "                slide: function(event, ui) {\r\n" +
                "                    $(\"#min-" + propertyCss + "\").val('$' + ui.values[0] );\r\n" +
                "                    $(\"#max-" + propertyCss + "\").val('$' + ui.values[1]);\r\n" +
                "                }\r\n" +
                "            });\r\n" +
                "        });\r\n" +
                "        $('#searchFilter-"+propertyCss+"').bind('slidechange',  updateSearchFilterResults); \r\n" +
        "        </script>");
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
}
