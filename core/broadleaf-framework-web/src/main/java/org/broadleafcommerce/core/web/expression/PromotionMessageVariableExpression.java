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
package org.broadleafcommerce.core.web.expression;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.offer.domain.OrderItemPriceDetailAdjustment;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.domain.OrderItemPriceDetail;
import org.broadleafcommerce.core.promotionMessage.domain.type.PromotionMessagePlacementType;
import org.broadleafcommerce.core.promotionMessage.dto.PromotionMessageDTO;
import org.broadleafcommerce.core.promotionMessage.service.PromotionMessageGenerator;
import org.broadleafcommerce.core.promotionMessage.util.BLCPromotionMessageUtils;
import org.broadleafcommerce.presentation.condition.ConditionalOnTemplating;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

/**
 * @author Chris Kittrell (ckittrell)
 */
@Service("blPromotionMessageVariableExpression")
@ConditionalOnTemplating
public class PromotionMessageVariableExpression extends BLCVariableExpression {

    private static final Log LOG = LogFactory.getLog(PromotionMessageVariableExpression.class);

    public static final String PRODUCT = "product";
    public static final String PLACEMENT = "placement";

    @Resource(name = "blPromotionMessageGenerators")
    protected List<PromotionMessageGenerator> generators;

    @Override
    public String getName() {
        return "promotion_messages";
    }
    
    public List<PromotionMessageDTO> getProductPromotionMessages(Product product, String... placements) {
        List<String> filteredPlacements = filterInvalidPlacements(placements);
        if (!filteredPlacements.contains(PromotionMessagePlacementType.EVERYWHERE.getType())) {
            filteredPlacements.add(PromotionMessagePlacementType.EVERYWHERE.getType());
        }
        
        Map<String, List<PromotionMessageDTO>> promotionMessages = new MultiValueMap();
        for (PromotionMessageGenerator generator : generators) {
            promotionMessages.putAll(generator.generatePromotionMessages(product));
        }

        List<PromotionMessageDTO> filteredMessages = BLCPromotionMessageUtils.filterPromotionMessageDTOsByTypes(promotionMessages, filteredPlacements);
        BLCPromotionMessageUtils.sortMessagesByPriority(filteredMessages);

        return filteredMessages;
    }
    
    public List<String> getItemPromotionMessages(OrderItem orderItem) {
        List<String> appliedOfferNames = getAppliedOfferNamesForOrderItem(orderItem);
        for (OrderItem child : orderItem.getChildOrderItems()) {
            appliedOfferNames.addAll(getAppliedOfferNamesForOrderItem(child));
        }
        return appliedOfferNames;
    }
    
    protected List<String> getAppliedOfferNamesForOrderItem(OrderItem orderItem) {
        List<String> appliedOfferNames = new ArrayList<>();
        for (OrderItemPriceDetail oipd : orderItem.getOrderItemPriceDetails()) {
            for (OrderItemPriceDetailAdjustment adjustment : oipd.getOrderItemPriceDetailAdjustments()) {
                appliedOfferNames.add(adjustment.getOfferName());
            }
        }
        return appliedOfferNames;
    }
    
    protected List<String> filterInvalidPlacements(String[] placements) {
        List<String> requestedPlacement = new ArrayList<>();
        for (String placement : placements) {
            placement = placement.trim();
            if (isValidPlacementType(placement)) {
                requestedPlacement.add(placement);
            } else {
                LOG.warn("Stripping out invalid promotion message placement " + placement + ". See PromotionMessagePlacementType for valid placements");
            }
        }
        return requestedPlacement;
    }

    protected boolean isValidPlacementType(String placement) {
        return PromotionMessagePlacementType.getInstance(placement) != null;
    }

}
