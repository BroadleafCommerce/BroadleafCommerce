/*-
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.core.web.security;

import org.apache.commons.lang3.StringUtils;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.ValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class XssRequestWrapper extends HttpServletRequestWrapper {

    protected final Environment environment;
    private String[] whiteListParamNames;
    private final int MAX_INPUT_LENGTH = ESAPI.securityConfiguration().getIntProp("HttpUtilities.BroadleafMaxInputLength");
    private final String BLC_PARAM_VALUE_INPUT_TYPE = "BroadleafHttpParameterValue";

    @Value("${custom.strip.xss:false}")
    protected boolean customStripXssEnabled;

    public XssRequestWrapper(HttpServletRequest servletRequest, Environment environment, String[] whiteListParamNames) {
        super(servletRequest);
        this.environment = environment;
        this.whiteListParamNames = whiteListParamNames;
    }

    @Override
    public String[] getParameterValues(String parameter) {
        if (parameter == null) {
            return null;
        }
        String[] values = super.getParameterValues(parameter);
        if (values == null) {
            return null;
        }
        if(checkWhitelist(parameter)){
            return values;
        }

        int count = values.length;
        String[] encodedValues = new String[count];
        for (int i = 0; i < count; i++) {
            encodedValues[i] = stripXss(values[i], BLC_PARAM_VALUE_INPUT_TYPE);
        }

        return encodedValues;
    }

    protected boolean checkWhitelist(String parameter) {
        for (String whiteListParamName : whiteListParamNames) {
            if(whiteListParamName.equals(parameter)){
                return true;
            }
        }
        return false;
    }

    @Override
    public String getParameter(String parameter) {
        if (parameter == null) {
            return null;
        }
        String value = super.getParameter(parameter);
        if(checkWhitelist(parameter)){
            return value;
        }
        return stripXss(value, BLC_PARAM_VALUE_INPUT_TYPE);
    }

    protected String stripXss(String value) {
        return stripXss(value, null);
    }

    /**
     * When {@link #customStripXssEnabled} is false, it will run ESAPI's logic based on the esapiInputType.
     * If esapiInputType is null or empty, it will run {@link #stripXssAsHTML(String)}.
     *
     * @param value - value to be stripped
     * @param esapiInputType - The name of the ESAPI validation rule defined in ESAPI validation configuration file.
     */
    protected String stripXss(String value, String esapiInputType) {
        return customStripXssEnabled ? customStripXss(value) : stripXssWithESAPI(value, esapiInputType);
    }

    protected String customStripXss(String value) {
        if (value == null) {
            return null;
        }

        // Avoid null characters
        value = value.replaceAll("", "");

        // Avoid anything between script tags
        Pattern scriptPattern = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);
        value = scriptPattern.matcher(value).replaceAll("");

        // Avoid anything in a src='...' type of expression
        scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = scriptPattern.matcher(value).replaceAll("");

        scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = scriptPattern.matcher(value).replaceAll("");

        // Remove any lonesome </script> tag
        scriptPattern = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);
        value = scriptPattern.matcher(value).replaceAll("");

        // Remove any lonesome <script ...> tag
        scriptPattern = Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = scriptPattern.matcher(value).replaceAll("");

        // Avoid eval(...) expressions
        scriptPattern = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = scriptPattern.matcher(value).replaceAll("");

        // Avoid expression(...) expressions
        scriptPattern = Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = scriptPattern.matcher(value).replaceAll("");

        // Avoid javascript:... expressions
        scriptPattern = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
        value = scriptPattern.matcher(value).replaceAll("");

        // Avoid vbscript:... expressions
        scriptPattern = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);
        value = scriptPattern.matcher(value).replaceAll("");

        // Avoid onload= expressions
        scriptPattern = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = scriptPattern.matcher(value).replaceAll("");
        return value;
    }

    protected String stripXssWithESAPI(String value, String esapiInputType) {
        if (StringUtils.isEmpty(esapiInputType)) {
            return stripXssAsHTML(value);
        }

        try {
            return ESAPI.validator().getValidInput("Value: " + value, value, esapiInputType, MAX_INPUT_LENGTH, true, true);
        } catch (ValidationException e) {
            return stripXssAsHTML(value);
        }
    }

    protected String stripXssAsHTML(String value) {
        try {
            return ESAPI.validator().getValidSafeHTML("Value: " + value, value, MAX_INPUT_LENGTH, true);
        } catch (ValidationException e2) {
            return ESAPI.encoder().encodeForHTML(value);
        }
    }
}
