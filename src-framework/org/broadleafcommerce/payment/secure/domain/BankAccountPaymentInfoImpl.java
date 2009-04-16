package org.broadleafcommerce.payment.secure.domain;

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
@Table(name = "BLC_BANK_ACCOUNT_PAYMENT")
public class BankAccountPaymentInfoImpl implements BankAccountPaymentInfo {

    @Id
    @GeneratedValue
    @Column(name = "PAYMENT_ID")
    private long id;

    @Column(name = "REFERENCE_NUMBER")
    private String referenceNumber;

    @Column(name = "ACCOUNT_NUMBER")
    private Long pan;

    @Column(name = "ROUTING_NUMBER")
    private Integer expirationMonth;

    /* (non-Javadoc)
     * @see org.broadleafcommerce.payment.secure.domain.BankAccountPaymentInfo#getId()
     */
    public long getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.payment.secure.domain.BankAccountPaymentInfo#setId(long)
     */
    public void setId(long id) {
        this.id = id;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.payment.secure.domain.BankAccountPaymentInfo#getReferenceNumber()
     */
    public String getReferenceNumber() {
        return referenceNumber;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.payment.secure.domain.BankAccountPaymentInfo#setReferenceNumber(java.lang.String)
     */
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.payment.secure.domain.BankAccountPaymentInfo#getPan()
     */
    public Long getPan() {
        return pan;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.payment.secure.domain.BankAccountPaymentInfo#setPan(java.lang.Long)
     */
    public void setPan(Long pan) {
        this.pan = pan;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.payment.secure.domain.BankAccountPaymentInfo#getExpirationMonth()
     */
    public Integer getExpirationMonth() {
        return expirationMonth;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.payment.secure.domain.BankAccountPaymentInfo#setExpirationMonth(java.lang.Integer)
     */
    public void setExpirationMonth(Integer expirationMonth) {
        this.expirationMonth = expirationMonth;
    }

}
