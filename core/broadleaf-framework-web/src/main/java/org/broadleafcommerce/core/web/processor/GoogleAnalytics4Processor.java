/*-
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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

import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.Resource;

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
 *  <li>Multiple trackers (extensible via {@link #getTrackers()} or by setting the {@code googleAnalytics4.masterWebPropertyId}
 *      and {@code googleAnalytics4.webPropertyId})</li>
 *  <li>Affiliates for e-commerce ({@ googleAnalytics.affiliation property})</li>
 *  <li><a href="https://support.google.com/analytics/answer/2558867?hl=en&utm_id=ad">Link attribution</a>
 *      ({@code googleAnalytics4.enableLinkAttribution} system property, default {@code true})</li>
 *  <li><a href="https://support.google.com/analytics/answer/3450482">Display Advertising</a>
 *      ({@code googleAnalytics4.enableDisplayAdvertising} system property, default {@code false})</li>
 * </ul>
 *
 * @param ordernumber the order number to look up for ecommerce tracking, such as on the confirmation page
 * @author Markiian Buryi (MarekB01)
 */
@Component("blGoogleAnalytics4Processor")
@ConditionalOnTemplating
public class GoogleAnalytics4Processor extends AbstractBroadleafTagReplacementProcessor {

    private static final Log LOG = LogFactory.getLog(GoogleAnalytics4Processor.class);

    /**
     * Global value, intentionally only retrieved as a file property NOT via the system properties service
     */
    @Value("${googleAnalytics4.masterWebPropertyId:null}")
    protected String googleAnalytics4PropertyId;

    @Value("${googleAnalytics4.webPropertyId:null}")
    protected String googleAnalytics4WebPropertyId;

    @Value("${googleAnalytics4.tagIdForProperty:null}")
    protected String tagId;

    @Resource(name = "blOrderService")
    protected OrderService orderService;

    /**
     * This will force the domain to 127.0.0.1 which is useful to determine if the Google Analytics tag is sending
     * a request to Google
     */
    @Value("${googleAnalytics4.testLocal:false}")
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
    public BroadleafTemplateModel getReplacementModel(
            String tagName,
            Map<String, String> tagAttributes,
            BroadleafTemplateContext context
    ) {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotEmpty(getTagIdForProperty())) {
            tagId = getTagIdForProperty();
        }
        if (StringUtils.isNotEmpty(getWebPropertyId())) {
            googleAnalytics4WebPropertyId = getWebPropertyId();
        }

        if (StringUtils.isNotEmpty(tagId) || StringUtils.isNotEmpty(googleAnalytics4PropertyId)
                || StringUtils.isNotEmpty(googleAnalytics4WebPropertyId)) {
            sb.append("window.dataLayer = window.dataLayer || [];");
            sb.append("function gtag(){dataLayer.push(arguments);}");
            sb.append("gtag('js', new Date());");
            if (testLocal) {
                sb.append("gtag('set', 'cookie_domain', 'none');");
            }
            if (isIncludeLinkAttribution()) {
                sb.append("gtag('set', 'link_attribution', true);");
            }
            if (isIncludeDisplayAdvertising()) {
                sb.append("gtag('set', 'allow_google_signals', true);");
            }
            if (StringUtils.isNotEmpty(googleAnalytics4PropertyId)) {
                sb.append("gtag('config', '" + googleAnalytics4PropertyId + "');");
            }
            if (StringUtils.isNotEmpty(googleAnalytics4WebPropertyId)) {
                sb.append("gtag('config', '" + googleAnalytics4WebPropertyId + "');");
            }
            if (StringUtils.isNotEmpty(tagId)) {
                sb.append("gtag('config', '" + tagId + "');");
            }

            String orderNumberExpression = tagAttributes.get("ordernumber");
            String orderNumber = null;
            if (orderNumberExpression != null) {
                orderNumber = context.parseExpression(orderNumberExpression);
            }

            if (orderNumber != null) {
                Order order = orderService.findOrderByOrderNumber(orderNumber);
                if (order != null) {
                    sb.append(getTransactionJs(order));
                }
            }

            // Add contentNode to the document
            BroadleafTemplateModel model = context.createModel();
            Map<String, String> attrs = new HashMap<>();
            attrs.put("asynch", null);
            String analyticsProperty = StringUtils.isNotEmpty(googleAnalytics4PropertyId)
                    ? googleAnalytics4PropertyId
                    : googleAnalytics4WebPropertyId;
            String idToUse = StringUtils.isNotEmpty(analyticsProperty) ? analyticsProperty : tagId;
            attrs.put("src", "https://www.googletagmanager.com/gtag/js?id=" + idToUse);
            BroadleafTemplateElement importScript = context.createStandaloneElement("script", attrs, true);
            BroadleafTemplateNonVoidElement scriptTag = context.createNonVoidElement("script");
            BroadleafTemplateElement script = context.createTextElement(sb.toString());
            scriptTag.addChild(script);
            model.addElement(importScript);
            model.addElement(scriptTag);
            return model;
        } else {
            LOG.warn("No trackers were found, not outputting Google Analytics script. Set the googleAnalytics.webPropertyId"
                    + " and/or the googleAnalytics.masterWebPropertyId4 system properties to output Google Analytics");
        }
        return null;
    }

    /**
     * /**
     * Builds the transaction analytics for the given tracker name. Invokes {@link #getItemJs(Order, String) for each item
     * in the given <b>order</b>.
     */
    protected String getTransactionJs(Order order) {
        StringBuilder sb = new StringBuilder();
        sb.append("gtag('event', purchase',{");

        sb.append("transaction_id: '" + order.getOrderNumber() + "'");
        if (StringUtils.isNotBlank(getAffiliation())) {
            sb.append(",affiliation: '" + getAffiliation() + "'");
        }
        sb.append(",value: " + order.getTotal());
        sb.append(",shipping:" + order.getTotalShipping() + "");
        sb.append(",tax: " + order.getTotalTax());

        if (order.getCurrency() != null) {
            sb.append(",currency: '" + order.getCurrency().getCurrencyCode() + "'");
        }

        sb.append(getItemJs(order));

        sb.append("});");
        return sb.toString();
    }

    protected String getItemJs(Order order) {
        StringBuilder sb = new StringBuilder();
        sb.append(",items:[");
        String comma = "";
        for (FulfillmentGroup fulfillmentGroup : order.getFulfillmentGroups()) {
            for (FulfillmentGroupItem fulfillmentGroupItem : fulfillmentGroup.getFulfillmentGroupItems()) {

                OrderItem orderItem = fulfillmentGroupItem.getOrderItem();
                if (orderItem instanceof DiscreteOrderItem) {
                    if (SkuAccessor.class.isAssignableFrom(orderItem.getClass())) {
                        sb.append(comma).append("{");
                        Sku sku = ((SkuAccessor) orderItem).getSku();
                        sb.append("item_id: '" + sku.getId() + "'");
                        sb.append(",item_name: '" + sku.getName() + "'");
                        sb.append(",item_category: '" + getVariation(orderItem) + "'");
                        sb.append(",price: " + orderItem.getAveragePrice());
                        sb.append(",quantity: " + orderItem.getQuantity());
                        sb.append("}");
                        comma = ",";
                    }
                }
            }
        }
        sb.append("]");
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

    public String getTagIdForProperty() {
        return BLCSystemProperty.resolveSystemProperty("googleAnalytics4.tagIdForProperty");
    }

    public String getWebPropertyId() {
        return BLCSystemProperty.resolveSystemProperty("googleAnalytics4.webPropertyId");
    }

    public String getAffiliation() {
        return BLCSystemProperty.resolveSystemProperty("googleAnalytics4.affiliation");
    }

    public boolean isIncludeLinkAttribution() {
        return BLCSystemProperty.resolveBooleanSystemProperty(
                "googleAnalytics4.enableLinkAttribution", true
        );
    }

    public boolean isIncludeDisplayAdvertising() {
        return BLCSystemProperty.resolveBooleanSystemProperty(
                "googleAnalytics4.enableDisplayAdvertising", false
        );
    }

}
