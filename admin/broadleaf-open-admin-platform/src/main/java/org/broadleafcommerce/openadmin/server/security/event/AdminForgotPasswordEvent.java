package org.broadleafcommerce.openadmin.server.security.event;

import org.broadleafcommerce.common.event.BroadleafApplicationEvent;

/**
 * @author Nick Crum ncrum
 */
public class AdminForgotPasswordEvent extends BroadleafApplicationEvent {

    protected Long adminUserId;
    protected String token;
    protected String resetPasswordUrl;

    public AdminForgotPasswordEvent(Object source, Long adminUserId, String token, String resetPasswordUrl) {
        super(source);
        this.adminUserId = adminUserId;
        this.token = token;
        this.resetPasswordUrl = resetPasswordUrl;
    }

    public Long getAdminUserId() {
        return adminUserId;
    }

    public void setAdminUserId(Long adminUserId) {
        this.adminUserId = adminUserId;
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
