/*-
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.util.BLCSystemProperty;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.domain.OrderItemAttribute;
import org.broadleafcommerce.core.order.domain.SkuAccessor;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.presentation.condition.ConditionalOnTemplating;
import org.broadleafcommerce.presentation.dialect.AbstractBroadleafTagReplacementProcessor;
import org.broadleafcommerce.presentation.model.BroadleafTemplateContext;
import org.broadleafcommerce.presentation.model.BroadleafTemplateElement;
import org.broadleafcommerce.presentation.model.BroadleafTemplateModel;
import org.broadleafcommerce.presentation.model.BroadleafTemplateNonVoidElement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * <p>
 * Takes advantage of the new-type gtag.js from Google Analytics rather than the deprecated analytics.js. This also
 * supports a pre-processed <b>orderNumber</b> attribute that can be null, suitable for things like the order confirmation
 * page to send e-commerce transactions. Example usage:
 *
 * <pre>
 * &lt;google_analytics_4 ordernumber="${order?.orderNumber" /&gt;
 * </pre>
 *
 * <p>
 * This processor also supports:
 * <ul>
 *  <li>Multiple trackers (extensible via {@link #getTrackers()} or by setting the {@code googleAnalytics.masterWebPropertyId}
 *      and {@code googleAnalytics.webPropertyId})</li>
 *  <li>Affiliates for e-commerce ({@ googleAnalytics.affiliation property})</li>
 *  <li><a href="https://support.google.com/analytics/answer/2558867?hl=en&utm_id=ad">Link attribution</a>
 *      ({@code googleAnalytics.enableLinkAttribution} system property, default {@code true})</li>
 *  <li><a href="https://support.google.com/analytics/answer/3450482">Display Advertising</a>
 *      ({@code googleAnalytics.enableDisplayAdvertising} system property, default {@code false})</li>
 * </ul>
 *
 * @param ordernumber the order number to look up for ecommerce tracking, such as on the confirmation page
 *
 * @author Markiian Buryi (MarekB01)
 */

@Component("blGoogleAnalytics4Processor")
@ConditionalOnTemplating
public class GoogleAnalytics4Processor extends AbstractBroadleafTagReplacementProcessor {

    private static final Log LOG = LogFactory.getLog(GoogleAnalytics4Processor.class);

    /**
     * Global value, intentionally only retrieved as a file property NOT via the system properties service
     */
    @Value("${googleAnalytics.masterWebPropertyId}")
    protected String masterWebPropertyId;

    @Resource(name = "blOrderService")
    protected OrderService orderService;

    /**
     * This will force the domain to 127.0.0.1 which is useful to determine if the Google Analytics tag is sending
     * a request to Google
     */
    @Value("${googleAnalytics.testLocal}")
    protected boolean testLocal = false;

    @Override
    public String getName() {
        return "google_analytics_4";
    }

    @Override
    public int getPrecedence() {
        return 0;
    }

    @Override
    public BroadleafTemplateModel getReplacementModel(String tagName, Map<String, String> tagAttributes, BroadleafTemplateContext context) {
        StringBuffer sb = new StringBuffer();
        Map<String, String> trackers = getTrackers();
        if (MapUtils.isNotEmpty(trackers)) {

            sb.append("async src=\"https://www.googletagmanager.com/gtag/js?id=UA-XXXXX-Y\"");
            sb.append("window.dataLayer = window.dataLayer || [];");
            sb.append("function gtag(){dataLayer.push(arguments);}");
            sb.append("gtag('js', new Date());");


            String orderNumberExpression = tagAttributes.get("ordernumber");
            String orderNumber = null;
            if (orderNumberExpression != null) {
                orderNumber = (String) context.parseExpression(orderNumberExpression);
            }

            Order order = null;
            if (orderNumber != null) {
                order = orderService.findOrderByOrderNumber(orderNumber);
            }

            for (Entry<String, String> tracker : trackers.entrySet()) {
                String trackerName = tracker.getKey();
                String trackerPrefix = "";
                String id = tracker.getValue();
                sb.append("gtag('config', '" + id + "'");

                if (!"webProperty".equals(trackerName)) {
                    trackerPrefix = trackerName + ".";
                    sb.append("'name': '" + trackerName + "'");
                    if (testLocal) {
                        sb.append(",");
                    }
                }
                if (testLocal) {
                    sb.append("'cookieDomain': 'none'");
                }
                sb.append("});");

                if ("webProperty".equals(trackerName)) {
                    HttpServletRequest request = BroadleafRequestContext.getBroadleafRequestContext().getRequest();
                    if (request != null) {
                        Map<String, String> setValuesMap = (Map<String, String>) request.getAttribute("blGAValuesMap");
                        if (setValuesMap != null) {
                            for (Map.Entry<String, String> entry : setValuesMap.entrySet()) {
                                sb.append("gtag('" + trackerPrefix + "set',").append(entry.getKey()).append(",")
                                        .append(entry.getValue()).append(");");
                            }
                        }
                    }
                }

                sb.append("gtag('" + trackerPrefix + "event', 'pageview');");

                if (isIncludeLinkAttribution()) {
                    sb.append(getLinkAttributionJs(trackerPrefix));
                }
                if (isIncludeDisplayAdvertising()) {
                    sb.append(getDisplayAdvertisingJs(trackerPrefix));
                }

                if (order != null) {
                    sb.append(getTransactionJs(order, trackerPrefix));
                }
            }

            // Add contentNode to the document
            BroadleafTemplateModel model = context.createModel();
            BroadleafTemplateNonVoidElement scriptTag = context.createNonVoidElement("script");
            BroadleafTemplateElement script = context.createTextElement(sb.toString());
            scriptTag.addChild(script);
            model.addElement(scriptTag);
            return model;
        } else {
            LOG.warn("No trackers were found, not outputting Google Analytics script. Set the googleAnalytics.webPropertyId"
                    + " and/or the googleAnalytics.masterWebPropertyId system properties to output Google Analytics");
        }
        return null;
    }

    /**
     * Grabs a map of trackers keyed by the tracker name with the analytics ID as the value
     */
    protected Map<String, String> getTrackers() {
        Map<String, String> trackers = new HashMap<>();
        if (shouldShowMasterTracker()) {
            trackers.put("master", getMasterWebPropertyId());
        }
        if (StringUtils.isNotBlank(getWebPropertyId())) {
            trackers.put("webProperty", getWebPropertyId());
        }

        return trackers;
    }

    protected boolean shouldShowMasterTracker() {
        String masterWebPropertyId = getMasterWebPropertyId();
        return (StringUtils.isNotBlank(masterWebPropertyId) && (!"UA-XXXXXXX-X".equals(masterWebPropertyId)));
    }

    /**
     * Builds the linke attribution Javascript
     * @param tracker the name of the tracker that is using the link attribution
     * @return
     */
    protected String getLinkAttributionJs(String trackerPrefix) {
        return "gtag('" + trackerPrefix + "require', 'linkid', 'linkid.js');";
    }

    /**
     * Builds the display advertising Javascript for the given tracker
     * @param tracker
     * @return
     */
    protected String getDisplayAdvertisingJs(String trackerPrefix) {
        return "gtag('" + trackerPrefix + "require', 'displayfeatures');";
    }

    /**
     * Builds the transaction analytics for the given tracker name. Invokes {@link #getItemJs(Order, String) for each item
     * in the given <b>order</b>.
     */
    protected String getTransactionJs(Order order, String trackerPrefix) {
        StringBuffer sb = new StringBuffer();
        sb.append("gtag('" + trackerPrefix + "require', 'ecommerce', 'ecommerce.js');");

        sb.append("gtag('" + trackerPrefix + "ecommerce:addTransaction', {");
        sb.append("'id': '" + order.getOrderNumber() + "'");
        if (StringUtils.isNotBlank(getAffiliation())) {
            sb.append(",'affiliation': '" + getAffiliation() + "'");
        }
        sb.append(",'revenue': '" + order.getTotal() + "'");
        sb.append(",'shipping':'" + order.getTotalShipping() + "'");
        sb.append(",'tax': '" + order.getTotalTax() + "'");

        if (order.getCurrency() != null) {
            sb.append(",'currency': '" + order.getCurrency().getCurrencyCode() + "'");
        }
        sb.append("});");

        sb.append(getItemJs(order, trackerPrefix));

        sb.append("gtag('" + trackerPrefix + "ecommerce:send');");
        return sb.toString();
    }

    protected String getItemJs(Order order, String trackerPrefix) {
        StringBuffer sb = new StringBuffer();
        for (FulfillmentGroup fulfillmentGroup : order.getFulfillmentGroups()) {
            for (FulfillmentGroupItem fulfillmentGroupItem : fulfillmentGroup.getFulfillmentGroupItems()) {
                OrderItem orderItem = fulfillmentGroupItem.getOrderItem();


                if (orderItem instanceof DiscreteOrderItem) {
                    if (SkuAccessor.class.isAssignableFrom(orderItem.getClass())) {
                        Sku sku = ((SkuAccessor) orderItem).getSku();
                        sb.append("gtag('" + trackerPrefix + "ecommerce:addItem', {");
                        sb.append("'id': '" + order.getOrderNumber() + "'");
                        sb.append(",'name': '" + sku.getName() + "'");
                        sb.append(",'sku': '" + sku.getId() + "'");
                        sb.append(",'category': '" + getVariation(orderItem) + "'");
                        sb.append(",'price': '" + orderItem.getAveragePrice() + "'");
                        sb.append(",'quantity': '" + orderItem.getQuantity() + "'");
                        sb.append("});");
                    }
                }
            }
        }
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

    public String getMasterWebPropertyId() {
        return masterWebPropertyId;
    }

    public void setMasterWebPropertyId(String masterWebPropertyId) {
        this.masterWebPropertyId = masterWebPropertyId;
    }

    public String getAffiliation() {
        return BLCSystemProperty.resolveSystemProperty("googleAnalytics.affiliation");
    }

    public String getWebPropertyId() {
        return BLCSystemProperty.resolveSystemProperty("googleAnalytics.webPropertyId");
    }

    public boolean isIncludeLinkAttribution() {
        return BLCSystemProperty.resolveBooleanSystemProperty("googleAnalytics.enableLinkAttribution", true);
    }

    public boolean isIncludeDisplayAdvertising() {
        return BLCSystemProperty.resolveBooleanSystemProperty("googleAnalytics.enableDisplayAdvertising", false);
    }

}
