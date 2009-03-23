package org.broadleafcommerce.email.domain;

import java.io.Serializable;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * @author jfischer
 *
 */
@MappedSuperclass
public class AbstractEmailTargetUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String emailAddress;

    /**
     * @return the emailAddress
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * @param emailAddress the emailAddress to set
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
