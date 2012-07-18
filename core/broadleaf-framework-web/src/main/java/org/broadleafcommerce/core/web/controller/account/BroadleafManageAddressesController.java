package org.broadleafcommerce.core.web.controller.account;

import org.broadleafcommerce.common.web.controller.BroadleafAbstractController;
import org.broadleafcommerce.profile.core.service.AddressService;
import org.springframework.ui.Model;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BroadleafManageAddressesController extends BroadleafAbstractController {

    @Resource(name = "blAddressService")
    private AddressService addressService;

    private static String manageAddressesView = "account/manageAddresses";

    public String viewManageAddresses(HttpServletRequest request, HttpServletResponse response, Model model) {
        return getManageAddressesView();
    }

    public String processManageAddresses(HttpServletRequest request, HttpServletResponse response, Model model) {
        return getManageAddressesView();
    }

    public String getManageAddressesView() {
        return manageAddressesView;
    }

}
