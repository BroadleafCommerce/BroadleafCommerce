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
package org.broadleafcommerce.common.locale.domain;

import org.broadleafcommerce.common.admin.domain.AdminMainEntity;
import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.currency.domain.BroadleafCurrencyImpl;
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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Created by jfischer
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_LOCALE")
@Cache(usage= CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blCMSElements")
@AdminPresentationClass(friendlyName = "LocaleImpl_baseLocale")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_SITE),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.AUDITABLE_ONLY)
})
public class LocaleImpl implements Locale, AdminMainEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @Column (name = "LOCALE_CODE")
    @AdminPresentation(friendlyName = "LocaleImpl_Locale_Code", order = 1, 
        group = "LocaleImpl_Details", 
        prominent = true, gridOrder = 2)
    protected String localeCode;

    @Column (name = "FRIENDLY_NAME")
    @AdminPresentation(friendlyName = "LocaleImpl_Name", order = 2, 
        group = "LocaleImpl_Details", 
        prominent = true, gridOrder = 1)
    protected String friendlyName;

    @Column (name = "DEFAULT_FLAG")
    @AdminPresentation(friendlyName = "LocaleImpl_Is_Default", order = 3, 
        group = "LocaleImpl_Details", 
        prominent = false, gridOrder = 3)
    protected Boolean defaultFlag = false;

    @ManyToOne(targetEntity = BroadleafCurrencyImpl.class)
    @JoinColumn(name = "CURRENCY_CODE")
    @AdminPresentation(friendlyName = "LocaleImpl_Currency", order = 4, 
        group = "LocaleImpl_Details", 
        prominent = false)
    protected BroadleafCurrency defaultCurrency;

    @Column (name = "USE_IN_SEARCH_INDEX")
    @AdminPresentation(friendlyName = "LocaleImpl_Use_In_Search_Index", order = 5, 
        group = "LocaleImpl_Details", 
        prominent = false, gridOrder = 3)
    protected Boolean useInSearchIndex = false;
    
    @Transient
    protected java.util.Locale javaLocale;
    
    @Override
    public String getLocaleCode() {
        return localeCode;
    }

    @Override
    public void setLocaleCode(String localeCode) {
        this.localeCode = localeCode;
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
    public void setDefaultFlag(Boolean defaultFlag) {
        this.defaultFlag = defaultFlag;
    }

    @Override
    public Boolean getDefaultFlag() {
        if (defaultFlag == null) {
            return Boolean.FALSE;
        } else {
            return defaultFlag;
        }
    }

    @Override
    public BroadleafCurrency getDefaultCurrency() {
        return defaultCurrency;
    }

    @Override
    public void setDefaultCurrency(BroadleafCurrency defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
    }
    
    @Override
    public Boolean getUseCountryInSearchIndex() {
        return useInSearchIndex == null ? false : useInSearchIndex;
    }

    @Override
    public void setUseCountryInSearchIndex(Boolean useInSearchIndex) {
        this.useInSearchIndex = useInSearchIndex;
    }
    
    public java.util.Locale getJavaLocale() {
        if (javaLocale == null && getLocaleCode() != null) {
            String localeString = getLocaleCode();
            return org.springframework.util.StringUtils.parseLocaleString(localeString);
        }
        return javaLocale;
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

        LocaleImpl locale = (LocaleImpl) o;

        if (localeCode != null ? !localeCode.equals(locale.localeCode) : locale.localeCode != null) {
            return false;
        }
        if (friendlyName != null ? !friendlyName.equals(locale.friendlyName) : locale.friendlyName != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = localeCode != null ? localeCode.hashCode() : 0;
        result = 31 * result + (friendlyName != null ? friendlyName.hashCode() : 0);
        return result;
    }

    @Override
    public String getMainEntityName() {
        return getLocaleCode();
    }

}
