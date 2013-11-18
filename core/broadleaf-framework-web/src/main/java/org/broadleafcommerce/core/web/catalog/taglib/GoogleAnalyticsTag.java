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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.profile.core.domain.Address;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

public class GoogleAnalyticsTag extends SimpleTagSupport {
    
    private static final Log LOG = LogFactory.getLog(GoogleAnalyticsTag.class);
    
    @Value("${googleAnalytics.webPropertyId}")
    private String webPropertyId;
    
    private Order order;

    public void setOrder(Order order) {
        this.order = order;
    }

    public void setWebPropertyId(String webPropertyId) {
        this.webPropertyId = webPropertyId;
    }

    @Override
    public void doTag() throws JspException, IOException {
        JspWriter out = getJspContext().getOut();
        
        if (webPropertyId == null) {
            ServletContext sc = ((PageContext) getJspContext()).getServletContext();
            ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(sc);
            context.getAutowireCapableBeanFactory().autowireBeanProperties(this, AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, false);
        }
        
        if (webPropertyId.equals("UA-XXXXXXX-X")) {
            LOG.warn("googleAnalytics.webPropertyId has not been overridden in a custom property file. Please set this in order to properly use the Google Analytics tag");
        }
        
        out.println(analytics(webPropertyId, order));
        super.doTag();
    }

    /**
     * Documentation for the recommended asynchronous GA tag is at:
     * http://code.google.com/apis/analytics/docs/tracking/gaTrackingEcommerce.html
     * 
     * @param webPropertyId - Google Analytics ID
     * @param order - optionally track the order submission. This should be included on the
     * page after the order has been sucessfully submitted. If null, this will just track the current page
     * @return the relevant Javascript to render on the page
     */
    protected String analytics(String webPropertyId, Order order) {
        StringBuffer sb = new StringBuffer();
        
        sb.append("<script type=\"text/javascript\">");
        sb.append("var _gaq = _gaq || [];");
        sb.append("_gaq.push(['_setAccount', '" + webPropertyId + "']);");
        sb.append("_gaq.push(['_trackPageview']);");
        
        if (order != null) {
            Address paymentAddress = order.getPaymentInfos().get(0).getAddress();

            sb.append("_gaq.push(['_addTrans','" + order.getId() + "'");
            sb.append(",'" + order.getName() + "'");
            sb.append(",'" + order.getTotal() + "'");
            sb.append(",'" + order.getTotalTax() + "'");
            sb.append(",'" + order.getTotalShipping() + "'");
            sb.append(",'" + paymentAddress.getCity() + "'");
            sb.append(",'" + paymentAddress.getState().getName() + "'");
            sb.append(",'" + paymentAddress.getCountry().getName() + "'");
            sb.append("]);");

            for (FulfillmentGroup fulfillmentGroup : order.getFulfillmentGroups()) {
                for (FulfillmentGroupItem fulfillmentGroupItem : fulfillmentGroup.getFulfillmentGroupItems()) {
                    DiscreteOrderItem orderItem = (DiscreteOrderItem) fulfillmentGroupItem.getOrderItem();
                    sb.append("_gaq.push(['_addItem','" + order.getId() + "'");
                    sb.append(",'" + orderItem.getSku().getId() + "'");
                    sb.append(",'" + orderItem.getSku().getName() + "'");
                    sb.append(",' " + orderItem.getProduct().getDefaultCategory() + "'");
                    sb.append(",'" + orderItem.getPrice() + "'");
                    sb.append(",'" + orderItem.getQuantity() + "'");
                    sb.append("]);");
                }
            }
            sb.append("_gaq.push(['_trackTrans']);");
        }
        
        sb.append(" (function() {"
                + "var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;"
                + "ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';"
                + "var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);"
                + "})();");
        sb.append("</script>");

        return sb.toString();
    }
    
}