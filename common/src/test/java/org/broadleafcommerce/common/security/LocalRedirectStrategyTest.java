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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.MalformedURLException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Unit tests for LocalRedirectStrategy to verify open redirect vulnerability fixes.
 */
public class LocalRedirectStrategyTest {

    private LocalRedirectStrategy strategy;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        strategy = new LocalRedirectStrategy();
    }

    // ============ ALLOWED RELATIVE PATHS ============

    @Test
    public void testRedirectWithRelativePath() throws IOException {
        // Setup
        String url = "/login";
        setupMockRequest(url, "localhost", 8080, "/app");

        // Execute & Verify - Should not throw exception
        assertDoesNotThrow(() -> strategy.sendRedirect(request, response, url));
    }

    @Test
    public void testRedirectWithNestedRelativePath() throws IOException {
        // Setup
        String url = "/account/home";
        setupMockRequest(url, "localhost", 8080, "/app");

        // Execute & Verify - Should not throw exception
        assertDoesNotThrow(() -> strategy.sendRedirect(request, response, url));
    }

    @Test
    public void testRedirectWithRootPath() throws IOException {
        // Setup
        String url = "/";
        setupMockRequest(url, "localhost", 8080, "");

        // Execute & Verify - Should not throw exception
        assertDoesNotThrow(() -> strategy.sendRedirect(request, response, url));
    }

    // ============ BLOCKED: PROTOCOL-RELATIVE URLS ============

    @Test
    public void testRedirectWithProtocolRelativeUrlBlocked() throws IOException {
        // Setup - Protocol-relative URL
        String url = "//evil.com";
        setupMockRequest(url, "localhost", 8080, "/app");

        // Execute & Verify - Should throw MalformedURLException
        MalformedURLException exception = assertThrows(
                MalformedURLException.class,
                () -> strategy.sendRedirect(request, response, url)
        );
        assertTrue(exception.getMessage().contains("Protocol-relative redirects are not allowed"));
    }

    @Test
    public void testRedirectWithBackslashProtocolRelativeUrlBlocked() throws IOException {
        // Setup - Backslash protocol-relative URL (Windows-style path traversal)
        String url = "\\\\evil.com";
        setupMockRequest(url, "localhost", 8080, "/app");

        // Execute & Verify - Should throw MalformedURLException
        MalformedURLException exception = assertThrows(
                MalformedURLException.class,
                () -> strategy.sendRedirect(request, response, url)
        );
        assertTrue(exception.getMessage().contains("Protocol-relative redirects are not allowed"));
    }

    @Test
    public void testRedirectWithMultipleSlashesBlocked() throws IOException {
        // Setup - Multiple slashes at start
        String url = "////evil.com";
        setupMockRequest(url, "localhost", 8080, "/app");

        // Execute & Verify - Should throw MalformedURLException
        MalformedURLException exception = assertThrows(
                MalformedURLException.class,
                () -> strategy.sendRedirect(request, response, url)
        );
        assertTrue(exception.getMessage().contains("Protocol-relative redirects are not allowed"));
    }

    // ============ BLOCKED: ENCODED BYPASS ATTEMPTS ============

    @Test
    public void testRedirectWithEncodedProtocolRelativeUrlBlocked() throws IOException {
        // Setup - URL encoded protocol-relative URL: /%2F%2Fevil.com -> //evil.com after decoding
        String url = "/%2F%2Fevil.com";
        setupMockRequest(url, "localhost", 8080, "/app");

        // Execute & Verify - Should throw MalformedURLException after decoding
        MalformedURLException exception = assertThrows(
                MalformedURLException.class,
                () -> strategy.sendRedirect(request, response, url)
        );
        assertTrue(exception.getMessage().contains("Protocol-relative redirects are not allowed"));
    }

    @Test
    public void testRedirectWithEncodedProtocolRelativeUrlNoSlashPrefixBlocked() throws IOException {
        // Setup - URL encoded protocol-relative URL: %2F%2Fevil.com -> //evil.com after decoding
        String url = "%2F%2Fevil.com";
        setupMockRequest(url, "localhost", 8080, "/app");

        // Execute & Verify - Should throw MalformedURLException after decoding
        MalformedURLException exception = assertThrows(
                MalformedURLException.class,
                () -> strategy.sendRedirect(request, response, url)
        );
        assertTrue(exception.getMessage().contains("Protocol-relative redirects are not allowed"));
    }

    // ============ BLOCKED: ABSOLUTE URLS (ALWAYS VALIDATED) ============

    @Test
    public void testRedirectWithExternalHttpUrlAlwaysValidated() throws IOException {
        // Setup - External HTTP URL that doesn't match request
        String url = "http://evil.com";
        setupMockRequest(url, "localhost", 8080, "/app");

        // Execute & Verify - Should throw MalformedURLException because it's not a local redirect
        MalformedURLException exception = assertThrows(
                MalformedURLException.class,
                () -> strategy.sendRedirect(request, response, url)
        );
        assertTrue(exception.getMessage().contains("Invalid redirect url specified"));
    }

    @Test
    public void testRedirectWithExternalHttpsUrlAlwaysValidated() throws IOException {
        // Setup - External HTTPS URL that doesn't match request
        String url = "https://evil.com";
        setupMockRequest(url, "localhost", 8080, "/app");

        // Execute & Verify - Should throw MalformedURLException because it's not a local redirect
        MalformedURLException exception = assertThrows(
                MalformedURLException.class,
                () -> strategy.sendRedirect(request, response, url)
        );
        assertTrue(exception.getMessage().contains("Invalid redirect url specified"));
    }

    @Test
    public void testRedirectWithLocalHttpUrlValidates() throws IOException {
        // Setup - Local HTTP URL that matches request
        String url = "http://localhost:8080/app/account";
        setupMockRequest(url, "localhost", 8080, "/app");

        // Execute & Verify - Should not throw exception because it's a valid local redirect
        assertDoesNotThrow(() -> strategy.sendRedirect(request, response, url));
    }

    // ============ HELPER METHODS ============

    private void setupMockRequest(String url, String serverName, int serverPort, String contextPath) {
        org.mockito.Mockito.when(request.getParameter("successUrl")).thenReturn(null);
        org.mockito.Mockito.when(request.getParameter("failureUrl")).thenReturn(null);
        org.mockito.Mockito.when(request.getServerName()).thenReturn(serverName);
        org.mockito.Mockito.when(request.getServerPort()).thenReturn(serverPort);
        org.mockito.Mockito.when(request.getContextPath()).thenReturn(contextPath);
    }
}
