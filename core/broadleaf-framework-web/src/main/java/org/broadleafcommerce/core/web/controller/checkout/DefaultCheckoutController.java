package org.broadleafcommerce.core.web.controller.checkout;

import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.core.web.controller.BroadleafAbstractController;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DefaultCheckoutController extends BroadleafAbstractController {
	
	public String checkout(HttpServletRequest request, HttpServletResponse response, Model model) throws PricingException {
		return "checkout";
	}

}
