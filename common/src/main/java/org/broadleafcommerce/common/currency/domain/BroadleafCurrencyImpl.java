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
package org.broadleafcommerce.common.currency.domain;


import org.broadleafcommerce.common.admin.domain.AdminMainEntity;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Author: jerryocanas Date: 9/6/12
 */

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_CURRENCY")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "blCMSElements")
@AdminPresentationClass(friendlyName = "BroadleafCurrencyImpl_baseCurrency")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_SITE),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.AUDITABLE_ONLY)
})
public class BroadleafCurrencyImpl implements BroadleafCurrency, AdminMainEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CURRENCY_CODE")
    @AdminPresentation(friendlyName = "BroadleafCurrencyImpl_Currency_Code",
            order = 1, group = "BroadleafCurrencyImpl_Details", prominent = true, gridOrder = 2000)
    protected String currencyCode;

    @Column(name = "FRIENDLY_NAME")
    @AdminPresentation(friendlyName = "BroadleafCurrencyImpl_Name", order = 2, group = "BroadleafCurrencyImpl_Details",
            prominent = true, gridOrder = 1000)
    protected String friendlyName;

    @Column(name = "DEFAULT_FLAG")
    @AdminPresentation(friendlyName = "BroadleafCurrencyImpl_Is_Default", group = "BroadleafCurrencyImpl_Details", excluded = true)
    protected Boolean defaultFlag = false;
    
    @Transient
    protected java.util.Currency javaCurrency;

    @Override
    public String getCurrencyCode() {
        return currencyCode;
    }
    
    public java.util.Currency getJavaCurrency() {
        if (javaCurrency == null && getCurrencyCode() != null) {
            javaCurrency = java.util.Currency.getInstance(getCurrencyCode());
        }
        return javaCurrency;
    }

    @Override
    public void setCurrencyCode(String code) {
        this.currencyCode = code;
    }

    @Override
    public String getFriendlyName() {
        return friendlyName;
    }

    @Override
    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    @Override
    public boolean getDefaultFlag() {
        if (defaultFlag == null) {
            return false;
        }
        return defaultFlag.booleanValue();
    }

    @Override
    public void setDefaultFlag(boolean defaultFlag) {
        this.defaultFlag = new Boolean(defaultFlag);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) return false;
        if (!getClass().isAssignableFrom(o.getClass())) {
            return false;
        }

        BroadleafCurrencyImpl currency = (BroadleafCurrencyImpl) o;

        if (currencyCode != null ? !currencyCode.equals(currency.currencyCode) : currency.currencyCode != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = currencyCode != null ? currencyCode.hashCode() : 0;
        return result;
    }

    @Override
    public String getMainEntityName() {
        if (getFriendlyName() != null) {
            return getFriendlyName() + " (" + getCurrencyCode() + ")";
        } else {
            return getCurrencyCode();
        }
    }
}
