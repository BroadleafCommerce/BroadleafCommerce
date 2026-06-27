/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2026 Broadleaf Commerce
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

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.net.MalformedURLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Regression tests for {@link LocalRedirectStrategy}.
 *
 * <p>Guards against the open-redirect bypass reported in issue #3124: a protocol-relative target
 * such as {@code //evil.com} begins with a slash and so slipped past the legacy
 * {@code startsWith("/")} "is local" check, yet browsers resolve it to an external host.</p>
 */
public class LocalRedirectStrategyTest {

    private static final String SERVER_NAME = "localhost";
    private static final int SERVER_PORT = 80;

    private MockHttpServletRequest request(String paramName, String url) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServerName(SERVER_NAME);
        request.setServerPort(SERVER_PORT);
        request.setContextPath("");
        if (paramName != null) {
            request.setParameter(paramName, url);
        }
        return request;
    }

    private MalformedURLException expectBlocked(String url) {
        LocalRedirectStrategy strategy = new LocalRedirectStrategy();
        MockHttpServletRequest request = request("successUrl", url);
        MockHttpServletResponse response = new MockHttpServletResponse();
        try {
            strategy.sendRedirect(request, response, url);
            fail("Expected redirect to be blocked but it was allowed: " + url
                    + " -> " + response.getRedirectedUrl());
            return null; // unreachable
        } catch (MalformedURLException ex) {
            return ex;
        } catch (Exception ex) {
            fail("Expected MalformedURLException for " + url + " but got " + ex);
            return null; // unreachable
        }
    }

    // --- The reported bypass and its variants must be blocked ---------------------------------

    @Test
    public void protocolRelativeUrlIsBlocked() {
        expectBlocked("//evil.com");
    }

    @Test
    public void backslashProtocolRelativeVariantsAreBlocked() {
        expectBlocked("/\\evil.com");
        expectBlocked("\\/evil.com");
        expectBlocked("\\\\evil.com");
    }

    @Test
    public void absoluteExternalUrlIsBlocked() {
        // Pre-existing protection: a fully-qualified external url must still be rejected.
        expectBlocked("http://evil.com");
    }

    // --- Legitimate local redirects must still work -------------------------------------------

    @Test
    public void localRelativePathIsAllowed() throws Exception {
        LocalRedirectStrategy strategy = new LocalRedirectStrategy();
        MockHttpServletRequest request = request(null, null);
        MockHttpServletResponse response = new MockHttpServletResponse();

        strategy.sendRedirect(request, response, "/admin/login");

        assertEquals("/admin/login", response.getRedirectedUrl());
    }

    @Test
    public void sameHostAbsoluteUrlIsAllowed() throws Exception {
        LocalRedirectStrategy strategy = new LocalRedirectStrategy();
        String target = "http://" + SERVER_NAME + "/admin/home";
        MockHttpServletRequest request = request("successUrl", target);
        MockHttpServletResponse response = new MockHttpServletResponse();

        strategy.sendRedirect(request, response, target);

        assertEquals(target, response.getRedirectedUrl());
    }

    // --- Helper classification -----------------------------------------------------------------

    @Test
    public void isProtocolRelativeUrlClassifiesCorrectly() {
        LocalRedirectStrategy strategy = new LocalRedirectStrategy();
        assertTrue(strategy.isProtocolRelativeUrl("//evil.com"));
        assertTrue(strategy.isProtocolRelativeUrl("/\\evil.com"));
        assertTrue(strategy.isProtocolRelativeUrl("\\/evil.com"));
        assertTrue(strategy.isProtocolRelativeUrl("\\\\evil.com"));

        assertFalse(strategy.isProtocolRelativeUrl("/admin/login"));
        assertFalse(strategy.isProtocolRelativeUrl("/"));
        assertFalse(strategy.isProtocolRelativeUrl("http://localhost/x"));
        assertFalse(strategy.isProtocolRelativeUrl(""));
        assertFalse(strategy.isProtocolRelativeUrl(null));
    }
}
