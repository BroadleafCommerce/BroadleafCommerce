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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.broadleafcommerce.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.profile.domain.Address;

public class GoogleAnalyticsTag extends SimpleTagSupport {

    private static final long serialVersionUID = 1L;
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
        out.println(analytics(webPropertyId, order));
        super.doTag();
    }

    protected String analytics(String webPropertyId, Order order) {
        StringBuffer sb = new StringBuffer();

        if (order != null) {
            sb.append("<script type=\"text/javascript\">");
            sb.append("var gaJsHost = ((\"https:\" == document.location.protocol) ? \"https://ssl.\" : \"http://www.\");");
            sb.append("document.write(unescape(\"%3Cscript src='\" + gaJsHost + \"google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E\"))");
            sb.append("</script> <script type=\"text/javascript\">");
            sb.append(" try { var pageTracker = _gat._getTracker(\"" + webPropertyId + "\");");
            sb.append("pageTracker._trackPageview();");

            Address paymentAddress = order.getPaymentInfos().get(0).getAddress();

            sb.append("pageTracker._addTrans(" + order.getId() + "," + order.getName() + "," +
                    order.getTotal() + "," + order.getTotalTax() + "," + order.getTotalShipping() + "," +
                    paymentAddress.getCity() + "," + paymentAddress.getState().getName() + "," +
                    paymentAddress.getCountry().getName() + ");" );

            for (FulfillmentGroup fulfillmentGroup : order.getFulfillmentGroups()) {
                for (FulfillmentGroupItem fulfillmentGroupItem : fulfillmentGroup.getFulfillmentGroupItems()) {
                    DiscreteOrderItem orderItem = (DiscreteOrderItem) fulfillmentGroupItem.getOrderItem();
                    sb.append("pageTracker._addItem(" + order.getId() + "," + orderItem.getSku().getId() + "," +
                            orderItem.getSku().getName() + "," + null + "," +
                            orderItem.getPrice() + "," + orderItem.getQuantity() + ");" );
                }
            }

            sb.append("pageTracker._trackTrans();");
            sb.append("} catch(err) {}</script>");
        }
        else {
            sb.append("<script type=\"text/javascript\">");
            sb.append("var gaJsHost = ((\"https:\" == document.location.protocol) ? \"https://ssl.\" : \"http://www.\");");
            sb.append("document.write(unescape(\"%3Cscript src='\" + gaJsHost + \"google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E\"))");
            sb.append("</script> <script type=\"text/javascript\">");
            sb.append(" try { var pageTracker = _gat._getTracker(\"" + webPropertyId + "\");");
            sb.append("pageTracker._trackPageview();");
            sb.append("} catch(err) {}</script>");
        }

        return sb.toString();
    }
}