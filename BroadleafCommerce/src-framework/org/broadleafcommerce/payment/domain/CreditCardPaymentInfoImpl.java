package org.broadleafcommerce.payment.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

/**
 * TODO look at some pluggable encryption mechanism that would
 * decrypt protected fields. Something that's flexible that implementors
 * could use, or switch out with their own.
 * 
 * @author jfischer
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_CREDIT_CARD_PAYMENT")
public class CreditCardPaymentInfoImpl implements CreditCardPaymentInfo {

    @Id
    @GeneratedValue
    @Column(name = "PAYMENT_ID")
    private long id;

    @Column(name = "REFERENCE_NUMBER")
    private String referenceNumber;

    @Column(name = "PAN")
    private Long pan;

    @Column(name = "EXPIRATION_MONTH")
    private Integer expirationMonth;

    @Column(name = "EXPIRATION_YEAR")
    private Integer expirationYear;

    /* (non-Javadoc)
     * @see org.broadleafcommerce.payment.secure.domain.CreditCardPaymentInfo#getId()
     */
    public long getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.payment.secure.domain.CreditCardPaymentInfo#setId(long)
     */
    public void setId(long id) {
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
    public Long getPan() {
        return pan;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.payment.secure.domain.CreditCardPaymentInfo#setPan(java.lang.Long)
     */
    public void setPan(Long pan) {
        this.pan = pan;
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

}
