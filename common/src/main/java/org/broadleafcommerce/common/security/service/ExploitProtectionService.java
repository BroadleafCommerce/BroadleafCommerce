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
package org.broadleafcommerce.common.security.service;

import org.broadleafcommerce.common.exception.ServiceException;

/**
 * @author jfischer
 */
public interface ExploitProtectionService {

    /**
     * Detect and remove possible XSS threats from the passed in string. This
     * includes {@code <script>} tags, and the like.
     *
     * @param string The possibly dirty string
     * @return The cleansed version of the string
     * @throws ServiceException
     */
    public String cleanString(String string) throws ServiceException;

    /**
     * Detect and remove possible XSS threats from the passed in string. This
     * includes {@code <script>} tags, and the like. If an html, validation, or
     * security problem is detected, an exception is thrown. This method also emits
     * well formed xml, which is important if using Thymeleaf to display the results.
     *
     * @param string The possibly dirty string
     * @return The cleansed version of the string
     * @throws ServiceException, CleanStringException
     */
    public String cleanStringWithResults(String string) throws ServiceException;

    public String getAntiSamyPolicyFileLocation();
    public void setAntiSamyPolicyFileLocation(String antiSamyPolicyFileLocation);

    /**
     * Detect possible XSRF attacks by comparing the csrf token included
     * in the request against the true token for this user from the session. If they are
     * different, then the exception is thrown.
     *
     * @param passedToken The csrf token that was passed in the request
     * @throws ServiceException
     */
    public void compareToken(String passedToken) throws ServiceException;

    public String getCSRFToken() throws ServiceException;

    public String getCsrfTokenParameter();

}
