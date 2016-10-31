/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.order.service;

import org.apache.commons.collections4.CollectionUtils;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.promotionMessage.domain.type.PromotionMessagePlacementType;
import org.broadleafcommerce.core.promotionMessage.dto.PromotionMessageDTO;
import org.broadleafcommerce.core.promotionMessage.service.PromotionMessageGenerator;
import org.broadleafcommerce.core.promotionMessage.util.BLCPromotionMessageUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author Chris Kittrell (ckittrell)
 */
@Service("blCartMessageOrderItemServiceExtensionHandler")
public class CartMessageOrderItemServiceExtensionHandler extends AbstractOrderServiceExtensionHandler {

    @Resource(name = "blOrderItemService")
    protected OrderItemService orderItemService;

    @Resource(name = "blPromotionMessageGenerators")
    protected List<PromotionMessageGenerator> generators;

    @Resource(name = "blOrderServiceExtensionManager")
    protected OrderServiceExtensionManager extensionManager;

    @PostConstruct
    public void init() {
        if (isEnabled()) {
            extensionManager.registerHandler(this);
        }
    }
    
    @Override
    public ExtensionResultStatusType attachAdditionalDataToOrder(Order order, boolean priceOrder) {
        for (OrderItem orderItem : order.getOrderItems()) {
            updateOrderItemCartMessages(orderItem);
        }

        return ExtensionResultStatusType.HANDLED_CONTINUE;
    }

    protected void updateOrderItemCartMessages(OrderItem orderItem) {
        List<String> cartMessages = gatherOrderItemCartMessages(orderItem);

        if (CollectionUtils.isEmpty(cartMessages)) {
            cartMessages = gatherProductCartMessages(orderItem);
        }

        orderItem.setCartMessages(cartMessages);
        orderItemService.saveOrderItem(orderItem);
    }

    protected List<String> gatherOrderItemCartMessages(OrderItem orderItem) {
        List<String> cartMessages = new ArrayList<>();
        for (PromotionMessageGenerator generator : generators) {
            cartMessages.addAll(generator.generatePromotionMessages(orderItem));
        }

        return cartMessages;
    }

    protected List<String> gatherProductCartMessages(OrderItem orderItem) {
        List<String> cartMessages = new ArrayList<>();

        if (DiscreteOrderItem.class.isAssignableFrom(orderItem.getClass())) {
            DiscreteOrderItem discreteOrderItem = (DiscreteOrderItem) orderItem;
            Product product = discreteOrderItem.getProduct();

            for (PromotionMessageGenerator generator : generators) {
                Map<String, List<PromotionMessageDTO>> promotionMessages = generator.generatePromotionMessages(product);
                List<PromotionMessageDTO> messageDTOs = new ArrayList<>();
                addPromotionMessagesForType(messageDTOs, promotionMessages.get(PromotionMessagePlacementType.CART.getType()));
                addPromotionMessagesForType(messageDTOs, promotionMessages.get(PromotionMessagePlacementType.EVERYWHERE.getType()));

                BLCPromotionMessageUtils.sortMessagesByPriority(messageDTOs);

                cartMessages.addAll(BLCPromotionMessageUtils.gatherMessagesFromDTOs(messageDTOs));
            }
        }

        return cartMessages;
    }

    protected void addPromotionMessagesForType(List<PromotionMessageDTO> messageDTOs, List<PromotionMessageDTO> messages) {
        if (CollectionUtils.isNotEmpty(messages)) {
            messageDTOs.addAll(messages);
        }
    }

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE;
    }
}
