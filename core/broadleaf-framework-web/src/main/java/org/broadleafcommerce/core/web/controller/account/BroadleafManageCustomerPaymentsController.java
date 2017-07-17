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


import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.common.i18n.domain.ISOCountry;
import org.broadleafcommerce.common.i18n.service.ISOService;
import org.broadleafcommerce.common.payment.PaymentAdditionalFieldType;
import org.broadleafcommerce.common.web.controller.BroadleafAbstractController;
import org.broadleafcommerce.core.web.checkout.model.BillingInfoForm;
import org.broadleafcommerce.core.web.checkout.validator.BillingInfoFormValidator;
import org.broadleafcommerce.core.web.controller.account.validator.SavePaymentValidator;
import org.broadleafcommerce.profile.core.domain.*;
import org.broadleafcommerce.profile.core.service.*;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.beans.PropertyEditorSupport;
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

    @Resource(name = "blCustomerAddressService")
    protected CustomerAddressService customerAddressService;

    @Resource(name = "blBillingInfoFormValidator")
    protected BillingInfoFormValidator billingInfoFormValidator;

    @Resource(name = "blAddressService")
    protected AddressService addressService;

    @Resource(name = "blCountryService")
    protected CountryService countryService;

    @Resource(name = "blStateService")
    protected StateService stateService;

    @Resource(name = "blISOService")
    protected ISOService isoService;

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

        List<CustomerAddress> customerAddresses = customerAddressService.readActiveCustomerAddressesByCustomerId(CustomerState.getCustomer().getId());

        model.addAttribute("customerAddresses", customerAddresses);

        return getCustomerPaymentView();
    }

    public String addCustomerPayment(HttpServletRequest request, Model model, SavePaymentForm paymentForm,
                                     BindingResult paymentResult, BillingInfoForm billingForm, BindingResult billingResult) {

        Customer customer = CustomerState.getCustomer();

        addressService.populateAddressISOCountrySub(billingForm.getAddress());

        savePaymentValidator.validate(paymentForm, paymentResult);
        billingInfoFormValidator.validate(billingForm, billingResult);


        if ((billingForm.getAddress().getPhonePrimary() != null) &&
                (StringUtils.isEmpty(billingForm.getAddress().getPhonePrimary().getPhoneNumber()))) {
            billingForm.getAddress().setPhonePrimary(null);
        }
        if ((billingForm.getAddress().getPhoneSecondary() != null) &&
                (StringUtils.isEmpty(billingForm.getAddress().getPhoneSecondary().getPhoneNumber()))) {
            billingForm.getAddress().setPhoneSecondary(null);
        }
        if ((billingForm.getAddress().getPhoneFax() != null) &&
                (StringUtils.isEmpty(billingForm.getAddress().getPhoneFax().getPhoneNumber()))) {
            billingForm.getAddress().setPhoneFax(null);
        }

        if(paymentResult.hasErrors() || billingResult.hasErrors()) {
            return getCustomerPaymentRedirect();
        }

        CustomerPayment customerPayment = customerPaymentService.create();
        customerPayment.setCustomer(customer);
        customerPayment.setIsDefault(paymentForm.isDefaultMethod());
        customerPayment.getAdditionalFields().put(PaymentAdditionalFieldType.EXP_DATE.getType(), paymentForm.getExpiration());
        customerPayment.getAdditionalFields().put(PaymentAdditionalFieldType.LAST_FOUR.getType(), paymentForm.getLastFourDigits());
        customerPayment.getAdditionalFields().put(PaymentAdditionalFieldType.PAYMENT_NAME.getType(), paymentForm.getPaymentName());
        customerPayment.getAdditionalFields().put(PaymentAdditionalFieldType.NAME_ON_CARD.getType(), paymentForm.getPersonName());
        //customerPayment.getAdditionalFields().put(PaymentAdditionalFieldType.CARD_TYPE.getType(), paymentForm.getPersonName());
        customerPayment.setBillingAddress(billingForm.getAddress());
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

    @InitBinder
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {

        /**
         * @deprecated - address.setState() is deprecated in favor of ISO standardization
         * This is here for legacy compatibility
         */
        binder.registerCustomEditor(State.class, "address.state", new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                if (StringUtils.isNotEmpty(text)) {
                    State state = stateService.findStateByAbbreviation(text);
                    setValue(state);
                } else {
                    setValue(null);
                }
            }
        });

        /**
         * @deprecated - address.setCountry() is deprecated in favor of ISO standardization
         * This is here for legacy compatibility
         */
        binder.registerCustomEditor(Country.class, "address.country", new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                if (StringUtils.isNotEmpty(text)) {
                    Country country = countryService.findCountryByAbbreviation(text);
                    setValue(country);
                } else {
                    setValue(null);
                }
            }
        });

        binder.registerCustomEditor(ISOCountry.class, "address.isoCountryAlpha2", new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                if (StringUtils.isNotEmpty(text)) {
                    ISOCountry isoCountry = isoService.findISOCountryByAlpha2Code(text);
                    setValue(isoCountry);
                }else {
                    setValue(null);
                }
            }
        });

        binder.registerCustomEditor(Phone.class, "address.phonePrimary", new PropertyEditorSupport() {

            @Override
            public void setAsText(String text) {
                Phone phone = new PhoneImpl();
                phone.setPhoneNumber(text);
                setValue(phone);
            }

        });

        binder.registerCustomEditor(Phone.class, "address.phoneSecondary", new PropertyEditorSupport() {

            @Override
            public void setAsText(String text) {
                Phone phone = new PhoneImpl();
                phone.setPhoneNumber(text);
                setValue(phone);
            }

        });

        binder.registerCustomEditor(Phone.class, "address.phoneFax", new PropertyEditorSupport() {

            @Override
            public void setAsText(String text) {
                Phone phone = new PhoneImpl();
                phone.setPhoneNumber(text);
                setValue(phone);
            }

        });
    }
}
