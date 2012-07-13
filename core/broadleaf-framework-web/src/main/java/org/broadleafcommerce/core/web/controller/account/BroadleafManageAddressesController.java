package org.broadleafcommerce.core.web.controller.account;

import org.broadleafcommerce.common.web.controller.BroadleafAbstractController;
import org.broadleafcommerce.profile.core.service.AddressService;
import org.springframework.ui.Model;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BroadleafManageAddressesController extends BroadleafAbstractController {

    @Resource
    private AddressService addressService;

    private String manageAddressesView = "account/manageAddresses";

    public String viewManageAddresses(HttpServletRequest request, HttpServletResponse response, Model model) {
        return ajaxRender(getManageAddressesView(), request, model);
    }

    public String processManageAddresses(HttpServletRequest request, HttpServletResponse response, Model model) {
        return ajaxRender(getManageAddressesView(), request, model);
    }

    public String getManageAddressesView() {
        return manageAddressesView;
    }

    public void setManageAddressesView(String manageAddressesView) {
        this.manageAddressesView = manageAddressesView;
    }
}
