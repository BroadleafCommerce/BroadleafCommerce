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
package org.broadleafcommerce.core.offer.processor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.offer.domain.OrderItemPriceDetailAdjustment;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.domain.OrderItemPriceDetail;
import org.springframework.stereotype.Service;
import org.thymeleaf.Arguments;
import org.thymeleaf.processor.element.AbstractLocalVariableDefinitionElementProcessor;
import org.thymeleaf.standard.expression.StandardExpressions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Processor used to show the Offers that are being actively applied to the given {@link OrderItem}.
 *  This is mostly intended to be utilized in the context of the Cart.
 *
 * @author Jon Fleschler (jfleschler)
 */
@Service("blOfferMessageProcessor")
public class OfferMessageProcessor extends AbstractLocalVariableDefinitionElementProcessor {

    private static final Log LOG = LogFactory.getLog(OfferMessageProcessor.class);

    public static final String ORDER_ITEM = "item";

    /**
     * Sets the name of this processor to be used in Thymeleaf template
     */
    public OfferMessageProcessor() {
        super("offer_messages");
    }
    
    @Override
    public int getPrecedence() {
        return 1000;
    }

    @Override
    protected Map<String, Object> getNewLocalVariables(Arguments arguments, Element element) {
        OrderItem orderItem = getOrderItemFromArguments(arguments, element);

        Set<String> appliedOffers = getAppliedOffersForOrderItem(orderItem);
        for (OrderItem child : orderItem.getChildOrderItems()) {
            appliedOffers.addAll(getAppliedOffersForOrderItem(child));
        }

        Map<String, Object> newVars = new HashMap<>();
        newVars.put("appliedOffers", appliedOffers);
        return newVars;
    }

    protected Set<String> getAppliedOffersForOrderItem(OrderItem orderItem) {
        Set<String> appliedOffers = new HashSet<>();
        for (OrderItemPriceDetail oipd : orderItem.getOrderItemPriceDetails()) {
            for (OrderItemPriceDetailAdjustment adjustment : oipd.getOrderItemPriceDetailAdjustments()) {
                appliedOffers.add(adjustment.getOfferName());
            }
        }
        return appliedOffers;
    }

    protected OrderItem getOrderItemFromArguments(Arguments arguments, Element element) {
        Expression expression = (Expression) StandardExpressions.getExpressionParser(arguments.getConfiguration())
                .parseExpression(arguments.getConfiguration(), arguments, element.getAttributeValue(ORDER_ITEM));
        return (OrderItem) expression.execute(arguments.getConfiguration(), arguments);
    }

    @Override
    protected boolean removeHostElement(Arguments arguments, Element element) {
        return false;
    }
}
