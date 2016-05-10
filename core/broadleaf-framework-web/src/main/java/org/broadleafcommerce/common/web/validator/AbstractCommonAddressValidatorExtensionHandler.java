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
package org.broadleafcommerce.common.web.validator;

import org.broadleafcommerce.common.extension.AbstractExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.web.form.BroadleafFormType;
import org.broadleafcommerce.profile.core.domain.Address;
import org.springframework.validation.Errors;

/**
 * @author Elbert Bautista (elbertbautista)
 */
public abstract class AbstractCommonAddressValidatorExtensionHandler extends AbstractExtensionHandler
        implements BroadleafCommonAddressValidatorExtensionHandler {

    @Override
    public ExtensionResultStatusType validate(BroadleafFormType formType, Address address, Errors errors) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

}
