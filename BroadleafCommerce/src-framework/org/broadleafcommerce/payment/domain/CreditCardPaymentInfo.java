package org.broadleafcommerce.payment.domain;

public interface CreditCardPaymentInfo extends Referenced {

    /**
     * @return the id
     */
    public long getId();

    /**
     * @param id the id to set
     */
    public void setId(long id);

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

    /**
     * @return the expirationYear
     */
    public Integer getExpirationYear();

    /**
     * @param expirationYear the expirationYear to set
     */
    public void setExpirationYear(Integer expirationYear);

}