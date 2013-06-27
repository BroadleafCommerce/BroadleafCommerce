/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.web;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

/**
 * @author jfischer
 *
 */
public class BroadleafResponseWrapper implements HttpServletResponse {
    
    private HttpServletResponse response;
    private int status;
    
    public BroadleafResponseWrapper(HttpServletResponse response) {
        this.response = response;
    }
    
    public int getStatus() {
        return status;
    }

    /**
     * @param arg0
     * @see javax.servlet.http.HttpServletResponse#addCookie(javax.servlet.http.Cookie)
     */
    public void addCookie(Cookie arg0) {
        response.addCookie(arg0);
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
     * @see javax.servlet.http.HttpServletResponse#addHeader(java.lang.String, java.lang.String)
     */
    public void addHeader(String arg0, String arg1) {
        response.addHeader(arg0, arg1);
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
        response.sendRedirect(arg0);
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
