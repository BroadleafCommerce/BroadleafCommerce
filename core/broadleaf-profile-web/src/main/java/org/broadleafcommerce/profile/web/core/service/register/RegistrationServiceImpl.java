/*
 * #%L
 * BroadleafCommerce Profile Web
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
package org.broadleafcommerce.profile.web.core.service.register;

import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.broadleafcommerce.profile.web.core.form.RegisterCustomerForm;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


@Service("blRegistrationService")
public class RegistrationServiceImpl implements RegistrationService {

    @Resource(name="blCustomerService")
    protected CustomerService customerService;

    @Override
    public RegisterCustomerForm initCustomerRegistrationForm() {
        Customer customer = CustomerState.getCustomer();
        if (customer == null || ! customer.isAnonymous()) {
            customer = customerService.createCustomerFromId(null);
        }

        RegisterCustomerForm customerRegistrationForm = new RegisterCustomerForm();
        customerRegistrationForm.setCustomer(customer);
        return customerRegistrationForm;
    }

    @Override
    public void addRedirectUrlToForm(RegisterCustomerForm registerCustomerForm) {
        HttpServletRequest request = BroadleafRequestContext.getBroadleafRequestContext().getRequest();
        String redirectUrl = request.getParameter("successUrl");

        if (StringUtils.isNotBlank(redirectUrl)) {
            registerCustomerForm.setRedirectUrl(redirectUrl);
        } else {
            registerCustomerForm.setRedirectUrl(request.getContextPath());
        }
    }



}
