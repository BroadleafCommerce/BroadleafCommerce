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

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_GIFT_CARD_PAYMENT")
public class GiftCardPaymentInfoImpl implements GiftCardPaymentInfo {

    private static final long serialVersionUID = 1L;

    protected GiftCardPaymentInfoImpl() {
        // do not allow direct instantiation -- must at least be package private
        // for bytecode instrumentation
        // this complies with JPA specification requirements for entity
        // construction
    }

    @Transient
    protected EncryptionModule encryptionModule;

    @Id
    @GeneratedValue(generator = "GiftCardPaymentId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "GiftCardPaymentId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "GiftCardPaymentInfoImpl", allocationSize = 50)
    @Column(name = "PAYMENT_ID")
    protected Long id;

    @Column(name = "REFERENCE_NUMBER", nullable = false)
    protected String referenceNumber;

    @Column(name = "PAN", nullable = false)
    protected String pan;

    @Column(name = "PIN")
    protected String pin;

    public Long getId() {
        return id;
    }

    public String getPan() {
        return encryptionModule.decrypt(pan);
    }

    public String getPin() {
        return encryptionModule.decrypt(pin);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPan(String pan) {
        this.pan = encryptionModule.encrypt(pan);
    }

    public void setPin(String pin) {
        this.pin = encryptionModule.encrypt(pin);
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public EncryptionModule getEncryptionModule() {
        return encryptionModule;
    }

    public void setEncryptionModule(EncryptionModule encryptionModule) {
        this.encryptionModule = encryptionModule;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((pan == null) ? 0 : pan.hashCode());
        result = prime * result + ((pin == null) ? 0 : pin.hashCode());
        result = prime * result + ((referenceNumber == null) ? 0 : referenceNumber.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GiftCardPaymentInfoImpl other = (GiftCardPaymentInfoImpl) obj;

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (pan == null) {
            if (other.pan != null)
                return false;
        } else if (!pan.equals(other.pan))
            return false;
        if (pin == null) {
            if (other.pin != null)
                return false;
        } else if (!pin.equals(other.pin))
            return false;
        if (referenceNumber == null) {
            if (other.referenceNumber != null)
                return false;
        } else if (!referenceNumber.equals(other.referenceNumber))
            return false;
        return true;
    }

}
