package org.broadleafcommerce.core.web.controller.account;

import org.broadleafcommerce.common.web.form.CsrfProtectedForm;

import java.io.Serializable;

public class UpdateAccountForm extends CsrfProtectedForm implements Serializable {

    private static final long serialVersionUID = 1L;

    private String emailAddress;
    private String firstName;
    private String lastName;

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

}
