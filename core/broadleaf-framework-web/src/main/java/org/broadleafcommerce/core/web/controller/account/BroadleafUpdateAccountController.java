package org.broadleafcommerce.core.web.controller.account;

import org.broadleafcommerce.common.web.controller.BroadleafAbstractController;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: jfridye
 * Date: 7/10/12
 * Time: 1:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class BroadleafUpdateAccountController extends BroadleafAbstractController {

    private String updateAccountView = "account/updateAccount";

    public String viewUpdateAccount(HttpServletRequest request, HttpServletResponse response, Model model) {
        return ajaxRender(getUpdateAccountView(), request, model);
    }

    public void setUpdateAccountView(String updateAccountView) {
        this.updateAccountView = updateAccountView;
    }

    public String getUpdateAccountView() {
        return updateAccountView;
    }


}
