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

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.web.dialect.AbstractModelVariableModifierProcessor;
import org.broadleafcommerce.core.rating.domain.RatingSummary;
import org.broadleafcommerce.core.rating.domain.ReviewDetail;
import org.broadleafcommerce.core.rating.service.RatingService;
import org.broadleafcommerce.core.rating.service.type.RatingType;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.standard.expression.Expression;
import org.thymeleaf.standard.expression.StandardExpressions;

import javax.annotation.Resource;

/**
 * A Thymeleaf processor that will add the product ratings and reviews to the model
 *
 * @author jfridye
 */
public class RatingsProcessor extends AbstractModelVariableModifierProcessor {
    
    @Resource(name = "blRatingService")
    protected RatingService ratingService;

    /**
     * Sets the name of this processor to be used in Thymeleaf template
     *
     * NOTE: Thymeleaf normalizes the attribute names by converting all to lower-case
     * we will use the underscore instead of camel case to avoid confusion
     *
     */
    public RatingsProcessor() {
        super("ratings");
    }

    @Override
    public int getPrecedence() {
        return 10000;
    }

    @Override
    protected void modifyModelAttributes(Arguments arguments, Element element) {
        Expression expression = (Expression) StandardExpressions.getExpressionParser(arguments.getConfiguration())
                .parseExpression(arguments.getConfiguration(), arguments, element.getAttributeValue("itemId"));
        String itemId = String.valueOf(expression.execute(arguments.getConfiguration(), arguments));
        RatingSummary ratingSummary = ratingService.readRatingSummary(itemId, RatingType.PRODUCT);
        if (ratingSummary != null) {
            addToModel(arguments, getRatingsVar(element), ratingSummary);
        }
        
        Customer customer = CustomerState.getCustomer();
        ReviewDetail reviewDetail = null;
        if (!customer.isAnonymous()) {
            reviewDetail = ratingService.readReviewByCustomerAndItem(customer, itemId);
        }
        if (reviewDetail != null) {
            addToModel(arguments, "currentCustomerReview", reviewDetail);
        }
        
    }
    
    private String getRatingsVar(Element element) {
        String ratingsVar = element.getAttributeValue("ratingsVar");
        if (StringUtils.isNotEmpty(ratingsVar)) {
            return ratingsVar;
        } 
        return "ratingSummary";
    }

}
