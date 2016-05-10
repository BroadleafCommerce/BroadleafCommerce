/*
 * #%L
 * BroadleafCommerce CMS Module
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.cms.web.file;

import org.broadleafcommerce.cms.file.service.StaticAssetService;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Will convert from http/https based on the request.
 * 
 * If the URL is based on a  BLC-CMS-Asset and the environment
 * specifies a replacement prefix, then the prefix will be set.
 * 
 * Populates the page-attribute "assetpath" with the updated
 * url.
 * 
 */
public class UrlRewriteTag extends TagSupport {
    private static final long serialVersionUID = 1L;

    // The following attribute is set in BroadleafProcessURLFilter
    public static final String REQUEST_DTO = "blRequestDTO";

    private String value;
    private String var;

    private static StaticAssetService staticAssetService;

    protected void initServices() {
        if (staticAssetService == null) {
            WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(pageContext.getServletContext());          
            staticAssetService = (StaticAssetService) applicationContext.getBean("blStaticAssetService");
        }
    }
        

    /**
     * Returns true if the current request.scheme = HTTPS or if the request.isSecure value is true.
     * @return
     */
    protected boolean isRequestSecure() {
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        return ("HTTPS".equalsIgnoreCase(request.getScheme()) || request.isSecure());
    } 

    public int doStartTag() throws JspException {
        initServices();
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        
        String returnValue = staticAssetService.convertAssetPath(value, request.getContextPath(), isRequestSecure());

        if (var != null) {
            pageContext.setAttribute(var, returnValue);
        } else {
            try {
                pageContext.getOut().print(returnValue);
            } catch (IOException ioe) {
                throw new JspTagException(ioe.toString(), ioe);
            }
        }

        return EVAL_PAGE;
    }

    @Override
    public int doEndTag() throws JspException {       
        var=null;
        return super.doEndTag();                 
    }

    @Override
    public void release() {
        var = null;
        super.release();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getVar() {
        return var;
    }

    public void setVar(String var) {
        this.var = var;
    }
}
