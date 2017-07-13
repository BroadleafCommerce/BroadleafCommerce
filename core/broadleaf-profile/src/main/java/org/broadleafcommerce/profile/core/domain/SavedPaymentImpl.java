package org.broadleafcommerce.profile.core.domain;

import javax.persistence.*;

/**
 * @author Jacob Mitash
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_SAVED_PAYMENT")
public class SavedPaymentImpl implements SavedPayment {

    @Id
    @Column(name = "SAVED_PAYMENT_ID")
    protected Long id;

    @Column(name = "PAYMENT_NAME")
    protected String paymentName;

    @Column(name = "PERSON_NAME")
    protected String personName;

    @Column(name = "DEFAULT_METHOD")
    protected boolean defaultMethod;

    @Column(name = "LAST_FOUR_DIGITS")
    protected String lastFourDigits;

    @Column(name = "EXPIRATION")
    protected String expiration;

    @Column(name = "TOKEN")
    protected String token;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = CustomerImpl.class, optional = false)
    @JoinColumn(name = "CUSTOMER_ID")
    protected Customer customer;

    @Column(name = "CARD_COMPANY")
    private String cardCompany;


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
    public String getCardCompany() {
        return cardCompany;
    }

    @Override
    public void setCardCompany(String company) {
        this.cardCompany = company;
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
