package org.broadleafcommerce.core.web.controller.checkout;

import org.broadleafcommerce.common.web.controller.BroadleafAbstractController;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BroadleafCheckoutController extends BroadleafAbstractController {
	
	public String checkout(HttpServletRequest request, HttpServletResponse response, Model model) {
		return "checkout";
	}
	
	public String showMutliship(HttpServletRequest request, HttpServletResponse response, Model model) {
		return ajaxRender("multiship", request, model);
	}


    public String attachShippingAddress(HttpServletRequest request, HttpServletResponse response, Model model) {
        return null;
    }
}
