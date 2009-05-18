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

@Controller("blCustomerPhoneController")
public class CustomerPhoneController {
    private static final String CONFIRMATION_MSG = "confirmationMessage";
    @Resource
    private final CustomerPhoneService customerPhoneService;
    @Resource
    private final CustomerState customerState;
    @Resource
    private final PhoneFormatter phoneFormatter;
    @Resource
    private final PhoneValidator phoneValidator;
    @Resource
    private final CustomerPhoneValidator customerPhoneValidator;
    @Resource
    private EntityConfiguration entityConfiguration;

    private String viewPhoneSuccessView = "";

    private String VIEW_PHONE_SUCCESS = "success"; //update to full JSP URL as default

    public CustomerPhoneController() {
        this.customerPhoneService = null;
        this.customerState = null;
        this.phoneValidator = null;
        this.phoneFormatter = null;
        this.customerPhoneValidator = null;
    }

    @RequestMapping(method =  {
            RequestMethod.GET, RequestMethod.POST}
    )
    public String deletePhone(@RequestParam(required = true)
            Long customerPhoneId, Model model, HttpServletRequest request) {
        customerPhoneService.deleteCustomerPhoneByIdAndCustomerId(customerPhoneId, customerState.getCustomerId(request));
        return "redirect:success";
    }

    @RequestMapping(method =  {
            RequestMethod.GET, RequestMethod.POST}
    )
    public String makePhoneDefault(@RequestParam(required = true)
            Long customerPhoneId, Model model, HttpServletRequest request) {
        //TODO: check to see if this can be refactored to make one service call to pass in customerPhoneId to set to default
        CustomerPhone customerPhone = customerPhoneService.readCustomerPhoneByIdAndCustomerId(customerPhoneId, customerState.getCustomerId(request));
        customerPhoneService.makeCustomerPhoneDefault(customerPhone.getId(), customerPhone.getCustomerId());
        model.addAttribute(CONFIRMATION_MSG, "Your phone has been set to default.");

        return "redirect:success";
    }

    @RequestMapping(method =  {
            RequestMethod.GET, RequestMethod.POST}
    )
    public String savePhone(
            @ModelAttribute("phoneNameForm")PhoneNameForm phoneNameForm,
            BindingResult errors, HttpServletRequest request) {
        if (GenericValidator.isBlankOrNull(phoneNameForm.getPhoneName())) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phoneName", "phoneName.required");
        }
        phoneFormatter.formatPhoneNumber(phoneNameForm.getPhone());
        errors.pushNestedPath("phone");
        phoneValidator.validate(phoneNameForm.getPhone(), errors);
        errors.popNestedPath();

        if (! errors.hasErrors()) {
            CustomerPhone customerPhone = (CustomerPhone) entityConfiguration.createEntityInstance("org.broadleafcommerce.profile.domain.CustomerPhone");
            customerPhone.setCustomerId(customerState.getCustomerId(request));
            customerPhone.setPhoneName(phoneNameForm.getPhoneName());
            customerPhone.setPhone(phoneNameForm.getPhone());

            customerPhoneValidator.validate(customerPhone, errors);

            if (! errors.hasErrors()) {
                customerPhoneService.saveCustomerPhone(customerPhone);
                request.setAttribute("customerPhoneId", customerPhone.getId());
            }
            //TODO return path from variable
            return "success";
        } else {
            return "success";
        }
    }

    @RequestMapping(method =  {
            RequestMethod.GET, RequestMethod.POST}
    )
    public String viewPhone(@RequestParam(required = false) Long customerPhoneId,  HttpServletRequest request,
            @ModelAttribute("phoneNameForm") PhoneNameForm phoneNameForm,
            BindingResult errors) {
        if (customerPhoneId == null) {
            return "success"; // TODO: look this up
        } else {
            CustomerPhone cPhone = customerPhoneService.readCustomerPhoneByIdAndCustomerId(customerPhoneId, customerState.getCustomerId(request));
            if (cPhone != null) {
                // TODO: verify this is the current customers phone
                phoneNameForm.setPhone(cPhone.getPhone());
                phoneNameForm.setPhoneName(cPhone.getPhoneName());
                request.setAttribute("customerPhoneId", cPhone.getId());
                return "success";
            } else {
                //TODO redirect the user to an error page
                return "errors"; // TODO: look this up
            }
        }
    }

    @ModelAttribute("phoneNameForm")
    public PhoneNameForm initPhoneNameForm(HttpServletRequest request) {
        PhoneNameForm form = new PhoneNameForm();
        form.setPhone((Phone) entityConfiguration.createEntityInstance("org.broadleafcommerce.profile.domain.Phone"));
        return form;
    }
}
