/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.security;

import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.common.util.StringUtil;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BroadleafAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private String defaultFailureUrl;

    /**
     * Maps the fully-qualified classname of an exception thrown during an authentication failure to a view
     * to use as the destination. If the exception is not mapped, the defaultFailureUrl will be used.
     * <p>
     *     Example of what you can add in your app's security configuration to map 
     *     {@link org.springframework.security.authentication.CredentialsExpiredException} to "/login/forcedPasswordChange":
     *     
     *     <pre>
     *      @Bean
     *      protected AuthenticationFailureHandler blAuthenticationFailureHandler(@Qualifier("blAuthenticationFailureRedirectStrategy") RedirectStrategy redirectStrategy) {
     *          final BroadleafAuthenticationFailureHandler response = new BroadleafAuthenticationFailureHandler("/login?error=true");
     *          response.setRedirectStrategy(redirectStrategy);
     *
     *          final Map<String, String> exceptionMappings = new HashMap<>();
     *          exceptionMappings.put("org.springframework.security.authentication.CredentialsExpiredException", "/login/forcedPasswordChange");
     *          response.setExceptionMappings(exceptionMappings);
     *
     *          return response;
     *      }
     *     </pre>
     * </p>
     */
    private final Map<String, String> failureUrlMap = new HashMap<>();

    public BroadleafAuthenticationFailureHandler() {
        super();
    }

    public BroadleafAuthenticationFailureHandler(String defaultFailureUrl) {
        super(defaultFailureUrl);
        this.defaultFailureUrl = defaultFailureUrl;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        final String failureUrlParam = StringUtil.cleanseUrlString(request.getParameter("failureUrl"));
        String successUrlParam = StringUtil.cleanseUrlString(request.getParameter("successUrl"));
        String failureUrl = StringUtils.trimToNull(failureUrlParam);

        // Verify that the url passed in is a servlet path and not a link redirecting away from the webapp.
        failureUrl = validateUrlParam(failureUrl);
        successUrlParam = validateUrlParam(successUrlParam);

        // check if the implementor has a particular view mapped for this exception
        if (failureUrl == null) {
            final String exceptionClassname = exception.getClass().getName();
            failureUrl = StringUtils.trimToNull(failureUrlMap.get(exceptionClassname));
        }
        
        if (failureUrl == null) {
            failureUrl = StringUtils.trimToNull(defaultFailureUrl);
        }
        
        if (failureUrl != null) {
            if (StringUtils.isNotEmpty(successUrlParam)) {
                if (!failureUrl.contains("?")) {
                    failureUrl += "?successUrl=" + successUrlParam;
                } else {
                    failureUrl += "&successUrl=" + successUrlParam;
                }
            }
            
            saveException(request, exception);
            getRedirectStrategy().sendRedirect(request, response, failureUrl);
        } else {
            super.onAuthenticationFailure(request, response, exception);
        }
    }

    public String validateUrlParam(final String url) {
        if (url != null) {
            if (url.contains("http") || url.contains("www") || url.contains(".")) {
                return null;
            }
        }
        
        return url;
    }

    public void setExceptionMappings(final Map<String, String> failureUrlMap) {
        this.failureUrlMap.clear();
        this.failureUrlMap.putAll(failureUrlMap);
    }

}
