/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.core.web.controller.account;

import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.web.controller.BroadleafAbstractController;
import org.broadleafcommerce.core.order.domain.NullOrderImpl;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.MergeCartService;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.core.web.order.CartState;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.broadleafcommerce.profile.web.controller.validator.RegisterCustomerValidator;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.broadleafcommerce.profile.web.core.form.RegisterCustomerForm;
import org.broadleafcommerce.profile.web.core.service.login.LoginService;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The controller responsible for registering a customer.
 * 
 * Uses a component registered with the name blCustomerValidator to perform validation of the
 * submitted customer.
 * 
 * Uses the property "useEmailForLogin" to determine if the username should be defaulted to the
 * email address if no username is supplied.
 * 
 * 
 * @author apazzolini
 * @author bpolster
 */
public class BroadleafRegisterController extends BroadleafAbstractController {
        
    protected boolean useEmailForLogin = true;
    protected static String registerSuccessView = "ajaxredirect:/";
    protected static String registerView = "authentication/register";
    
    @Resource(name="blCustomerService")
    protected CustomerService customerService;

    @Resource(name="blRegisterCustomerValidator")
    protected RegisterCustomerValidator registerCustomerValidator;

    @Resource(name="blLoginService")
    protected LoginService loginService;

    @Resource(name = "blOrderService")
    protected OrderService orderService;
    
    public String register(RegisterCustomerForm registerCustomerForm, HttpServletRequest request, 
            HttpServletResponse response, Model model) {
        String redirectUrl = request.getParameter("successUrl");
        if (StringUtils.isNotBlank(redirectUrl)) {
            registerCustomerForm.setRedirectUrl(redirectUrl);
        }
        return getRegisterView();
    }
    
    public String processRegister(RegisterCustomerForm registerCustomerForm, BindingResult errors, 
            HttpServletRequest request, HttpServletResponse response, Model model)
            throws ServiceException, PricingException {
        
        if (useEmailForLogin) {
            Customer customer = registerCustomerForm.getCustomer();
            customer.setUsername(customer.getEmailAddress());
        }
        
        registerCustomerValidator.validate(registerCustomerForm, errors, useEmailForLogin);
        if (!errors.hasErrors()) {
            Customer newCustomer = customerService.registerCustomer(registerCustomerForm.getCustomer(), 
                    registerCustomerForm.getPassword(), registerCustomerForm.getPasswordConfirm());
            assert(newCustomer != null);
            
            // The next line needs to use the customer from the input form and not the customer returned after registration
            // so that we still have the unencoded password for use by the authentication mechanism.
            loginService.loginCustomer(registerCustomerForm.getCustomer());

            // Need to ensure that the Cart on CartState is owned by the newly registered customer.
            Order cart = CartState.getCart();
            if (cart != null && !(cart instanceof NullOrderImpl) && cart.getEmailAddress() == null) {
                cart.setEmailAddress(newCustomer.getEmailAddress());
                orderService.save(cart, false);
            }
            
            String redirectUrl = registerCustomerForm.getRedirectUrl();
            if (StringUtils.isNotBlank(redirectUrl) && redirectUrl.contains(":")) {
                redirectUrl = null;
            }
            return StringUtils.isBlank(redirectUrl) ? getRegisterSuccessView() : "redirect:" + redirectUrl;
        } else {
            return getRegisterView();
        }
    }
    
    public RegisterCustomerForm initCustomerRegistrationForm() {
        Customer customer = CustomerState.getCustomer();
        if (customer == null || ! customer.isAnonymous()) {
            customer = customerService.createCustomerFromId(null);
        }
        
        RegisterCustomerForm customerRegistrationForm = new RegisterCustomerForm();
        customerRegistrationForm.setCustomer(customer);
        return customerRegistrationForm;
    }

    public boolean isUseEmailForLogin() {
        return useEmailForLogin;
    }

    public void setUseEmailForLogin(boolean useEmailForLogin) {
        this.useEmailForLogin = useEmailForLogin;
    }

    /**
     * Returns the view that will be returned from this controller when the 
     * registration is successful.   The success view should be a redirect (e.g. start with "redirect:" since 
     * this will cause the entire SpringSecurity pipeline to be fulfilled.
     * 
     * By default, returns "redirect:/"
     * 
     * @return the register success view
     */
    public String getRegisterSuccessView() {
        return registerSuccessView;
    }

    /**
     * Returns the view that will be used to display the registration page.
     * 
     * By default, returns "/register"
     * 
     * @return the register view
     */
    public String getRegisterView() {
        return registerView;
    }


}
