package org.broadleafcommerce.profile.core.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * Created by bpolster.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_CUSTOMER_PASSWORD_TOKEN")
public class CustomerForgotPasswordSecurityTokenImpl implements CustomerForgotPasswordSecurityToken {
    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name = "PASSWORD_TOKEN", nullable = false)
    protected String token;
    
    @Column(name = "CREATE_DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date createDate;

    @Column(name = "TOKEN_USED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date tokenUsedDate;
    
    @Column(name = "CUSTOMER_ID", nullable = false)
    protected Long customerId;
    
    @Column(name = "TOKEN_USED_FLAG", nullable = false)
    protected boolean tokenUsedFlag;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getTokenUsedDate() {
        return tokenUsedDate;
    }

    public void setTokenUsedDate(Date tokenUsedDate) {
        this.tokenUsedDate = tokenUsedDate;
    }

    public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public boolean isTokenUsedFlag() {
        return tokenUsedFlag;
    }

    public void setTokenUsedFlag(boolean tokenUsedFlag) {
        this.tokenUsedFlag = tokenUsedFlag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CustomerForgotPasswordSecurityTokenImpl that = (CustomerForgotPasswordSecurityTokenImpl) o;

        if (token != null ? !token.equals(that.token) : that.token != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return token != null ? token.hashCode() : 0;
    }
}
