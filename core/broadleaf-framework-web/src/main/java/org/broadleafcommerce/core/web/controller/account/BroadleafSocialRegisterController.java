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
import org.broadleafcommerce.core.order.domain.NullOrderImpl;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.core.web.order.CartState;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.web.core.form.RegisterCustomerForm;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This is an extension of BroadleafRegisterController
 * that utilizes Spring Social to register a customer from a Service Provider
 * such as Facebook or Twitter.
 *
 * To use: extend this class and provide @RequestMapping annotations
 *
 * @see org.broadleafcommerce.core.web.controller.account.BroadleafRegisterController
 * @author elbertbautista
 *
 */
public class BroadleafSocialRegisterController extends BroadleafRegisterController {

    //Pre-populate portions of the RegisterCustomerForm from ProviderSignInUtils.getConnection();
    public String register(RegisterCustomerForm registerCustomerForm, HttpServletRequest request,
                           HttpServletResponse response, Model model) {
        Connection<?> connection = ProviderSignInUtils.getConnection(new ServletWebRequest(request));
        if (connection != null) {
            UserProfile userProfile = connection.fetchUserProfile();
            Customer customer = registerCustomerForm.getCustomer();
            customer.setFirstName(userProfile.getFirstName());
            customer.setLastName(userProfile.getLastName());
            customer.setEmailAddress(userProfile.getEmail());
            if (isUseEmailForLogin()){
                customer.setUsername(userProfile.getEmail());
            } else {
                customer.setUsername(userProfile.getUsername());
            }
        }

        return super.register(registerCustomerForm, request, response, model);
    }

    //Calls ProviderSignInUtils.handlePostSignUp() after a successful registration
    public String processRegister(RegisterCustomerForm registerCustomerForm, BindingResult errors,
                                  HttpServletRequest request, HttpServletResponse response, Model model)
            throws ServiceException, PricingException {
        if (isUseEmailForLogin()) {
            Customer customer = registerCustomerForm.getCustomer();
            customer.setUsername(customer.getEmailAddress());
        }

        registerCustomerValidator.validate(registerCustomerForm, errors, isUseEmailForLogin());
        if (!errors.hasErrors()) {
            Customer newCustomer = customerService.registerCustomer(registerCustomerForm.getCustomer(),
                    registerCustomerForm.getPassword(), registerCustomerForm.getPasswordConfirm());
            assert(newCustomer != null);

            ProviderSignInUtils.handlePostSignUp(newCustomer.getUsername(), new ServletWebRequest(request));

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

}
