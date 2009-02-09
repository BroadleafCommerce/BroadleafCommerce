package org.broadleafcommerce.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.rules.domain.CouponCode;
import org.broadleafcommerce.rules.service.RuleBaseService;
import org.drools.WorkingMemory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class CreateTestOrderFormController extends SimpleFormController {

	protected final Log logger = LogFactory.getLog(getClass());

	private RuleBaseService ruleBaseService;

	public void setRuleBaseService(RuleBaseService ruleBaseService){
		this.ruleBaseService = ruleBaseService;
	}


	protected Object formBackingObject(HttpServletRequest request)
			throws ServletException {
		CouponCode couponCode = new CouponCode();
		return couponCode;
	}

	@Override
	protected ModelAndView onSubmit(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors)
			throws Exception {

		CouponCode couponCode = (CouponCode) command;
		System.out.println(couponCode.getCode());
		WorkingMemory workingMemory = ruleBaseService.getRuleBase().newStatefulSession();
		workingMemory.insert(couponCode);
		workingMemory.fireAllRules();


		if (errors.hasErrors()) {
			logger.debug("Error returning back to the form");

			return showForm(request, response, errors);
		}

		ModelAndView mav = new ModelAndView(getSuccessView(), errors.getModel());
		mav.addObject("saved", true);

		return mav;

	}

}
