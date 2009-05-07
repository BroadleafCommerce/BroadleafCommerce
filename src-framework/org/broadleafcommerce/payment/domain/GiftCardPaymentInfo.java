package org.broadleafcommerce.payment.domain;

public interface GiftCardPaymentInfo extends Referenced {

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
     * @return the pin
     */
    public String getPin();

    /**
     * @param pin the pin to set
     */
    public void setPin(String pin);
}
