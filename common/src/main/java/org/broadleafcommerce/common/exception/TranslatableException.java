/*
 * Copyright 2008-2013 the original author or authors.
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

package org.broadleafcommerce.common.exception;

import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.springframework.context.NoSuchMessageException;

/**
 * An exception whose message can be translated into a message suitable for a user.
 *
 * @author Jeff Fischer
 */
public abstract class TranslatableException extends Exception {

    private static final long serialVersionUID = 1L;

    protected int code;

    /**
     * Create a new exception instance
     *
     * @param code an integer code that represents this exception state
     * @param message the message that will be posted to stack traces on the console (not necessarily intended for the user)
     */
    public TranslatableException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * Retrieve the error code associated with this exception
     *
     * @return the error code
     */
    public int getCode() {
        return code;
    }

    /**
     * <p>Return the message to show to the user. The framework will first look in the localized property bundles
     * for any messages that match the supplied error code and exception type. If not found, the regular message
     * submitted to the constructor will be returned.</p>
     *
     * <p>Message bundle properties have the following format:</p>
     *
     * <p>
     * [simple class name of exception]_[integer error code]=[localized message for this exception and code]
     * </p>
     *
     * @return The error message to display to the user
     */
    @Override
    public String getLocalizedMessage() {
        String response = getMessage();
        try {
            String exCode = getMessageKey();
            BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
            if (context != null && context.getMessageSource() != null) {
                response = context.getMessageSource().getMessage(exCode, null, context.getJavaLocale());
                if (response.equals(exCode)) {
                    response = getMessage();
                }
            }
        } catch (NoSuchMessageException e) {
            response = getMessage();
        }
        return response;
    }

    public String getMessageKey() {
        return getClass().getSimpleName() + "_" + code;
    }

    /**
     * Cause the message passed to the constructor to show up on stack trace logs
     *
     * @return the non-localized version of the exception message
     */
    @Override
    public String toString() {
        String s = getClass().getName();
        String message = getMessage();
        return (message != null) ? (s + ": " + message) : s;
    }
}
