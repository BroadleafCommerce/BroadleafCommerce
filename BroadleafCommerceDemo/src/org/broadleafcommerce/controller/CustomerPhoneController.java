package org.broadleafcommerce.controller;

import javax.annotation.Resource;

import org.apache.commons.validator.GenericValidator;
import org.broadleafcommerce.controller.validator.PhoneValidator;
import org.broadleafcommerce.profile.domain.CustomerPhone;
import org.broadleafcommerce.profile.domain.CustomerPhoneImpl;
import org.broadleafcommerce.profile.domain.PhoneImpl;
import org.broadleafcommerce.profile.service.CustomerPhoneService;
import org.broadleafcommerce.profile.web.CustomerState;
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
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

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

    private String viewPhoneSuccessView = "";

    private String VIEW_PHONE_SUCCESS = "success"; //update to full JSP URL as default

    public CustomerPhoneController() {
        this.customerPhoneService = null;
        this.customerState = null;
        this.phoneValidator = null;
        this.phoneFormatter = null;
    }

    @RequestMapping(method =  {
            RequestMethod.GET, RequestMethod.POST}
    )
    public String deletePhone(@RequestParam(required = true)
            Long customerPhoneId, Model model, WebRequest request) {
        customerPhoneService.deleteCustomerPhoneByIdAndCustomerId(customerPhoneId, customerState.getCustomerId(request));
        return "success";
    }

    @RequestMapping(method =  {
            RequestMethod.GET, RequestMethod.POST}
    )
    public String makePhoneDefault(@RequestParam(required = true)
            Long customerPhoneId, Model model, WebRequest request) {
        //TODO: check to see if this can be refactored to make one service call to pass in customerPhoneId to set to default
        CustomerPhone customerPhone = customerPhoneService.readCustomerPhoneByIdAndCustomerId(customerPhoneId, customerState.getCustomerId(request));
        customerPhoneService.makeCustomerPhoneDefault(customerPhone.getId(), customerPhone.getCustomerId());
        model.addAttribute(CONFIRMATION_MSG, "Your phone has been set to default.");

        return "success";
    }

    //TODO: could ModelAttribute name be changed?
    @RequestMapping(method =  {
            RequestMethod.GET, RequestMethod.POST}
    )
    public ModelAndView savePhone(
    		@ModelAttribute("phoneNameForm")PhoneNameForm phoneNameForm, 
    		BindingResult errors, WebRequest request) {
        if (GenericValidator.isBlankOrNull(phoneNameForm.getPhoneName())) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phoneName", "phoneName.required");
        }
        phoneFormatter.formatPhoneNumber(phoneNameForm.getPhone());
        errors.pushNestedPath("phone");
        phoneValidator.validate(phoneNameForm.getPhone(), errors);
        errors.popNestedPath();

        if (! errors.hasErrors()) {
            // TODO: lookup customerPhone using bl syntax
            CustomerPhone customerPhone = new CustomerPhoneImpl();
            customerPhone.setCustomerId(customerState.getCustomerId(request));
            customerPhone.setPhoneName(phoneNameForm.getPhoneName());
            customerPhone.setPhone(phoneNameForm.getPhone());
            customerPhoneService.saveCustomerPhone(customerPhone);
            //TODO return path from variable
            return new ModelAndView(new RedirectView("success")).addObject("confirmationMessage","The stuff was completed.");
        } else {
        	ModelAndView mv = new ModelAndView(new RedirectView("success")); 
        	mv.getModel().put("confirmationMessage", "some random confirmation message");
        	
            return mv; 
        }
    }

    @RequestMapping(method =  {
            RequestMethod.GET, RequestMethod.POST}
    )
    public String viewPhone(@RequestParam(required = false) Long customerPhoneId,  WebRequest request, @ModelAttribute("phoneNameForm") PhoneNameForm phoneNameForm) {
        if (customerPhoneId == null) {
            return "success"; // TODO: look this up
        } else {
            CustomerPhone cPhone = customerPhoneService.readCustomerPhoneByIdAndCustomerId(customerPhoneId, customerState.getCustomerId(request));
            if (cPhone != null) {
                // TODO: verify this is the current customers phone
                phoneNameForm.setPhone(cPhone.getPhone());
                phoneNameForm.setPhoneName(cPhone.getPhoneName());
                return "success";
            } else {
                return "error"; // TODO: look this up
            }
        }
    }

    @ModelAttribute("phoneNameForm")
    public PhoneNameForm initPhoneNameForm(WebRequest request) {
        PhoneNameForm form = new PhoneNameForm();
        // TODO: Use broadleaf standard for constructing an entity object
        form.setPhone(new PhoneImpl());
        return form;
    }
}
