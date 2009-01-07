package org.springcommerce.demo.web;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class DemoServlet extends HttpServlet {

	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());
		/*
    	 * Test to make sure the first patch was applied. This patch involves merging in 
    	 * new properties (and overriding a property and attribute) for a bean
    	 * with a given id.
    	 */
    	ExtensibilityTestBean bean1 = (ExtensibilityTestBean) context.getBean("test");
    	if (!bean1.getTestProperty().equals("test") || !bean1.getTestProperty2().equals("test2")) {
    		throw new RuntimeException("test failed...");
    	}
    	/*
    	 * Test to make sure the second patch was applied. This patch involves adding
    	 * an entirely new bean to the source.
    	 */
    	ExtensibilityTestBean3 bean2 = (ExtensibilityTestBean3) context.getBean("test2");
    	if (!bean2.getTestProperty().equals("new") || !bean2.getTestProperty2().equals("none2") || !bean2.getTestProperty3().equals("none3")) {
    		throw new RuntimeException("test failed...");
    	}
    	
    	System.out.println("Test Succeeded!");
	}

}
