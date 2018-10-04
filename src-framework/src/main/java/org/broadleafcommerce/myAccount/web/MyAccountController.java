package org.broadleafcommerce.myAccount.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class MyAccountController {

    @RequestMapping(method = RequestMethod.GET)
    public String myAccount(ModelMap model, HttpServletRequest request) {
        return "/myAccount/myAccount";
    }
}