/*
 * #%L
 * BroadleafCommerce Profile
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.profile.core.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * @author Jacob Mitash
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_SAVED_PAYMENT")
public class SavedPaymentImpl implements SavedPayment {

    @Id
    @GeneratedValue(generator = "CustomerPaymentId")
    @GenericGenerator(
            name = "CustomerPaymentId",
            strategy = "org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "segment_value", value = "CustomerPaymentImpl"),
                    @org.hibernate.annotations.Parameter(name = "entity_name", value = "org.broadleafcommerce.profile.core.domain.CustomerPaymentImpl")
            })
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
