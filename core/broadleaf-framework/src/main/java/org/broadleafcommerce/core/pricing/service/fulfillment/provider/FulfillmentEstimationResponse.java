/*-
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.pricing.service.fulfillment.provider;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.order.domain.FulfillmentOption;
import org.broadleafcommerce.core.pricing.service.FulfillmentPricingService;

import java.util.Map;

/**
 * DTO to allow FulfillmentProcessors to respond to estimation requests for a particular FulfillmentGroup
 * for a particular FulfillmentOptions
 * 
 * @author Phillip Verheyden
 * @see {@link FulfillmentPricingProvider}, {@link FulfillmentPricingService}
 */
public class FulfillmentEstimationResponse {

    protected Map<? extends FulfillmentOption, Money> fulfillmentOptionPrices;

    public Map<? extends FulfillmentOption, Money> getFulfillmentOptionPrices() {
        return fulfillmentOptionPrices;
    }

    public void setFulfillmentOptionPrices(Map<? extends FulfillmentOption, Money> fulfillmentOptionPrices) {
        this.fulfillmentOptionPrices = fulfillmentOptionPrices;
    }
}
