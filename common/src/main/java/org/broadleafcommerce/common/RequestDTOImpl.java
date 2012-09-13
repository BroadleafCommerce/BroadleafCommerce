/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.common;

import org.broadleafcommerce.common.presentation.AdminPresentation;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

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

    public RequestDTOImpl() {
            // no arg constructor - used by rule builder
    }

    public RequestDTOImpl(HttpServletRequest request) {
        requestURI = request.getRequestURI();
        fullUrlWithQueryString = request.getRequestURL().toString();
        secure = ("HTTPS".equalsIgnoreCase(request.getScheme()) || request.isSecure());
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

}
