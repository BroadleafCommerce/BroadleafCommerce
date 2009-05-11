package org.broadleafcommerce.profile.web.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.broadleafcommerce.profile.domain.CustomerPhone;
import org.broadleafcommerce.profile.domain.CustomerPhoneImpl;
import org.broadleafcommerce.profile.domain.Phone;
import org.broadleafcommerce.profile.service.CustomerPhoneService;
import org.broadleafcommerce.profile.web.CustomerState;
import org.broadleafcommerce.profile.web.controller.validator.PhoneValidator;
import org.broadleafcommerce.profile.web.util.PhoneFormatter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller("blCustomerPhoneController")
public class CustomerPhoneController {

    @Resource
    private final CustomerPhoneService customerPhoneService;
    @Resource
    private final PhoneValidator phoneValidator;
    @Resource
    private final PhoneFormatter phoneFormatter;
    @Resource
    private final CustomerState customerState;

    public CustomerPhoneController(){
        this.customerPhoneService = null;
        this.customerState = null;
        this.phoneValidator = null;
        this.phoneFormatter = null;
    }

    //TODO: remove?
    public CustomerPhoneController(CustomerPhoneService customerPhoneService,
            CustomerState customerState) {
        this.customerPhoneService = customerPhoneService;
        this.customerState = customerState;
        this.phoneValidator = null;
        this.phoneFormatter = null;
    }

    /*    //TODO: remove?
    public CustomerPhoneController(CustomerPhoneService customerPhoneService,
            CustomerState customerState, PhoneValidator phoneValidator, PhoneFormatter phoneFormatter) {
        this.customerPhoneService = customerPhoneService;
        this.customerState = customerState;
        this.phoneValidator = phoneValidator;
        this.phoneFormatter = phoneFormatter;
    }
     */
    @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST })
    public String viewPhone(@RequestParam(required = false) Long customerPhoneId, Model model, HttpServletRequest request) {
        CustomerPhone customerPhone = null;
        if (customerPhoneId == null) {
            customerPhone = initCustomerPhone(request);
        } else {
            customerPhone = customerPhoneService.readCustomerPhoneByIdAndCustomerId(customerPhoneId, customerState.getCustomerId(request));
        }
        model.addAttribute("phone", customerPhone.getPhone());
        model.addAttribute("phoneName", customerPhone.getPhoneName());
        //model.addAttribute("customerPhone", customerPhone);
        return "success";
    }

    @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST })
    public String makePhoneDefault(@RequestParam(required = true) Long customerPhoneId, HttpServletRequest request) {
        //TODO: check to see if this can be refactored to make one service call to pass in customerPhoneId to set to default
        CustomerPhone customerPhone = customerPhoneService.readCustomerPhoneByIdAndCustomerId(customerPhoneId, customerState.getCustomerId(request));
        customerPhoneService.makeCustomerPhoneDefault(customerPhone.getId(), customerPhone.getCustomerId());
        return "success";
    }

    @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST })
    //TODO: could ModelAttribute name be changed?
    public String savePhone(@ModelAttribute("phone") Phone phone, @ModelAttribute("phoneName") String phoneName,
            BindingResult bindResult, Model model, HttpServletRequest request) {
        phoneFormatter.formatPhoneNumber(phone);
        //        bindResult.setNestedPath("phone");
        phoneValidator.validate(phone, bindResult);
        //        bindResult.setNestedPath("");
        if (!bindResult.hasErrors()) {
            //build customerPhone
            CustomerPhone customerPhone = new CustomerPhoneImpl();
            customerPhone.setCustomerId(customerState.getCustomerId(request));
            customerPhone.setPhoneName(phoneName);
            customerPhone.setPhone(phone);
            customerPhoneService.saveCustomerPhone(customerPhone);
            return "success";
        }
        model.addAttribute("phone", phone);
        return "success";
    }

    @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST })
    public String deletePhone(@RequestParam(required = true) Long customerPhoneId, HttpServletRequest request) {
        customerPhoneService.deleteCustomerPhoneByIdAndCustomerId(customerPhoneId, customerState.getCustomerId(request));
        return "success";
    }

    public CustomerPhone initCustomerPhone(HttpServletRequest request) {
        return new CustomerPhoneImpl(customerState.getCustomerId(request));
    }
}