/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
package org.broadleafcommerce.core.web.expression.checkout;

import org.broadleafcommerce.common.web.expression.BroadleafVariableExpression;
import org.broadleafcommerce.core.web.checkout.stage.CheckoutStageType;
import org.broadleafcommerce.presentation.condition.ConditionalOnTemplating;
import org.springframework.stereotype.Component;

/**
 * @author Chris Kittrell (ckittrell)
 */
@Component("blCheckoutStageVariableExpression")
@ConditionalOnTemplating
public class CheckoutStageVariableExpression implements BroadleafVariableExpression {

    @Override
    public String getName() {
        return "checkoutStage";
    }

    public boolean isPreviousStage(String currentStage, String activeStage) {
        CheckoutStageType currentStageType = CheckoutStageType.getInstance(currentStage);
        CheckoutStageType activeStageType = CheckoutStageType.getInstance(activeStage);

        return currentStageType.compareTo(activeStageType) < 0;
    }

    public boolean isActiveStage(String currentStage, String activeStage) {
        CheckoutStageType currentStageType = CheckoutStageType.getInstance(currentStage);
        CheckoutStageType activeStageType = CheckoutStageType.getInstance(activeStage);

        return currentStageType.compareTo(activeStageType) == 0;
    }

    public boolean isLaterStage(String currentStage, String activeStage) {
        CheckoutStageType currentStageType = CheckoutStageType.getInstance(currentStage);
        CheckoutStageType activeStageType = CheckoutStageType.getInstance(activeStage);

        return currentStageType.compareTo(activeStageType) > 0;
    }

}
