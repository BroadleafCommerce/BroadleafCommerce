/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

import com.gwtincubator.security.exception.ApplicationSecurityException;

/**
 * Based on SecurityExceptionFactory by David Martin http://code.google.com/p/gwt-incubator-lib/
 * 
 * @author jfischer
 */
public class SecurityExceptionFactory {

    public static ApplicationSecurityException get(final Throwable springException) {
        ApplicationSecurityException gwtException = null;
        if (springException instanceof AccessDeniedException) {
            gwtException = new com.gwtincubator.security.exception.AccessDeniedException(springException.getMessage(), springException);
        } else if (springException instanceof AuthenticationException) {
            gwtException = new com.gwtincubator.security.exception.AuthenticationException(springException.getMessage(), springException);
        } else {
            gwtException = new com.gwtincubator.security.exception.ApplicationSecurityException(springException.getMessage(), springException);
        }
        return gwtException;
    }

}
