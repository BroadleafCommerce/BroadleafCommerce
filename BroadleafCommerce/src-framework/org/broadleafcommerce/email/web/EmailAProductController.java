package org.broadleafcommerce.email.web;


import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.email.domain.EmailAProduct;
import org.broadleafcommerce.email.domain.EmailListType;
import org.broadleafcommerce.email.service.EmailListService;
import org.broadleafcommerce.email.service.EmailWebService;
import org.broadleafcommerce.email.service.validator.EmailAProductValidator;
import org.broadleafcommerce.email.service.validator.EmailListRequest;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.web.CustomerState;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public class EmailAProductController {

    private final String defaultSubject;
    private final String defaultMessage;
    private final EmailListService emailListService; //TCS version
    private final EmailWebService emailWebService;	//TCS version
    private final CustomerState customerState;

    public EmailAProductController(EmailListService emailListService, EmailWebService emailWebService, CustomerState customerState, String defaultSubject, String defaultMessage) {
        this.emailListService = emailListService;
        this.emailWebService = emailWebService;
        this.customerState = customerState;
        this.defaultSubject = defaultSubject;
        this.defaultMessage = defaultMessage;
    }

    @RequestMapping(value = "emailAProduct.htm", method = RequestMethod.GET)
    public String showEmailAProduct(ModelMap model, HttpServletRequest request) {
        EmailAProduct emailAProduct = new EmailAProduct();
        Customer currentCustomer = customerState.getCustomer(request);
        if (currentCustomer != null) {
            emailAProduct.setSenderEmail(currentCustomer.getEmailAddress());
        }
        emailAProduct.setEmailSubject(defaultSubject);
        emailAProduct.setEmailMessage(defaultMessage);
        model.addAttribute("emailAProduct", emailAProduct);
        return "email/emailAProduct";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String emailAProduct(ModelMap model, HttpServletRequest request, @ModelAttribute EmailAProduct emailAProduct, Errors errors) {
        new EmailAProductValidator().validate(emailAProduct, errors);
        if (errors.hasErrors()) {
            if ("".equals(StringUtils.trim(emailAProduct.getEmailMessage()))) {
                emailAProduct.setEmailMessage(defaultMessage);
            }
            if ("".equals(StringUtils.trim(emailAProduct.getEmailSubject()))) {
                emailAProduct.setEmailMessage(defaultSubject);
            }
            model.addAttribute("emailAProduct", emailAProduct);
            return "email/emailAProduct";
        }
        if (emailAProduct.getEmailSubject() == null || emailAProduct.getEmailSubject().trim().length() == 0 ) {
            emailAProduct.setEmailSubject(defaultSubject);
        }
        if (emailAProduct.getEmailMessage() == null || emailAProduct.getEmailMessage().trim().length() == 0 ) {
            emailAProduct.setEmailMessage(defaultMessage);
        }
        if (emailAProduct.isSignUpForEmail()) {
            if (!emailListService.isOnList(emailAProduct.getSenderEmail(), EmailListType.MASTER)) {
                EmailListRequest emailListRequest = new EmailListRequest(emailAProduct.getSenderEmail());
                emailListRequest.setSendConfirmationEmail(true);
                emailListRequest.generateComment(request.getRemoteHost(), request.getHeader("USER-AGENT"));
                emailListService.subscribe(emailListRequest);
            }
        }

        emailWebService.sendEmailAProduct(emailAProduct);
        return "redirect:" + emailAProduct.getProductUrl();
    }

}
