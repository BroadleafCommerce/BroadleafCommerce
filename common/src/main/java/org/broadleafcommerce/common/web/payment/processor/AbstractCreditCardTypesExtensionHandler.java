/*-
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.web.payment.processor;

import org.broadleafcommerce.common.extension.AbstractExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.payment.PaymentGatewayType;
import org.broadleafcommerce.common.payment.service.PaymentGatewayResolver;
import java.util.Map;
import javax.annotation.Resource;

/**
 * @author Elbert Bautista (elbertbautista)
 */
public abstract class AbstractCreditCardTypesExtensionHandler extends AbstractExtensionHandler
        implements CreditCardTypesExtensionHandler {

    @Resource(name = "blPaymentGatewayResolver")
    protected PaymentGatewayResolver paymentGatewayResolver;

    @Override
    public ExtensionResultStatusType populateCreditCardMap(Map<String, String> creditCardTypes) {

        if (paymentGatewayResolver.isHandlerCompatible(getHandlerType())) {
            setCardTypes(creditCardTypes);
            return ExtensionResultStatusType.HANDLED;
        }

        return ExtensionResultStatusType.NOT_HANDLED;
    }

    public abstract PaymentGatewayType getHandlerType();

    public abstract void setCardTypes(Map<String, String> creditCardTypes);

}
