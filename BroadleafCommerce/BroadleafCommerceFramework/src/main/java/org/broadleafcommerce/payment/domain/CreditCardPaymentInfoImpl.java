/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.payment.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.broadleafcommerce.encryption.EncryptionModule;

/**
 * 
 * @author jfischer
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_CREDIT_CARD_PAYMENT")
public class CreditCardPaymentInfoImpl implements CreditCardPaymentInfo {

    private static final long serialVersionUID = 1L;

    protected CreditCardPaymentInfoImpl() {
        //do not allow direct instantiation -- must at least be package private for bytecode instrumentation
        //this complies with JPA specification requirements for entity construction
    }

    @Transient
    protected EncryptionModule encryptionModule;

    @Id
    @GeneratedValue(generator = "CreditCardPaymentId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "CreditCardPaymentId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "CreditCardPaymentInfoImpl", allocationSize = 50)
    @Column(name = "PAYMENT_ID")
    protected Long id;

    @Column(name = "REFERENCE_NUMBER", nullable=false)
    protected String referenceNumber;

    @Column(name = "PAN", nullable=false)
    protected String pan;

    @Column(name = "EXPIRATION_MONTH", nullable=false)
    protected Integer expirationMonth;

    @Column(name = "EXPIRATION_YEAR", nullable=false)
    protected Integer expirationYear;

    @Transient
    protected String cvvCode;

    /* (non-Javadoc)
     * @see org.broadleafcommerce.payment.secure.domain.CreditCardPaymentInfo#getId()
     */
    public Long getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.payment.secure.domain.CreditCardPaymentInfo#setId(long)
     */
    public void setId(Long id) {
        this.id = id;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.payment.secure.domain.CreditCardPaymentInfo#getReferenceNumber()
     */
    public String getReferenceNumber() {
        return referenceNumber;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.payment.secure.domain.CreditCardPaymentInfo#setReferenceNumber(java.lang.String)
     */
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.payment.secure.domain.CreditCardPaymentInfo#getPan()
     */
    public String getPan() {
        return encryptionModule.decrypt(pan);
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.payment.secure.domain.CreditCardPaymentInfo#setPan(java.lang.Long)
     */
    public void setPan(String pan) {
        this.pan = encryptionModule.encrypt(pan);
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.payment.secure.domain.CreditCardPaymentInfo#getExpirationMonth()
     */
    public Integer getExpirationMonth() {
        return expirationMonth;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.payment.secure.domain.CreditCardPaymentInfo#setExpirationMonth(java.lang.Integer)
     */
    public void setExpirationMonth(Integer expirationMonth) {
        this.expirationMonth = expirationMonth;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.payment.secure.domain.CreditCardPaymentInfo#getExpirationYear()
     */
    public Integer getExpirationYear() {
        return expirationYear;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.payment.secure.domain.CreditCardPaymentInfo#setExpirationYear(java.lang.Integer)
     */
    public void setExpirationYear(Integer expirationYear) {
        this.expirationYear = expirationYear;
    }

    public String getCvvCode() {
        return cvvCode;
    }

    public void setCvvCode(String cvvCode) {
        this.cvvCode = cvvCode;
    }

    public EncryptionModule getEncryptionModule() {
        return encryptionModule;
    }

    public void setEncryptionModule(EncryptionModule encryptionModule) {
        this.encryptionModule = encryptionModule;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((expirationMonth == null) ? 0 : expirationMonth.hashCode());
        result = prime * result + ((expirationYear == null) ? 0 : expirationYear.hashCode());
        result = prime * result + ((pan == null) ? 0 : pan.hashCode());
        result = prime * result + ((referenceNumber == null) ? 0 : referenceNumber.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CreditCardPaymentInfoImpl other = (CreditCardPaymentInfoImpl) obj;

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (expirationMonth == null) {
            if (other.expirationMonth != null)
                return false;
        } else if (!expirationMonth.equals(other.expirationMonth))
            return false;
        if (expirationYear == null) {
            if (other.expirationYear != null)
                return false;
        } else if (!expirationYear.equals(other.expirationYear))
            return false;
        if (pan == null) {
            if (other.pan != null)
                return false;
        } else if (!pan.equals(other.pan))
            return false;
        if (referenceNumber == null) {
            if (other.referenceNumber != null)
                return false;
        } else if (!referenceNumber.equals(other.referenceNumber))
            return false;
        return true;
    }

}
