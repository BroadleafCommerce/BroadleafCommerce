package org.broadleafcommerce.cms.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.broadleafcommerce.cms.page.dto.PageDTO;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * This class works in combination with the PageHandlerMapping which finds a page based upon
 * the request URL.
 *
 * @author bpolster
 */
public class PageController implements Controller {	
    private static String MODEL_ATTRIBUTE_NAME="page";    

	@Override
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView model = new ModelAndView();
		PageDTO page = (PageDTO) request.getAttribute(PageHandlerMapping.PAGE_ATTRIBUTE_NAME);
		assert page != null;

		model.addObject(MODEL_ATTRIBUTE_NAME, page);		
		model.setViewName(page.getTemplatePath());
		return model;
	}
}
