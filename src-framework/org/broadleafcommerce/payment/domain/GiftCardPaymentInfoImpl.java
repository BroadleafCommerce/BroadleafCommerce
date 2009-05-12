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

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_GIFT_CARD_PAYMENT")
public class GiftCardPaymentInfoImpl implements GiftCardPaymentInfo {

    @Id
    @GeneratedValue(generator = "PaymentId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "PaymentId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "GiftCardPaymentInfoImpl", allocationSize = 1)
    @Column(name = "PAYMENT_ID")
    private long id;

    @Column(name = "REFERENCE_NUMBER")
    private String referenceNumber;

    @Column(name = "PAN")
    private String pan;

    @Column(name = "PIN")
    private String pin;

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getPan() {
        return pan;
    }

    @Override
    public String getPin() {
        return pin;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public void setPan(String pan) {
        this.pan = pan;
    }

    @Override
    public void setPin(String pin) {
        this.pin = pin;
    }

    @Override
    public String getReferenceNumber() {
        return referenceNumber;
    }

    @Override
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((pan == null) ? 0 : pan.hashCode());
        result = prime * result + ((pin == null) ? 0 : pin.hashCode());
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
        GiftCardPaymentInfoImpl other = (GiftCardPaymentInfoImpl) obj;
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
