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
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.CustomerAddress;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

public class BroadleafManageCustomerAddressesController extends AbstractCustomerAddressController {

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
        CustomerAddressForm form = new CustomerAddressForm();
        form.setAddress(customerAddress.getAddress());
        form.setAddressName(customerAddress.getAddressName());
        form.setCustomerAddressId(customerAddress.getId());
        model.addAttribute("customerAddressForm", form);
        return getCustomerAddressesView();
    }

    public String addCustomerAddress(HttpServletRequest request, Model model, CustomerAddressForm form, BindingResult result, RedirectAttributes redirectAttributes) throws ServiceException {
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
            customerAddressService.deleteCustomerAddressById(customerAddressId);
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
    
}
