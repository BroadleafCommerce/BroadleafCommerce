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
package org.broadleafcommerce.core.web.controller.account;


import org.broadleafcommerce.common.payment.PaymentAdditionalFieldType;
import org.broadleafcommerce.common.web.controller.BroadleafAbstractController;
import org.broadleafcommerce.core.web.checkout.model.BillingInfoForm;
import org.broadleafcommerce.core.web.controller.account.validator.SavePaymentValidator;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerPayment;
import org.broadleafcommerce.profile.core.service.CustomerPaymentService;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.security.InvalidParameterException;
import java.util.List;

/**
 * This is the page controller for adding, updating, and deleting saved payment information.
 *
 * @author Jacob Mitash
 */
public class BroadleafManageCustomerPaymentsController extends BroadleafAbstractController {

    @Value("${validate.customer.owned.data:true}")
    protected boolean validateCustomerOwnedData;

    protected static String customerPaymentView = "account/manageCustomerPayments";
    protected static String customerPaymentRedirect = "redirect:/account/payments";

    @Resource(name = "blSavePaymentValidator")
    protected SavePaymentValidator savePaymentValidator;

    @Resource(name = "blCustomerPaymentService")
    protected CustomerPaymentService customerPaymentService;

    public String viewCustomerPayments(HttpServletRequest request, Model model) {
        Customer customer = CustomerState.getCustomer(request);

        if(customer == null) {
            throw new SecurityException("Customer is not found but tried to access account page");
        }

        List<CustomerPayment> savedPaymentList = customerPaymentService.readCustomerPaymentsByCustomerId(customer.getId());

        SavePaymentForm savePaymentForm = new SavePaymentForm();
        savePaymentForm.setDefaultMethod(savedPaymentList.isEmpty());

        model.addAttribute("savedPayments", savedPaymentList);
        model.addAttribute("savePaymentForm", savePaymentForm);
        model.addAttribute("billingInfoForm", new BillingInfoForm());
        model.addAttribute("managePaymentMethods", true);

        return getCustomerPaymentView();
    }

    public String addCustomerPayment(HttpServletRequest request, Model model, SavePaymentForm form, BindingResult result) {
        Customer customer = CustomerState.getCustomer();

        savePaymentValidator.validate(form, result);

        if(result.hasErrors()) {
            return getCustomerPaymentRedirect();
        }

        CustomerPayment customerPayment = customerPaymentService.create();
        customerPayment.setCustomer(customer);
        customerPayment.setIsDefault(form.isDefaultMethod());
        customerPayment.getAdditionalFields().put(PaymentAdditionalFieldType.EXP_DATE.getType(), form.getExpiration());
        customerPayment.getAdditionalFields().put(PaymentAdditionalFieldType.LAST_FOUR.getType(), form.getLastFourDigits());
        customerPayment.getAdditionalFields().put(PaymentAdditionalFieldType.PAYMENT_NAME.getType(), form.getPaymentName());
        customerPayment.getAdditionalFields().put(PaymentAdditionalFieldType.NAME_ON_CARD.getType(), form.getPersonName());
//        customerPayment.setToken();

        customerPaymentService.saveCustomerPayment(customerPayment);

        return getCustomerPaymentRedirect();
    }

    public String makeDefaultCustomerPayment(HttpServletRequest request, Model model, Long customerPaymentId) {
        CustomerPayment customerPayment = customerPaymentService.readCustomerPaymentById(customerPaymentId);

        if(customerPayment == null) {
            throw new InvalidParameterException("Requested customer payment does not exist.");
        }

        customerPaymentService.setAsDefaultPayment(customerPayment);

        return getCustomerPaymentRedirect();
    }

    public String removeCustomerPayment(HttpServletRequest request, Model model, Long customerPaymentId) {
        customerPaymentService.deleteCustomerPaymentById(customerPaymentId);

        return getCustomerPaymentRedirect();
    }

    public String getCustomerPaymentView() {
        return customerPaymentView;
    }

    public String getCustomerPaymentRedirect() {
        return customerPaymentRedirect;
    }
}
