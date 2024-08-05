/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.web.processor;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.payment.PaymentType;
import org.broadleafcommerce.common.util.BLCSystemProperty;
import org.broadleafcommerce.common.web.dialect.AbstractModelVariableModifierProcessor;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.order.domain.BundleOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.domain.OrderItemAttribute;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.payment.domain.OrderPayment;
import org.broadleafcommerce.profile.core.domain.Address;
import org.springframework.beans.factory.annotation.Value;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;

import java.util.Map;

import javax.annotation.Resource;

/**
 * A Thymeleaf processor that will output Google Analytics tracking Javascript. When used on an order confirmation page
 * in conjunction with an <b>orderNumber</b> this will also output the ecommerce transaction tracking parameters for
 * Google Analytics
 * 
 * Example usage on order confirmation page:
 * <pre>
 *  {@code
 *      <blc:googleanalytics th:attr="orderNumber=${order != null ? order.orderNumber : null}" />
 *      <script th:utext="${analytics}" />
 *  }
 * </pre>
 * 
 * @param ordernumber the order number of the submitted order
 * 
 * @author tleffert
 * @deprecated use the {@link GoogleUniversalAnalyticsProcessor} instead
 */
@Deprecated
public class GoogleAnalyticsProcessor extends AbstractModelVariableModifierProcessor {

    @Resource(name = "blOrderService")
    protected OrderService orderService;

    protected String affiliation;

    protected String getWebPropertyId() {
        return BLCSystemProperty.resolveSystemProperty("googleAnalytics.webPropertyId");
    }

    protected String getAffiliationDefault() {
        return BLCSystemProperty.resolveSystemProperty("googleAnalytics.affiliation");
    }
    
    @Value("${googleAnalytics.testLocal}")
    protected boolean testLocal = false;

    /**
     * Sets the name of this processor to be used in Thymeleaf template
     */
    public GoogleAnalyticsProcessor() {
        super("googleanalytics");
    }

    @Override
    public int getPrecedence() {
        return 100000;
    }

    @Override
    protected void modifyModelAttributes(Arguments arguments, Element element) {

        String orderNumber = element.getAttributeValue("orderNumber");
        Order order = null;
        if (orderNumber != null) {
            order = orderService.findOrderByOrderNumber(orderNumber);
        }
        addToModel(arguments, "analytics", analytics(getWebPropertyId(), order));
    }

    /**
     * Documentation for the recommended asynchronous GA tag is at:
     * http://code.google.com/apis/analytics/docs/tracking/gaTrackingEcommerce.html
     * 
     * @param webPropertyId
     *            - Google Analytics ID
     * @param order
     *            - optionally track the order submission. This should be
     *            included on the page after the order has been sucessfully
     *            submitted. If null, this will just track the current page
     * @return the relevant Javascript to render on the page
     */
    protected String analytics(String webPropertyId, Order order) {
        StringBuffer sb = new StringBuffer();

        sb.append("var _gaq = _gaq || [];\n");
        sb.append("_gaq.push(['_setAccount', '" + webPropertyId + "']);");

        sb.append("_gaq.push(['_trackPageview']);");
        
        if (testLocal) {
            sb.append("_gaq.push(['_setDomainName', '127.0.0.1']);");
        }
        
        if (order != null) {
            Address paymentAddress = getBillingAddress(order);
            if (paymentAddress != null) {
                sb.append("_gaq.push(['_addTrans','" + order.getOrderNumber() + "'");
                sb.append(",'" + getAffiliation() + "'");
                sb.append(",'" + order.getTotal() + "'");
                sb.append(",'" + order.getTotalTax() + "'");
                sb.append(",'" + order.getTotalShipping() + "'");
                sb.append(",'" + paymentAddress.getCity() + "'");

                String state = null;
                if (StringUtils.isNotBlank(paymentAddress.getStateProvinceRegion())) {
                    state = paymentAddress.getStateProvinceRegion();
                } else if (paymentAddress.getState() != null) {
                    state = paymentAddress.getState().getName();
                }

                String country = null;
                if (paymentAddress.getIsoCountryAlpha2() != null) {
                    country = paymentAddress.getIsoCountryAlpha2().getName();
                } else if (paymentAddress.getCountry() != null) {
                    country = paymentAddress.getCountry().getName();
                }

                if (state != null) {
                    sb.append(",'" + state + "'");
                }

                if (country != null) {
                    sb.append(",'" + country + "'");
                }

                sb.append("]);");
            }
            for (FulfillmentGroup fulfillmentGroup : order.getFulfillmentGroups()) {
                for (FulfillmentGroupItem fulfillmentGroupItem : fulfillmentGroup.getFulfillmentGroupItems()) {
                    OrderItem orderItem = fulfillmentGroupItem.getOrderItem();

                    Sku sku = null;
                    if (orderItem instanceof DiscreteOrderItem) {
                        sku = ((DiscreteOrderItem)orderItem).getSku();
                    } else if (orderItem instanceof BundleOrderItem) {
                        sku = ((BundleOrderItem)orderItem).getSku();
                    }
                    
                    sb.append("_gaq.push(['_addItem','" + order.getOrderNumber() + "'");
                    sb.append(",'" + sku.getId() + "'");
                    sb.append(",'" + sku.getName() + "'");
                    sb.append(",'" + getVariation(orderItem) + "'");
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

        return sb.toString();
    }
    
    /**
     * Returns the product option values separated by a space if they are
     * relevant for the item, or the product category if no options are available
     * 
     * @return
     */
    protected String getVariation(OrderItem item) {
        if (MapUtils.isEmpty(item.getOrderItemAttributes())) {
            return item.getCategory() == null ? "" : item.getCategory().getName();
        }
        
        //use product options instead
        String result = "";
        for (Map.Entry<String, OrderItemAttribute> entry : item.getOrderItemAttributes().entrySet()) {
            result += entry.getValue().getValue() + " ";
        }

        //the result has a space at the end, ensure that is stripped out
        return result.substring(0, result.length() - 1);
    }

    protected Address getBillingAddress(Order order) {
        Address address = null;
        for (OrderPayment payment : order.getPayments())  {
            if (payment.isActive() && PaymentType.CREDIT_CARD.equals(payment.getType())) {
                address = payment.getBillingAddress();
            }
        }

        return address;
    }
    
    protected void setTestLocal(boolean testLocal) {
        this.testLocal = testLocal;
    }
    
    public boolean getTestLocal() {
        return testLocal;
    }
    
    public String getAffiliation() {
        if (affiliation == null) {
            return getAffiliationDefault();
        } else {
            return affiliation;
        }
    }
    
    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

}
