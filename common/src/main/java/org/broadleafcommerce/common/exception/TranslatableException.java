package org.broadleafcommerce.common.exception;

import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.springframework.context.NoSuchMessageException;

/**
 * An exception whose message can be translated into a message suitable for a user.
 *
 * @author Jeff Fischer
 */
public class TranslatableException extends Exception {

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
            String exCode = getClass().getSimpleName() + "_" + code;
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
}
