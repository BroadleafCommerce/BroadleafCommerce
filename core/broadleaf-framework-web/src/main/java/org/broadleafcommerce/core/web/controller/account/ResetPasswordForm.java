package org.broadleafcommerce.core.web.controller.account;

import org.broadleafcommerce.common.web.form.CsrfProtectedForm;

import java.io.Serializable;

/**
 * Created by bpolster.
 */
public class ResetPasswordForm extends CsrfProtectedForm implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String username;
    private String token;
    private String password;
    private String passwordConfirm; 

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

	public String getPasswordConfirm() {
		return passwordConfirm;
	}

	public void setPasswordConfirm(String passwordConfirm) {
		this.passwordConfirm = passwordConfirm;
	}    
}
