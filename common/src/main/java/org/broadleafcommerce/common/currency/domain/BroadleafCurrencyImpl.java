/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.common.currency.domain;


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

/**
 * Author: jerryocanas Date: 9/6/12
 */

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_CURRENCY")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "blCMSElements")
@AdminPresentationClass(friendlyName = "BroadleafCurrencyImpl_baseCurrency")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_SITE)
})
public class BroadleafCurrencyImpl implements BroadleafCurrency {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CURRENCY_CODE")
    @AdminPresentation(friendlyName = "BroadleafCurrencyImpl_Currency_Code", order = 1, group = "BroadleafCurrencyImpl_Details", prominent = true)
    protected String currencyCode;

    @Column(name = "FRIENDLY_NAME")
    @AdminPresentation(friendlyName = "BroadleafCurrencyImpl_Name", order = 2, group = "BroadleafCurrencyImpl_Details", prominent = true)
    protected String friendlyName;

    @Column(name = "DEFAULT_FLAG")
    @AdminPresentation(friendlyName = "BroadleafCurrencyImpl_Is_Default", group = "BroadleafCurrencyImpl_Details", excluded = true)
    protected Boolean defaultFlag = false;

    @Override
    public String getCurrencyCode() {
        return currencyCode;
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
}
