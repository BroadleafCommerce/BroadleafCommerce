/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.profile.web.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.validator.GenericValidator;
import org.broadleafcommerce.profile.domain.CustomerPhone;
import org.broadleafcommerce.profile.domain.Phone;
import org.broadleafcommerce.profile.service.CustomerPhoneService;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.broadleafcommerce.profile.web.CustomerState;
import org.broadleafcommerce.profile.web.controller.validator.CustomerPhoneValidator;
import org.broadleafcommerce.profile.web.controller.validator.PhoneValidator;
import org.broadleafcommerce.profile.web.model.PhoneNameForm;
import org.broadleafcommerce.profile.web.util.PhoneFormatter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Provides access and mutator functions to manage a customer's phones.
 *
 * @author sconlon
 */
@Controller("blCustomerPhoneController")
public class CustomerPhoneController {
    private static final String prefix = "myAccount/phone/customerPhones";
    private static final String redirect = "redirect:/myaccount/phone/viewPhone.htm";
    @Resource
    private CustomerPhoneService customerPhoneService;
    @Resource
    private CustomerPhoneValidator customerPhoneValidator;
    @Resource
    private CustomerState customerState;
    @Resource
    private EntityConfiguration entityConfiguration;
    @Resource
    private PhoneFormatter phoneFormatter;
    @Resource
    private PhoneValidator phoneValidator;

    /* ??? -
     * Will these static defaults alter their ability to be
     * overwritten via the appContext?  TODO scc: test this scenario
     * */
    private String deletePhoneSuccessUrl = redirect;
    private String makePhoneDefaultSuccessUrl = redirect;
    private String savePhoneErrorUrl = prefix;
    private String savePhoneSuccessUrl = prefix;
    private String viewPhoneErrorUrl = prefix;
    private String viewPhoneSuccessUrl = prefix;

    /**
     * Completely deletes the customerPhone with the given customerPhoneId from the database.
     * 
     * @param customerPhoneId
     * @param request
     *
     * @return
     */
    @RequestMapping(method =  {
            RequestMethod.GET, RequestMethod.POST}
    )
    public String deletePhone(@RequestParam(required = true)
            Long customerPhoneId, HttpServletRequest request) {
        customerPhoneService.deleteCustomerPhoneByIdAndCustomerId(customerPhoneId, customerState.getCustomerId(request));

        request.setAttribute("phone.deletedPhone", "true");

        return deletePhoneSuccessUrl + customerPhoneId;
    }

    /**
     * Called before each and every request comes into the controller, and is placed on the request for use by those methods.
     * 
     *
     * @param request
     * @param model
     *
     * @return
     */
    @ModelAttribute("phoneNameForm")
    public PhoneNameForm initPhoneNameForm(HttpServletRequest request, Model model) {
        PhoneNameForm form = new PhoneNameForm();
        form.setPhone((Phone) entityConfiguration.createEntityInstance("org.broadleafcommerce.profile.domain.Phone"));

        return form;
    }

    /**
     * Sets the passed in customerPhoneId as the default phone for the user.
     *
     * @param customerPhoneId
     * @param request
     *
     * @return
     */
    @RequestMapping(method =  {
            RequestMethod.GET, RequestMethod.POST}
    )
    public String makePhoneDefault(@RequestParam(required = true)
            Long customerPhoneId, HttpServletRequest request) {
        //TODO: check to see if this can be refactored to make one service call to pass in customerPhoneId to set to default
        CustomerPhone customerPhone = customerPhoneService.readCustomerPhoneByIdAndCustomerId(customerPhoneId, customerState.getCustomerId(request));
        customerPhoneService.makeCustomerPhoneDefault(customerPhone.getId(), customerPhone.getCustomerId());

        request.setAttribute("phone.madePhoneDefault", "true");

        return makePhoneDefaultSuccessUrl;
    }

    /**
     * Creates a new phone if no customerPhoneId & phoneId are passed in; otherwise, it creates a new customerPhone object otherwise.  If they are passed in,
     *  it is assumed that there is an update.
     *
     * @param phoneNameForm
     * @param errors
     * @param request
     * @param customerPhoneId DOCUMENT ME!
     * @param phoneId DOCUMENT ME!
     *
     * @return
     */
    @RequestMapping(method =  {
            RequestMethod.GET, RequestMethod.POST}
    )
    public String savePhone(@ModelAttribute("phoneNameForm")
            PhoneNameForm phoneNameForm, BindingResult errors, HttpServletRequest request, @RequestParam(required = false)
            Long customerPhoneId, @RequestParam(required = false)
            Long phoneId) {
        if (GenericValidator.isBlankOrNull(phoneNameForm.getPhoneName())) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phoneName", "phoneName.required");
        }

        if(phoneId != null){
            phoneNameForm.getPhone().setId(phoneId);
        }

        phoneFormatter.formatPhoneNumber(phoneNameForm.getPhone());
        errors.pushNestedPath("phone");
        phoneValidator.validate(phoneNameForm.getPhone(), errors);
        errors.popNestedPath();

        if (!errors.hasErrors()) {
            CustomerPhone customerPhone = (CustomerPhone) entityConfiguration.createEntityInstance("org.broadleafcommerce.profile.domain.CustomerPhone");
            customerPhone.setCustomerId(customerState.getCustomerId(request));
            customerPhone.setPhoneName(phoneNameForm.getPhoneName());
            customerPhone.setPhone(phoneNameForm.getPhone());

            if ((customerPhoneId != null) && (customerPhoneId > 0)) {
                customerPhone.setId(customerPhoneId);
            }

            customerPhoneValidator.validate(customerPhone, errors);

            if (!errors.hasErrors()) {
                customerPhoneService.saveCustomerPhone(customerPhone);
                request.setAttribute("customerPhoneId", customerPhone.getId());
                request.setAttribute("phoneId", customerPhone.getPhone().getId());
            }

            return savePhoneSuccessUrl;
        } else {
            return savePhoneErrorUrl;
        }
    }

    public void setCustomerPhoneService(CustomerPhoneService customerPhoneService) {
        this.customerPhoneService = customerPhoneService;
    }

    public void setCustomerPhoneValidator(CustomerPhoneValidator customerPhoneValidator) {
        this.customerPhoneValidator = customerPhoneValidator;
    }

    public void setCustomerState(CustomerState customerState) {
        this.customerState = customerState;
    }

    public void setDeletePhoneSuccessUrl(String deletePhoneSuccessUrl) {
        this.deletePhoneSuccessUrl = deletePhoneSuccessUrl;
    }

    public void setEntityConfiguration(EntityConfiguration entityConfiguration) {
        this.entityConfiguration = entityConfiguration;
    }

    public void setMakePhoneDefaultSuccessUrl(String makePhoneDefaultSuccessUrl) {
        this.makePhoneDefaultSuccessUrl = makePhoneDefaultSuccessUrl;
    }

    public void setPhoneFormatter(PhoneFormatter phoneFormatter) {
        this.phoneFormatter = phoneFormatter;
    }

    public void setPhoneValidator(PhoneValidator phoneValidator) {
        this.phoneValidator = phoneValidator;
    }

    public void setSavePhoneErrorUrl(String savePhoneErrorUrl) {
        this.savePhoneErrorUrl = savePhoneErrorUrl;
    }

    public void setSavePhoneSuccessUrl(String savePhoneSuccessUrl) {
        this.savePhoneSuccessUrl = savePhoneSuccessUrl;
    }

    public void setViewPhoneErrorUrl(String viewPhoneErrorUrl) {
        this.viewPhoneErrorUrl = viewPhoneErrorUrl;
    }

    public void setViewPhoneSuccessUrl(String viewPhoneSuccessUrl) {
        this.viewPhoneSuccessUrl = viewPhoneSuccessUrl;
    }

    /**
     * Provides a blank template for a new Customer Phone to be created if no customerPhoneId is provided.
     *  Otherwise, when a customerPhoneId is provided, the associated customerPhone object is retrieved,
     *  and placed on the request.
     * 
     * @param customerPhoneId
     * @param request
     * @param phoneNameForm
     * @param errors
     * @return
     */
    @RequestMapping(method =  {
            RequestMethod.GET, RequestMethod.POST}
    )
    public String viewPhone(@RequestParam(required = false)
            Long customerPhoneId, HttpServletRequest request, @ModelAttribute("phoneNameForm")
            PhoneNameForm phoneNameForm, BindingResult errors) {
        if (customerPhoneId == null) {
            return viewPhoneSuccessUrl;
        } else {
            Long currCustomerId = customerState.getCustomerId(request);
            CustomerPhone cPhone = customerPhoneService.readCustomerPhoneByIdAndCustomerId(customerPhoneId, currCustomerId);

            if (cPhone != null) {
                // TODO: verify this is the current customers phone
                //? - do we really need this since we read the phone with the currCustomerId?
                if (!cPhone.getCustomerId().equals(currCustomerId)) {
                    return viewPhoneErrorUrl;
                }

                phoneNameForm.setPhone(cPhone.getPhone());
                phoneNameForm.setPhoneName(cPhone.getPhoneName());
                request.setAttribute("customerPhoneId", cPhone.getId());
                request.setAttribute("phoneId", cPhone.getPhone().getId());

                return viewPhoneSuccessUrl;
            } else {
                return viewPhoneErrorUrl;
            }
        }
    }
}
