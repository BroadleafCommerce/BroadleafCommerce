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
