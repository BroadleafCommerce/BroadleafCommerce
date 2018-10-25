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
package org.broadleafcommerce.core.web.controller.account;

import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerAddress;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

public class BroadleafManageCustomerAddressesController extends AbstractCustomerAddressController {

    @Value("${validate.customer.owned.data:true}")
    protected boolean validateCustomerOwnedData;

    protected String addressUpdatedMessage = "Address successfully updated";
    protected String addressAddedMessage = "Address successfully added";
    protected String addressRemovedMessage = "Address successfully removed";
    protected String addressRemovedErrorMessage = "Address could not be removed as it is in use";

    public String viewCustomerAddresses(HttpServletRequest request, Model model) {
        model.addAttribute("customerAddressForm", new CustomerAddressForm());
        return getCustomerAddressesView();
    }
    
    public String viewCustomerAddress(HttpServletRequest request, Model model, Long customerAddressId) {
        CustomerAddress customerAddress = customerAddressService.readCustomerAddressById(customerAddressId);
        if (customerAddress == null) {
            throw new IllegalArgumentException("Customer Address not found with the specified customerAddressId");
        }

        validateCustomerOwnedData(customerAddress);

        CustomerAddressForm form = new CustomerAddressForm();
        form.setAddress(customerAddress.getAddress());
        form.setAddressName(customerAddress.getAddressName());
        form.setCustomerAddressId(customerAddress.getId());
        model.addAttribute("customerAddressForm", form);
        return getCustomerAddressesView();
    }

    public String addCustomerAddress(HttpServletRequest request, Model model, CustomerAddressForm form, BindingResult result, RedirectAttributes redirectAttributes) throws ServiceException {
        addressService.populateAddressISOCountrySub(form.getAddress());
        customerAddressValidator.validate(form, result);
        if (result.hasErrors()) {
            return getCustomerAddressesView();
        }

        removeUnusedPhones(form);
        
        Address address = addressService.saveAddress(form.getAddress());
        CustomerAddress customerAddress = customerAddressService.create();
        customerAddress.setAddress(address);
        customerAddress.setAddressName(form.getAddressName());
        customerAddress.setCustomer(CustomerState.getCustomer());
        customerAddress = customerAddressService.saveCustomerAddress(customerAddress);
        if (form.getAddress().isDefault()) {
            customerAddressService.makeCustomerAddressDefault(customerAddress.getId(), customerAddress.getCustomer().getId());
        }

        form.setCustomerAddressId(customerAddress.getId());

        if (!isAjaxRequest(request)) {
            List<CustomerAddress> addresses = customerAddressService.readActiveCustomerAddressesByCustomerId(CustomerState.getCustomer().getId());
            model.addAttribute("addresses", addresses);
        }
        redirectAttributes.addFlashAttribute("successMessage", getAddressAddedMessage());
        return getCustomerAddressesRedirect();
    }
    
    public String updateCustomerAddress(HttpServletRequest request, Model model, Long customerAddressId, CustomerAddressForm form, BindingResult result, RedirectAttributes redirectAttributes) throws ServiceException {
        customerAddressValidator.validate(form, result);
        if (result.hasErrors()) {
            return getCustomerAddressesView();
        }

        if ((form.getAddress().getPhonePrimary() != null) &&
                (StringUtils.isEmpty(form.getAddress().getPhonePrimary().getPhoneNumber()))) {
            form.getAddress().setPhonePrimary(null);
        }
        if ((form.getAddress().getPhoneSecondary() != null) &&
                (StringUtils.isEmpty(form.getAddress().getPhoneSecondary().getPhoneNumber()))) {
            form.getAddress().setPhoneSecondary(null);
        }
        if ((form.getAddress().getPhoneFax() != null) &&
                (StringUtils.isEmpty(form.getAddress().getPhoneFax().getPhoneNumber()))) {
            form.getAddress().setPhoneFax(null);
        }

        CustomerAddress customerAddress = customerAddressService.readCustomerAddressById(customerAddressId);
        if (customerAddress == null) {
            throw new IllegalArgumentException("Customer Address not found with the specified customerAddressId");
        }

        validateCustomerOwnedData(customerAddress);

        customerAddress.setAddress(form.getAddress());
        customerAddress.setAddressName(form.getAddressName());
        customerAddress = customerAddressService.saveCustomerAddress(customerAddress);
        if (form.getAddress().isDefault()) {
            customerAddressService.makeCustomerAddressDefault(customerAddress.getId(), customerAddress.getCustomer().getId());
        }
        redirectAttributes.addFlashAttribute("successMessage", getAddressUpdatedMessage());
        return getCustomerAddressesRedirect();
    }
    
    public String removeCustomerAddress(HttpServletRequest request, Model model, Long customerAddressId, RedirectAttributes redirectAttributes) {
        try {
            CustomerAddress customerAddress = customerAddressService.readCustomerAddressById(customerAddressId);

            // we don't care if the address is null on a remove
            if (customerAddress != null) {
                validateCustomerOwnedData(customerAddress);
                customerAddressService.deleteCustomerAddressById(customerAddressId);
            }

            redirectAttributes.addFlashAttribute("successMessage", getAddressRemovedMessage());
        } catch (DataIntegrityViolationException e) {
            // This likely occurred because there is an order or cart in the system that is currently utilizing this
            // address. Therefore, we're not able to remove it as it breaks a foreign key constraint
            redirectAttributes.addFlashAttribute("errorMessage", getAddressRemovedErrorMessage());
        }
        return getCustomerAddressesRedirect();
    }

    public void removeUnusedPhones(CustomerAddressForm form) {
        if ((form.getAddress().getPhonePrimary() != null) &&
                    (StringUtils.isEmpty(form.getAddress().getPhonePrimary().getPhoneNumber()))) {
            form.getAddress().setPhonePrimary(null);
        }
        if ((form.getAddress().getPhoneSecondary() != null) &&
                    (StringUtils.isEmpty(form.getAddress().getPhoneSecondary().getPhoneNumber()))) {
            form.getAddress().setPhoneSecondary(null);
        }
        if ((form.getAddress().getPhoneFax() != null) &&
                    (StringUtils.isEmpty(form.getAddress().getPhoneFax().getPhoneNumber()))) {
            form.getAddress().setPhoneFax(null);
        }
    }

    public String getAddressUpdatedMessage() {
        return addressUpdatedMessage;
    }

    public String getAddressAddedMessage() {
        return addressAddedMessage;
    }

    public String getAddressRemovedMessage() {
        return addressRemovedMessage;
    }
    
    public String getAddressRemovedErrorMessage() {
        return addressRemovedErrorMessage;
    }

    protected void validateCustomerOwnedData(CustomerAddress customerAddress) {
        if (validateCustomerOwnedData) {
            Customer activeCustomer = CustomerState.getCustomer();
            if (activeCustomer != null
                    && !(activeCustomer.equals(customerAddress.getCustomer()))) {
                throw new SecurityException("The active customer does not own the object that they are trying to view, edit, or remove.");
            }

            if (activeCustomer == null && customerAddress.getCustomer() != null) {
                throw new SecurityException("The active customer does not own the object that they are trying to view, edit, or remove.");
            }
        }
    }
    
}
