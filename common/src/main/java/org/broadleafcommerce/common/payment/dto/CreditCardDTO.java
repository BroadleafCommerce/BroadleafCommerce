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

package org.broadleafcommerce.common.payment.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Elbert Bautista (elbertbautista)
 */
public class CreditCardDTO<T> {

    protected T parent;

    protected Map<String, Object> additionalFields;
    protected String creditCardHolderName;
    protected String creditCardType;
    protected String creditCardNum;
    protected String creditCardLastFour;
    protected String creditCardExpDate;
    protected String creditCardExpMonth;
    protected String creditCardExpYear;
    protected String creditCardCvv;

    public CreditCardDTO() {
        this.additionalFields = new HashMap<String, Object>();
    }

    public CreditCardDTO(T parent) {
        this.additionalFields = new HashMap<String, Object>();
        this.parent = parent;
    }

    public T done() {
        return parent;
    }

    public CreditCardDTO<T> additionalFields(String key, Object value) {
        additionalFields.put(key, value);
        return this;
    }

    public CreditCardDTO<T> creditCardHolderName(String creditCardHolderName) {
        this.creditCardHolderName = creditCardHolderName;
        return this;
    }

    public CreditCardDTO<T> creditCardType(String creditCardType) {
        this.creditCardType = creditCardType;
        return this;
    }

    public CreditCardDTO<T> creditCardNum(String creditCardNum) {
        this.creditCardNum = creditCardNum;
        return this;
    }

    public CreditCardDTO<T> creditCardLastFour(String creditCardLastFour) {
        this.creditCardLastFour = creditCardLastFour;
        return this;
    }

    public CreditCardDTO<T> creditCardExpDate(String creditCardExpDate) {
        this.creditCardExpDate = creditCardExpDate;
        return this;
    }

    public CreditCardDTO<T> creditCardExpMonth(String creditCardExpMonth) {
        this.creditCardExpMonth = creditCardExpMonth;
        return this;
    }

    public CreditCardDTO<T> creditCardExpYear(String creditCardExpYear) {
        this.creditCardExpYear = creditCardExpYear;
        return this;
    }

    public CreditCardDTO<T> creditCardCvv(String creditCardCvv) {
        this.creditCardCvv = creditCardCvv;
        return this;
    }


    public Map<String, Object> getAdditionalFields() {
        return additionalFields;
    }

    public String getCreditCardHolderName() {
        return creditCardHolderName;
    }

    public String getCreditCardType() {
        return creditCardType;
    }

    public String getCreditCardNum() {
        return creditCardNum;
    }

    public String getCreditCardLastFour() {
        return creditCardLastFour;
    }

    public String getCreditCardExpDate() {
        return creditCardExpDate;
    }

    public String getCreditCardExpMonth() {
        return creditCardExpMonth;
    }

    public String getCreditCardExpYear() {
        return creditCardExpYear;
    }

    public String getCreditCardCvv() {
        return creditCardCvv;
    }

    public boolean creditCardPopulated() {
        return ((getAdditionalFields() != null && !getAdditionalFields().isEmpty()) ||
                getCreditCardHolderName() != null ||
                getCreditCardType() != null ||
                getCreditCardNum() != null ||
                getCreditCardLastFour() != null ||
                getCreditCardExpDate() != null ||
                getCreditCardExpMonth() != null ||
                getCreditCardExpYear() != null ||
                getCreditCardCvv() != null);
    }
}
