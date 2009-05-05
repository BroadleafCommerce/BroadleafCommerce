package org.broadleafcommerce.controller;

import org.springframework.web.servlet.mvc.SimpleFormController;

public class ShoppingCartPromotionTestOrderFormController extends SimpleFormController {
	/*
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

		Double price = Double.valueOf(request.getParameter("orderTotal"));
		BroadleafOrder broadleafOrder = new BroadleafOrder();
		broadleafOrder.setOrderTotal(price);

		WorkingMemory workingMemory = ruleBaseService.getRuleBase().newStatefulSession();
		workingMemory.insert(broadleafOrder);
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
	*/
}
