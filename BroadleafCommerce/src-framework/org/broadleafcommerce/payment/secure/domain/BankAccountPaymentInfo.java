package org.broadleafcommerce.payment.secure.domain;

public interface BankAccountPaymentInfo {

    /**
     * @return the id
     */
    public long getId();

    /**
     * @param id the id to set
     */
    public void setId(long id);

    /**
     * @return the referenceNumber
     */
    public String getReferenceNumber();

    /**
     * @param referenceNumber the referenceNumber to set
     */
    public void setReferenceNumber(String referenceNumber);

    /**
     * @return the pan
     */
    public Long getPan();

    /**
     * @param pan the pan to set
     */
    public void setPan(Long pan);

    /**
     * @return the expirationMonth
     */
    public Integer getExpirationMonth();

    /**
     * @param expirationMonth the expirationMonth to set
     */
    public void setExpirationMonth(Integer expirationMonth);

}