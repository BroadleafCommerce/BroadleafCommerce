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
    private Long pan;

    @Column(name = "PIN")
    private String pin;

    @Override
    public long getId() {
        return id;
    }

    @Override
    public Long getPan() {
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
    public void setPan(Long pan) {
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

}
