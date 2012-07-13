package org.broadleafcommerce.core.web.controller.account;

import org.broadleafcommerce.common.web.controller.BroadleafAbstractController;
import org.broadleafcommerce.core.web.controller.account.validator.ChangePasswordValidator;
import org.broadleafcommerce.core.web.controller.account.validator.UpdateAccountValidator;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BroadleafUpdateAccountController extends BroadleafAbstractController {

    @Resource
    CustomerService customerService;
    @Resource(name = "blUpdateAccountValidator")
    private UpdateAccountValidator updateAccountValidator;

    private String updateAccountView = "account/myAccount";

    public String viewUpdateAccount(HttpServletRequest request, Model model, UpdateAccountForm form) {
        Customer customer = CustomerState.getCustomer();
        form.setEmailAddress(customer.getEmailAddress());
        form.setFirstName(customer.getFirstName());
        form.setLastName(customer.getLastName());
        return ajaxRender(getUpdateAccountView(), request, model);
    }

    public String processUpdateAccount(HttpServletRequest request, Model model, UpdateAccountForm form, BindingResult result) {

        updateAccountValidator.validate(form, result);

        if (result.hasErrors()) {
            return ajaxRender(getUpdateAccountView(), request, model);
        }

        Customer customer = CustomerState.getCustomer();
        customer.setEmailAddress(form.getEmailAddress());
        customer.setFirstName(form.getFirstName());
        customer.setLastName(form.getLastName());
        customerService.saveCustomer(customer);

        return ajaxRender(getUpdateAccountView(), request, model);

    }

    public void setUpdateAccountView(String updateAccountView) {
        this.updateAccountView = updateAccountView;
    }

    public String getUpdateAccountView() {
        return updateAccountView;
    }


}
