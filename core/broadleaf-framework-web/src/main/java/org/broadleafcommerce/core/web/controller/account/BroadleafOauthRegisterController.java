/*-
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.core.web.controller.account;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.core.order.domain.NullOrderImpl;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.core.web.order.CartState;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.web.core.form.RegisterCustomerForm;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * This is an extension of BroadleafRegisterController
 * that utilizes Spring Social to register a customer from a Service Provider
 * such as Facebook or Twitter.
 * <p>
 * To use: extend this class and provide @RequestMapping annotations
 *
 * @author elbertbautista
 * @see org.broadleafcommerce.core.web.controller.account.BroadleafRegisterController
 */
public class BroadleafOauthRegisterController extends BroadleafRegisterController {

    private final OAuth2AuthorizedClientService authorizedClientService;

    public BroadleafOauthRegisterController(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    @RequestMapping
    public String register(
            RegisterCustomerForm registerCustomerForm,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model
    ) {
        try {
            assert (authorizedClientService != null);
            OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                    registerCustomerForm.getProviderId(),
                    registerCustomerForm.getOAuth2UserRequest().getClientRegistration().getRegistrationId()
            );

            if (authorizedClient != null) {
                OAuth2AuthenticationToken authenticationToken = (OAuth2AuthenticationToken) SecurityContextHolder
                        .getContext()
                        .getAuthentication();
                Customer customer = registerCustomerForm.getCustomer();
                OAuth2User oauth2User = authenticationToken.getPrincipal();
                customer.setFirstName(oauth2User.getAttribute("firstName"));
                customer.setLastName(oauth2User.getAttribute("lastName"));
                customer.setEmailAddress(oauth2User.getAttribute("email"));
                if (isUseEmailForLogin()) {
                    customer.setUsername(oauth2User.getAttribute("email"));
                } else {
                    customer.setUsername(oauth2User.getAttribute("username"));
                }
            }
        } catch (NullPointerException e) {
            model.addAttribute("error", "An error occurred while processing the registration. Please try again.");
            return "errorPage";
        }
        return super.register(registerCustomerForm, request, response, model);
    }


    //Calls ProviderSignInUtils.handlePostSignUp() after a successful registration
    @RequestMapping(params = "action=register")
    public String processRegister(
            RegisterCustomerForm registerCustomerForm,
            BindingResult errors,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model,
            RedirectAttributes redirectAttributes
    ) throws PricingException {
        if (isUseEmailForLogin()) {
            Customer customer = registerCustomerForm.getCustomer();
            customer.setUsername(customer.getEmailAddress());
        }
        registerCustomerValidator.validate(registerCustomerForm, errors, isUseEmailForLogin());
        if (!errors.hasErrors()) {
            Customer newCustomer = customerService.registerCustomer(
                    registerCustomerForm.getCustomer(),
                    registerCustomerForm.getPassword(),
                    registerCustomerForm.getPasswordConfirm()
            );
            assert (newCustomer != null);

            assert (authorizedClientService != null);
            OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                    registerCustomerForm.getProviderId(),
                    registerCustomerForm.getOAuth2UserRequest().getClientRegistration().getRegistrationId()
            );
            authorizedClientService.saveAuthorizedClient(
                    authorizedClient,
                    (Authentication) registerCustomerForm.getOAuth2UserRequest()
            );

            loginService.loginCustomer(registerCustomerForm.getCustomer());

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
