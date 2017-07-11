package org.broadleafcommerce.core.web.controller.account;

import org.broadleafcommerce.common.web.controller.BroadleafAbstractController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * This is the page controller for adding, updating, and deleting saved payment information.
 *
 * @author Jacob Mitash
 */
@Controller
@RequestMapping("/account/payments")
public class BroadleafManageCustomerPaymentsController extends BroadleafAbstractController {

    protected static String customerPaymentView = "account/manageCustomerPayments";
    protected static String customerPaymentRedirect = "redirect:/account/payments";

    @RequestMapping(method = RequestMethod.GET)
    public String viewCustomerPayments(HttpServletRequest request, Model model) {
        //TODO: update model

        return getCustomerPaymentView();
    }

    @RequestMapping(value = "/{customerPaymentId}/", method = RequestMethod.GET)
    public String viewCustomerPayment(HttpServletRequest request, Model model, @PathVariable("customerPaymentId") Long customerPaymentId) {
        //TODO: update model

        return getCustomerPaymentView();
    }

    @RequestMapping(value = "/{customerPaymentId}/", method = RequestMethod.POST)
    public String updateCustomerPayment(HttpServletRequest request, Model model, @PathVariable("customerPaymentId") Long customerPaymentId) {
        //TODO: update model, add form

        return getCustomerPaymentRedirect();
    }

    @RequestMapping(value = "/{customerPaymentId}/", method = RequestMethod.POST, params="removePayment=Remove")
    public String removeCustomerPayment(HttpServletRequest request, Model model, @PathVariable("customerPaymentId") Long customerPaymentId) {
        //TODO: update model, add form

        return getCustomerPaymentRedirect();
    }

    public String getCustomerPaymentView() {
        return customerPaymentView;
    }

    public String getCustomerPaymentRedirect() {
        return customerPaymentRedirect;
    }
}
