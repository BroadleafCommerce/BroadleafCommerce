/*-
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.security.service.AntisamyService;
import org.broadleafcommerce.common.security.service.AntisamyServiceImpl;
import org.owasp.encoder.esapi.ESAPIEncoder;
import org.owasp.validator.html.CleanResults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.validation.ValidationException;

public class XssRequestWrapper extends HttpServletRequestWrapper {

    protected final Environment environment;
    private String[] whiteListParamNames;
    private final int MAX_PARAMETER_LENGTH = 99999;

    private static final Log LOG = LogFactory.getLog(XssRequestWrapper.class);

    protected Pattern parameterPatter = Pattern.compile("^[a-zA-Z0-9.\\-\\/+=@_ #']*$");

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
            try {
                encodedValues[i] = stripXss(values[i]);
            }catch (jakarta.validation.ValidationException ex){
                if(values[i]!=null){
                    encodedValues[i] = stripXss(values[i].substring(MAX_PARAMETER_LENGTH));
                }
            }
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
        try {
            return stripXss(value);
        } catch (jakarta.validation.ValidationException exception) {
            LOG.error("Parameter " + parameter + " value too long", exception);
            if (value != null) {
                return stripXss(value.substring(MAX_PARAMETER_LENGTH));
            }
        }
        return value;
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
        if (value != null && value.length() < MAX_PARAMETER_LENGTH) {
            String canonicalize = ESAPIEncoder.getInstance().canonicalize(value);
            if (!parameterPatter.matcher(canonicalize).matches()) {
                return canonicalize;
            } else {
                return stripXssAsHTML(value);
            }
        } else if (value != null && value.length() >= MAX_PARAMETER_LENGTH) {
            throw new ValidationException("Parameter value to long, max=" + MAX_PARAMETER_LENGTH);
        }
        return value;
    }

    protected String stripXssAsHTML(String value) {
        try {
            if(value!=null && value.length()<MAX_PARAMETER_LENGTH) {
                String canonicalize = ESAPIEncoder.getInstance().canonicalize(value);
                AntisamyService instance = AntisamyServiceImpl.getInstance();
                CleanResults scan = instance.getAntiSamy().scan(canonicalize, instance.getAntiSamyPolicy());
                if (scan.getErrorMessages().size() > 0) {
                    throw new ValidationException("Input value failed validation against antisamy policy");
                }
                return scan.getCleanHTML().trim();
            }else if(value!=null && value.length()>=MAX_PARAMETER_LENGTH){
                throw new ValidationException("Parameter value to long, max=" + MAX_PARAMETER_LENGTH);
            }
        } catch (Exception e2) {
            return ESAPIEncoder.getInstance().encodeForHTML(value);
        }
        return value;
    }
}
