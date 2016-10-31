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
package org.broadleafcommerce.core.promotionMessage.processor;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.promotionMessage.domain.type.PromotionMessagePlacementType;
import org.broadleafcommerce.core.promotionMessage.dto.PromotionMessageDTO;
import org.broadleafcommerce.core.promotionMessage.service.PromotionMessageGenerator;
import org.broadleafcommerce.core.promotionMessage.util.BLCPromotionMessageUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.element.AbstractLocalVariableDefinitionElementProcessor;
import org.thymeleaf.standard.expression.Expression;
import org.thymeleaf.standard.expression.StandardExpressions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

/**
 * @author Chris Kittrell (ckittrell)
 */
@Service("blPromotionMessageProcessor")
public class PromotionMessageProcessor extends AbstractLocalVariableDefinitionElementProcessor {

    private static final Log LOG = LogFactory.getLog(PromotionMessageProcessor.class);

    public static final String PRODUCT = "product";
    public static final String PLACEMENT = "placement";

    @Resource(name = "blPromotionMessageGenerators")
    protected List<PromotionMessageGenerator> generators;

    /**
     * Sets the name of this processor to be used in Thymeleaf template
     */
    public PromotionMessageProcessor() {
        super("promotion_messages");
    }
    
    @Override
    public int getPrecedence() {
        return 1000;
    }

    @Override
    protected Map<String, Object> getNewLocalVariables(Arguments arguments, Element element) {
        Product product = getProductFromArguments(arguments, element);
        List<String> placementTypes = getPlacementFromArguments(element);
        placementTypes.add(PromotionMessagePlacementType.EVERYWHERE.getType());

        Map<String, List<PromotionMessageDTO>> promotionMessages = new MultiValueMap();
        for (PromotionMessageGenerator generator : generators) {
            promotionMessages.putAll(generator.generatePromotionMessages(product));
        }

        List<PromotionMessageDTO> filteredPromotionMessages = new ArrayList<>();
        for (String type : promotionMessages.keySet()) {
            if (placementTypes.contains(type)) {
                filteredPromotionMessages.addAll(promotionMessages.get(type));
            }
        }

        BLCPromotionMessageUtils.sortMessagesByPriority(filteredPromotionMessages);

        Map<String, Object> newVars = new HashMap<>();
        newVars.put("promotionMessages", filteredPromotionMessages);
        return newVars;
    }

    protected Product getProductFromArguments(Arguments arguments, Element element) {
        Expression expression = (Expression) StandardExpressions.getExpressionParser(arguments.getConfiguration())
                .parseExpression(arguments.getConfiguration(), arguments, element.getAttributeValue(PRODUCT));
        return (Product) expression.execute(arguments.getConfiguration(), arguments);
    }

    protected List<String> getPlacementFromArguments(Element element) {
        String placementString = element.getAttributeValue(PLACEMENT);
        List<String> requestedPlacement = new ArrayList<>();

        for (String placement : placementString.split(",")) {
            placement = placement.trim();
            if (testPlacementType(requestedPlacement, placement)) {
                requestedPlacement.add(placement);
            }
        }
        return requestedPlacement;
    }

    protected Boolean testPlacementType(List<String> requestedPlacement, String placement) {
        try {
            PromotionMessagePlacementType type = PromotionMessagePlacementType.getInstance(placement);
            requestedPlacement.add(type.getType());
        } catch (Exception e) {
            LOG.error("Unrecognized Promotion Message Placement Type", e);
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    @Override
    protected boolean removeHostElement(Arguments arguments, Element element) {
        return false;
    }
}
