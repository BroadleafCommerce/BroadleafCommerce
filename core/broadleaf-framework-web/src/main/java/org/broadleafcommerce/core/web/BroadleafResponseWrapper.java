/*
 * #%L
 * BroadleafCommerce Framework Web
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
package org.broadleafcommerce.core.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.util.StringUtil;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.AccessControlException;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * @author jfischer
 *
 */
public class BroadleafResponseWrapper implements HttpServletResponse {

    protected final Log LOG = LogFactory.getLog(getClass());
    
    private HttpServletResponse response;
    private int status;
    
    public BroadleafResponseWrapper(HttpServletResponse response) {
        this.response = response;
    }
    
    public int getStatus() {
        return status;
    }

    @Override
    public String getHeader(String s) {
        return response.getHeader(s);
    }

    @Override
    public Collection<String> getHeaders(String s) {
        return response.getHeaders(s);
    }

    @Override
    public Collection<String> getHeaderNames() {
        return response.getHeaderNames();
    }

    /**
     * @param arg0
     * @see org.owasp.esapi.HTTPUtilities#addCookie(HttpServletResponse, Cookie)
     */
    public void addCookie(Cookie arg0) {
        ESAPI.httpUtilities().addCookie(response, arg0);
    }

    /**
     * @param arg0
     * @param arg1
     * @see javax.servlet.http.HttpServletResponse#addDateHeader(java.lang.String, long)
     */
    public void addDateHeader(String arg0, long arg1) {
        response.addDateHeader(arg0, arg1);
    }

    /**
     * @param arg0
     * @param arg1
     * @see org.owasp.esapi.HTTPUtilities#addHeader(HttpServletResponse, String, String)
     */
    public void addHeader(String arg0, String arg1) {
        ESAPI.httpUtilities().addHeader(response, arg0, arg1);
    }

    /**
     * @param arg0
     * @param arg1
     * @see javax.servlet.http.HttpServletResponse#addIntHeader(java.lang.String, int)
     */
    public void addIntHeader(String arg0, int arg1) {
        response.addIntHeader(arg0, arg1);
    }

    /**
     * @param arg0
     * @return
     * @see javax.servlet.http.HttpServletResponse#containsHeader(java.lang.String)
     */
    public boolean containsHeader(String arg0) {
        return response.containsHeader(arg0);
    }

    /**
     * @param arg0
     * @return
     * @deprecated
     * @see javax.servlet.http.HttpServletResponse#encodeRedirectUrl(java.lang.String)
     */
    public String encodeRedirectUrl(String arg0) {
        return response.encodeRedirectUrl(arg0);
    }

    /**
     * @param arg0
     * @return
     * @see javax.servlet.http.HttpServletResponse#encodeRedirectURL(java.lang.String)
     */
    public String encodeRedirectURL(String arg0) {
        return response.encodeRedirectURL(arg0);
    }

    /**
     * @param arg0
     * @return
     * @deprecated
     * @see javax.servlet.http.HttpServletResponse#encodeUrl(java.lang.String)
     */
    public String encodeUrl(String arg0) {
        return response.encodeUrl(arg0);
    }

    /**
     * @param arg0
     * @return
     * @see javax.servlet.http.HttpServletResponse#encodeURL(java.lang.String)
     */
    public String encodeURL(String arg0) {
        return response.encodeURL(arg0);
    }

    /**
     * @throws IOException
     * @see javax.servlet.ServletResponse#flushBuffer()
     */
    public void flushBuffer() throws IOException {
        response.flushBuffer();
    }

    /**
     * @return
     * @see javax.servlet.ServletResponse#getBufferSize()
     */
    public int getBufferSize() {
        return response.getBufferSize();
    }

    /**
     * @return
     * @see javax.servlet.ServletResponse#getCharacterEncoding()
     */
    public String getCharacterEncoding() {
        return response.getCharacterEncoding();
    }

    /**
     * @return
     * @see javax.servlet.ServletResponse#getContentType()
     */
    public String getContentType() {
        return response.getContentType();
    }

    /**
     * @return
     * @see javax.servlet.ServletResponse#getLocale()
     */
    public Locale getLocale() {
        return response.getLocale();
    }

    /**
     * @return
     * @throws IOException
     * @see javax.servlet.ServletResponse#getOutputStream()
     */
    public ServletOutputStream getOutputStream() throws IOException {
        return response.getOutputStream();
    }

    /**
     * @return
     * @throws IOException
     * @see javax.servlet.ServletResponse#getWriter()
     */
    public PrintWriter getWriter() throws IOException {
        return response.getWriter();
    }

    /**
     * @return
     * @see javax.servlet.ServletResponse#isCommitted()
     */
    public boolean isCommitted() {
        return response.isCommitted();
    }

    /**
     * 
     * @see javax.servlet.ServletResponse#reset()
     */
    public void reset() {
        response.reset();
    }

    /**
     * 
     * @see javax.servlet.ServletResponse#resetBuffer()
     */
    public void resetBuffer() {
        response.resetBuffer();
    }

    /**
     * @param arg0
     * @param arg1
     * @throws IOException
     * @see javax.servlet.http.HttpServletResponse#sendError(int, java.lang.String)
     */
    public void sendError(int arg0, String arg1) throws IOException {
        response.sendError(arg0, arg1);
    }

    /**
     * @param arg0
     * @throws IOException
     * @see javax.servlet.http.HttpServletResponse#sendError(int)
     */
    public void sendError(int arg0) throws IOException {
        response.sendError(arg0);
    }

    /**
     * @param arg0
     * @throws IOException
     * @see javax.servlet.http.HttpServletResponse#sendRedirect(java.lang.String)
     */
    public void sendRedirect(String arg0) throws IOException {
        try {
            ESAPI.httpUtilities().sendRedirect(arg0);
        } catch (AccessControlException e) {
            LOG.error("SECURITY FAILURE Bad redirect url: " + StringUtil.sanitize(arg0), e);
            throw new IOException("Access Control Exception", e);
        }
    }

    /**
     * @param arg0
     * @see javax.servlet.ServletResponse#setBufferSize(int)
     */
    public void setBufferSize(int arg0) {
        response.setBufferSize(arg0);
    }

    /**
     * @param arg0
     * @see javax.servlet.ServletResponse#setCharacterEncoding(java.lang.String)
     */
    public void setCharacterEncoding(String arg0) {
        response.setCharacterEncoding(arg0);
    }

    /**
     * @param arg0
     * @see javax.servlet.ServletResponse#setContentLength(int)
     */
    public void setContentLength(int arg0) {
        response.setContentLength(arg0);
    }

    @Override
    public void setContentLengthLong(long l) {
        response.setContentLengthLong(l);
    }

    /**
     * @param arg0
     * @see javax.servlet.ServletResponse#setContentType(java.lang.String)
     */
    public void setContentType(String arg0) {
        response.setContentType(arg0);
    }

    /**
     * @param arg0
     * @param arg1
     * @see javax.servlet.http.HttpServletResponse#setDateHeader(java.lang.String, long)
     */
    public void setDateHeader(String arg0, long arg1) {
        response.setDateHeader(arg0, arg1);
    }

    /**
     * @param arg0
     * @param arg1
     * @see javax.servlet.http.HttpServletResponse#setHeader(java.lang.String, java.lang.String)
     */
    public void setHeader(String arg0, String arg1) {
        response.setHeader(arg0, arg1);
    }

    /**
     * @param arg0
     * @param arg1
     * @see javax.servlet.http.HttpServletResponse#setIntHeader(java.lang.String, int)
     */
    public void setIntHeader(String arg0, int arg1) {
        response.setIntHeader(arg0, arg1);
    }

    /**
     * @param arg0
     * @see javax.servlet.ServletResponse#setLocale(java.util.Locale)
     */
    public void setLocale(Locale arg0) {
        response.setLocale(arg0);
    }

    /**
     * @param arg0
     * @param arg1
     * @deprecated
     * @see javax.servlet.http.HttpServletResponse#setStatus(int, java.lang.String)
     */
    public void setStatus(int arg0, String arg1) {
        this.status = arg0;
        response.setStatus(arg0, arg1);
    }

    /**
     * @param arg0
     * @see javax.servlet.http.HttpServletResponse#setStatus(int)
     */
    public void setStatus(int arg0) {
        this.status = arg0;
        response.setStatus(arg0);
    }

}
