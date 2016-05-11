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
    protected Object[] messageParams = null;

    /**
     * Create a new exception instance
     *
     * @param code an integer code that represents this exception state
     * @param message the message that will be posted to stack traces on the console (not necessarily intended for the user)
     */
    public TranslatableException(int code, String message) {
        this(code, message, null);
    }

    /**
     * Creates a new exception instance
     * 
     * @param code an integer code that represents this exception state
     * @param message the message that will be posted to stack traces on the console (not necessarily intended for the user)
     * @param messageParams An array of objects that may be used to dymanically populate a message
     */
    public TranslatableException(int code, String message, Object[] messageParams) {
        super(message);
        this.code = code;
        this.messageParams = messageParams;
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
     * Retrieves the message key that the i18n message will be keyed by.
     * @return
     */
    public String getMessageKey() {
        return getClass().getSimpleName() + "_" + code;
    }

    /**
     * Retrieves the message parameters, if any, that will be used to populate any dynamic message parameters.
     * @return
     */
    public Object[] getMessageParameters() {
        return this.messageParams;
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
                response = context.getMessageSource().getMessage(exCode, this.messageParams, getMessage(), context.getJavaLocale());
                if (response.equals(exCode)) {
                    response = getMessage();
                }
            }
        } catch (NoSuchMessageException e) {
            response = getMessage();
        }
        return response;
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
