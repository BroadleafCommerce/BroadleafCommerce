package org.broadleafcommerce.profile.core.event;

import org.broadleafcommerce.common.event.BroadleafApplicationEvent;

/**
 * @author Nick Crum ncrum
 */
public class ForgotPasswordEvent extends BroadleafApplicationEvent {

    protected Long customerId;
    protected String token;
    protected String resetPasswordUrl;

    public ForgotPasswordEvent(Object source, Long customerId, String token, String resetPasswordUrl) {
        super(source);
        this.customerId = customerId;
        this.token = token;
        this.resetPasswordUrl = resetPasswordUrl;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getResetPasswordUrl() {
        return resetPasswordUrl;
    }

    public void setResetPasswordUrl(String resetPasswordUrl) {
        this.resetPasswordUrl = resetPasswordUrl;
    }
}
