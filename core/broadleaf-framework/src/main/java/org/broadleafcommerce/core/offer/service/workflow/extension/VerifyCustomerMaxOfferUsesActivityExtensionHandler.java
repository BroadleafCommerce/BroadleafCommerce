/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.offer.service.workflow.extension;

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.core.checkout.service.workflow.CheckoutSeed;
import org.broadleafcommerce.core.offer.service.exception.OfferMaxUseExceededException;

/**
 * @author Nathan Moore (nathanmoore).
 */
public interface VerifyCustomerMaxOfferUsesActivityExtensionHandler extends ExtensionHandler {
    /**
     * This extension allows for one to verify the number of times a Customer has used an Offer
     * without having to override or extend 
     * {@link org.broadleafcommerce.core.offer.service.workflow.VerifyCustomerMaxOfferUsesActivity}
     * 
     * @param checkoutSeed
     * @param resultHolder
     * @return ExtensionResultStatusType
     */
    ExtensionResultStatusType verify(CheckoutSeed checkoutSeed, ExtensionResultHolder<Exception> resultHolder)
            throws OfferMaxUseExceededException;
}
