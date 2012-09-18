package org.broadleafcommerce.common.currency.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Author: jerryocanas Date: 9/6/12
 */

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_CURRENCY")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "blCMSElements")
@AdminPresentationClass(friendlyName = "BroadleafCurrencyImpl_baseCurrency")
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
    @AdminPresentation(friendlyName = "BroadleafCurrencyImpl_Is_Default", order = 3, group = "BroadleafCurrencyImpl_Details", prominent = true)
    protected Boolean defaultFlag;

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

    public static Money getMoney(double d, BroadleafCurrency currency) {
        if (currency != null) {
            return new Money(d, currency.getCurrencyCode());
        } else {
            return new Money(d);
        }
    }

    public static Money getMoney(BigDecimal d, BroadleafCurrency currency) {
        if(d==null) {
            return null;
        }
        if (currency != null) {
            return new Money(d, currency.getCurrencyCode());
        } else {
            return new Money(d);
        }
    }
    public static Money getMoney(BroadleafCurrency currency) {
        if (currency != null) {
            return new Money(currency.getCurrencyCode());
        } else {
            return new Money();
        }
    }


}
