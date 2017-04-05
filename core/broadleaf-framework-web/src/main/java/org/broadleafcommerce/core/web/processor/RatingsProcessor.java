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

import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.core.rating.domain.RatingSummary;
import org.broadleafcommerce.core.rating.domain.ReviewDetail;
import org.broadleafcommerce.core.rating.service.RatingService;
import org.broadleafcommerce.core.rating.service.type.RatingType;
import org.broadleafcommerce.presentation.condition.ConditionalOnTemplating;
import org.broadleafcommerce.presentation.dialect.AbstractBroadleafVariableModifierProcessor;
import org.broadleafcommerce.presentation.model.BroadleafTemplateContext;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

/**
 * A Thymeleaf processor that will add the product ratings and reviews to the model
 *
 * @author jfridye
 */
@Component("blRatingsProcessor")
@ConditionalOnTemplating
public class RatingsProcessor extends AbstractBroadleafVariableModifierProcessor {

    @Resource(name = "blRatingService")
    protected RatingService ratingService;

    @Override
    public String getName() {
        return "ratings";
    }
    
    @Override
    public int getPrecedence() {
        return 10000;
    }

    @Override
    public Map<String, Object> populateModelVariables(String tagName, Map<String, String> tagAttributes, BroadleafTemplateContext context) {
        String itemId = String.valueOf(context.parseExpression(tagAttributes.get("itemId")));
        RatingSummary ratingSummary = ratingService.readRatingSummary(itemId, RatingType.PRODUCT);
        Map<String, Object> newModelVars = new HashMap<>();
        if (ratingSummary != null) {
            newModelVars.put(getRatingsVar(tagAttributes), ratingSummary);
        }

        Customer customer = CustomerState.getCustomer();
        ReviewDetail reviewDetail = null;
        if (!customer.isAnonymous()) {
            reviewDetail = ratingService.readReviewByCustomerAndItem(customer, itemId);
        }
        if (reviewDetail != null) {
            newModelVars.put("currentCustomerReview", reviewDetail);
        }
        return newModelVars;
    }

    private String getRatingsVar(Map<String, String> tagAttributes) {
        String ratingsVar = tagAttributes.get("ratingsVar");
        if (StringUtils.isNotEmpty(ratingsVar)) {
            return ratingsVar;
        }
        return "ratingSummary";
    }
}
