package org.broadleafcommerce.core.web.controller.account;

import org.broadleafcommerce.common.web.controller.BroadleafAbstractController;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerPayment;
import org.broadleafcommerce.profile.core.service.CustomerPaymentService;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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

    @Resource(name = "blCustomerPaymentService")
    protected CustomerPaymentService customerPaymentService;

    public String viewCustomerPayments(HttpServletRequest request, Model model) {
        //TODO: update model

        return getCustomerPaymentView();
    }

    public String viewCustomerPayment(HttpServletRequest request, Model model, Long customerPaymentId) {
        //TODO: update model, add form

        return getCustomerPaymentView();
    }

    public String updateCustomerPayment(HttpServletRequest request, Model model, Long customerPaymentId) {
        //TODO: update model, add form

        return getCustomerPaymentRedirect();
    }

    public String removeCustomerPayment(HttpServletRequest request, Model model, Long customerPaymentId) {
        //TODO: update model, add form

        return getCustomerPaymentRedirect();
    }

    protected void validateCustomerOwnedData(CustomerPayment customerPayment) {
        if (validateCustomerOwnedData) {
            Customer activeCustomer = CustomerState.getCustomer();
            if (activeCustomer != null
                    && !(activeCustomer.equals(customerPayment.getCustomer()))) {
                throw new SecurityException("The active customer does not own the object that they are trying to view, edit, or remove.");
            }

            if (activeCustomer == null && customerPayment.getCustomer() != null) {
                throw new SecurityException("The active customer does not own the object that they are trying to view, edit, or remove.");
            }
        }
    }

    public String getCustomerPaymentView() {
        return customerPaymentView;
    }

    public String getCustomerPaymentRedirect() {
        return customerPaymentRedirect;
    }
}
