/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.common;

import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.springframework.web.context.request.WebRequest;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by bpolster.
 */
public class RequestDTOImpl implements RequestDTO, Serializable {

    private static final long serialVersionUID = 1L;

    @AdminPresentation(friendlyName = "RequestDTOImpl_Request_URI")
    private String requestURI;


    @AdminPresentation(friendlyName = "RequestDTOImpl_Full_Url")
    private String fullUrlWithQueryString;

    @AdminPresentation(friendlyName = "RequestDTOImpl_Is_Secure")
    private Boolean secure;

    public RequestDTOImpl(HttpServletRequest request) {
        requestURI = request.getRequestURI();
        fullUrlWithQueryString = request.getRequestURL().toString();
        secure = ("HTTPS".equalsIgnoreCase(request.getScheme()) || request.isSecure());
    }

    public RequestDTOImpl(WebRequest request) {
        // Page level targeting does not work for WebRequest.
        secure = request.isSecure();
    }

    /**
     * @return  returns the request not including the protocol, domain, or query string
     */
    public String getRequestURI() {
        return requestURI;
    }

    /**
     * @return Returns the URL and parameters.
     */
    public String getFullUrLWithQueryString() {
        return fullUrlWithQueryString;
    }

    /**
     * @return true if this request came in through HTTPS
     */
    public Boolean isSecure() {
        return secure;
    }

    public String getFullUrlWithQueryString() {
        return fullUrlWithQueryString;
    }

    public void setFullUrlWithQueryString(String fullUrlWithQueryString) {
        this.fullUrlWithQueryString = fullUrlWithQueryString;
    }

    public Boolean getSecure() {
        return secure;
    }

    public void setSecure(Boolean secure) {
        this.secure = secure;
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

}
