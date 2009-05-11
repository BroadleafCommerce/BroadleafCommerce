package org.broadleafcommerce.payment.domain;

public interface BankAccountPaymentInfo extends Referenced {

    /**
     * @return the id
     */
    public long getId();

    /**
     * @param id the id to set
     */
    public void setId(long id);

    /**
     * @return the account number
     */
    public String getAccountNumber();

    /**
     * @param account number the account number to set
     */
    public void setAccountNumber(String accountNumber);

    /**
     * @return the routing number
     */
    public String getRoutingNumber();

    /**
     * @param routing number the routing number to set
     */
    public void setRoutingNumber(String routingNumber);

}