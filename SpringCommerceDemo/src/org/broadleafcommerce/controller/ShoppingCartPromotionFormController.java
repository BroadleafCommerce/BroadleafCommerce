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
import org.broadleafcommerce.rules.domain.ShoppingCartPromotion;
import org.broadleafcommerce.rules.service.RuleService;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class ShoppingCartPromotionFormController extends SimpleFormController {

	protected final Log logger = LogFactory.getLog(getClass());
	private RuleService ruleService;

	public void setRuleService(RuleService ruleService) {
		this.ruleService = ruleService;
	}

	protected Object formBackingObject(HttpServletRequest request)
			throws ServletException {
		ShoppingCartPromotion shoppingCartPromotion = new ShoppingCartPromotion();

		if (request.getParameter("promotionRuleId") != null) {
			shoppingCartPromotion = ruleService.readShoppingCartPromotionById(Long
					.valueOf(request.getParameter("promotionRuleId")));
		}

		return shoppingCartPromotion;
	}
	
	@SuppressWarnings("unchecked")
	protected Map referenceData(HttpServletRequest request) throws Exception {
        Map refData = new HashMap();
        List<PromotionRuleCategory> promotionRuleCategoryList = ruleService.readAllRuleCategories();
        refData.put("promotionRuleCategoryList", promotionRuleCategoryList );
        return refData;
    }


	@Override
	protected ModelAndView onSubmit(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors)
			throws Exception {

		ShoppingCartPromotion shoppingCartPromotion = (ShoppingCartPromotion) command;
		
		/*
		String condition1 = ParseToRuleFile.patternParser(request.getParameter("pattern"));
		String property1 = ParseToRuleFile.propertyParser(request.getParameter("property"));
		System.out.println("PROPERTY1 = " + property1);
		String booleanCondition1 = ParseToRuleFile.booleanExpressionParser(request.getParameter("booleanExpression"));
		
		try {
			
			File aFile = new File("war/WEB-INF/drools/" + promotionRule.getName() + ".drl");
			
			Writer output = new BufferedWriter(new FileWriter(aFile));

			if (aFile == null) {
				throw new IllegalArgumentException("File should not be null.");
			}
			if (!aFile.exists()) {
				throw new FileNotFoundException("File does not exist: " + aFile);
			}
			if (!aFile.isFile()) {
				throw new IllegalArgumentException(
						"Should not be a directory: " + aFile);
			}
			if (!aFile.canWrite()) {
				throw new IllegalArgumentException("File cannot be written: "
						+ aFile);
			}
			String newLine = "\n";
			String tab = "\t";
			
			output.write("package org.springcommerce.rules;" + newLine);
			output.write("import org.springcommerce.order.domain.Order;" + newLine);
			output.write("rule \"" + promotionRule.getName() + "\"");
			output.write(newLine);
			
			output.write("when");
			output.write(newLine + tab);
	
			output.write(condition1 + property1 + booleanCondition1 + request.getParameter("value") + ")");
			
			output.write(newLine);
			output.write("then");
			output.write(newLine + tab);
			output.write("System.out.println(\"SUCCESS\");");
			output.write(newLine);
			output.write("end");
			output.close();
			
			Properties props = new Properties();
			props.put("file", "src/org/springcommerce/rules/one.drl"); 
			props.put("newInstance", "true");
			props.put("name", "ruleAgentDeployer");
			
			RuleAgent agent = RuleAgent.newRuleAgent(props);
			RuleBase ruleBase = agent.getRuleBase();

			WorkingMemory workingMemory = ruleBase.newStatefulSession();
			Order order = new Order();
			order.setOrderTotal((Double)request.getAttribute("orderTotal"));
			workingMemory.insert(order);
			workingMemory.fireAllRules();
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error writing drools file");
		} finally {

		}
		*/
		
		//ruleService.savePromotionRule(promotionRule);
		
		System.out.println(shoppingCartPromotion.getName());
		if (errors.hasErrors()) {
			logger.debug("Error returning back to the form");
			
			return showForm(request, response, errors);
		}
		ModelAndView mav = new ModelAndView(getSuccessView(), errors.getModel());
		mav.addObject("saved", true);

		return mav;
	}
}
