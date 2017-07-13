package org.broadleafcommerce.profile.core.domain;

/**
 * @author Jacob Mitash
 */
//@Entity
//@Inheritance(strategy = InheritanceType.JOINED)
//@Table(name = "BLC_SAVED_PAYMENT")
public class SavedPaymentImpl implements SavedPayment {

    protected Long id;
    protected String paymentName;
    protected String personName;
    protected boolean defaultMethod;
    protected String lastFourDigits;
    protected String expiration;
    protected String token;
    protected Customer customer;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getPaymentName() {
        return paymentName;
    }

    @Override
    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName;
    }

    @Override
    public boolean isDefaultMethod() {
        return defaultMethod;
    }

    @Override
    public void setDefaultMethod(boolean defaultMethod) {
        this.defaultMethod = defaultMethod;
    }

    @Override
    public String getLastFourDigits() {
        return lastFourDigits;
    }

    @Override
    public void setLastFourDigits(String lastFourDigits) {
        this.lastFourDigits = lastFourDigits;
    }

    @Override
    public String getExpiration() {
        return expiration;
    }

    @Override
    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String getPersonName() {
        return personName;
    }

    @Override
    public void setPersonName(String personName) {
        this.personName = personName;
    }

    @Override
    public Customer getCustomer() {
        return customer;
    }

    @Override
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
