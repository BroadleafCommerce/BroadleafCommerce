package org.broadleafcommerce.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.rules.domain.PromotionRuleCategory;
import org.broadleafcommerce.rules.service.RuleService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class ListRuleCategoryFormController extends SimpleFormController {
	
	protected final Log logger = LogFactory.getLog(getClass());
	private RuleService ruleService;
	
    public void setRuleService(RuleService ruleService) {
        this.ruleService = ruleService;
    }

    @Override
	protected Object formBackingObject(HttpServletRequest request)throws ServletException {
    	return new PromotionRuleCategory();
    }

	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<PromotionRuleCategory> ruleCategoryList = ruleService.readAllRuleCategories();
		Map<Object, Object> model = new HashMap<Object, Object>();
		model.put("ruleCategoryList", ruleCategoryList);

		return new ModelAndView("listRuleCategory", model);
	}
}
