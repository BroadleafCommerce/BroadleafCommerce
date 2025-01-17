/*-
 * #%L
 * BroadleafCommerce Profile
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.profile.core.domain;

import java.io.Serial;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

/**
 * Created by bpolster.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_CUSTOMER_PASSWORD_TOKEN")
public class CustomerForgotPasswordSecurityTokenImpl implements CustomerForgotPasswordSecurityToken {

    @Serial
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
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (!getClass().isAssignableFrom(o.getClass()))
            return false;

        CustomerForgotPasswordSecurityTokenImpl that = (CustomerForgotPasswordSecurityTokenImpl) o;

        if (token != null ? !token.equals(that.token) : that.token != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return token != null ? token.hashCode() : 0;
    }

}
