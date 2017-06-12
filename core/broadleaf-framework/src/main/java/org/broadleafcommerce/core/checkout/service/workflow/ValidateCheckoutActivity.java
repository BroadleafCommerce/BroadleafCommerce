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
package org.broadleafcommerce.core.checkout.service.workflow;

import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.core.checkout.service.workflow.extension.ValidateCheckoutActivityExtensionManager;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * This activity is responsible for providing an extension point for validating a checkout request.
 *
 * @author Nick Crum ncrum
 */
@Component("blValidateCheckoutActivity")
public class ValidateCheckoutActivity extends BaseActivity<ProcessContext<CheckoutSeed>> {

    public static final int ORDER = 500;
    
    @Resource(name = "blValidateCheckoutActivityExtensionManager")
    protected ValidateCheckoutActivityExtensionManager extensionManager;
    
    public ValidateCheckoutActivity() {
        setOrder(ORDER);
    }

    @Override
    public ProcessContext<CheckoutSeed> execute(ProcessContext<CheckoutSeed> context) throws Exception {
        ExtensionResultHolder<Exception> resultHolder = new ExtensionResultHolder<>();
        resultHolder.setResult(null);
        ExtensionResultStatusType result = extensionManager.getProxy().validateCheckout(context.getSeedData(), resultHolder);
        if (!ExtensionResultStatusType.NOT_HANDLED.equals(result)) {
            if (resultHolder.getResult() != null) {
                throw resultHolder.getResult();
            }
        }

        return context;
    }
}
