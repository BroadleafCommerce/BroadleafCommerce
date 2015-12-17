/*
 * #%L
 * BroadleafCommerce Subscription
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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

package org.broadleafcommerce.core.web.api.wrapper;

import org.broadleafcommerce.common.util.xml.ISO8601DateAdapter;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerPayment;
import org.broadleafcommerce.profile.core.service.AddressService;
import org.broadleafcommerce.profile.core.service.CustomerPaymentService;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.springframework.context.ApplicationContext;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * This is a JAXB wrapper to wrap CustomerPayment.
 * <p/>
 */
@XmlRootElement(name = "customerPayment")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class CustomerPaymentWrapper extends BaseWrapper implements APIWrapper<CustomerPayment>, WrapperAdditionalFields, APIUnwrapper<CustomerPayment> {

    @XmlElement
    protected Long id;

    @XmlElement
    protected String paymentToken;

    @XmlElement
    protected Boolean isDefault;

    @XmlElement
    protected AddressWrapper billingAddress;

    @XmlElement
    protected CustomerWrapper customer;

    @XmlElement
    @XmlJavaTypeAdapter(ISO8601DateAdapter.class)
    private Date expirationDate;

    @XmlElement
    private String cardName;

    @XmlElement
    private String lastFour;

    @XmlElement
    private String cardType;

    @XmlElement
    private String paymentGatewayType;

    @XmlElement(name = "additionalField")
    @XmlElementWrapper(name = "additionalFields")
    protected List<MapElementWrapper> additionalFields;

    @Override
    public void wrapDetails(CustomerPayment model, HttpServletRequest request) {

        this.id = model.getId();
        this.isDefault = model.isDefault();

        if (model.getPaymentToken() != null) {
            this.paymentToken = model.getPaymentToken();
        }

        if (model.getBillingAddress() != null) {
            AddressWrapper addressWrapper = (AddressWrapper) context.getBean(AddressWrapper.class.getName());
            addressWrapper.wrapDetails(model.getBillingAddress(), request);
            this.billingAddress = addressWrapper;
        }

        CustomerWrapper customerWrapper = (CustomerWrapper) context.getBean(CustomerWrapper.class.getName());
        customerWrapper.wrapDetails(model.getCustomer(), request);
        this.customer = customerWrapper;

        this.additionalFields = super.createElementWrappers(model);

    }

    /**
     * The CustomerPaymentWrapper is assumed to be generated only in the context of a Subscription post.
     * In this context, it is also assumed to contain id references to existing Customer and (billing) Address. 
     * No other fields from either customer or billingAddress are assumed, or necessary. 
     */
    @Override
    public CustomerPayment unwrap(HttpServletRequest request, ApplicationContext context) {
        CustomerPaymentService custPayService = (CustomerPaymentService) context.getBean("blCustomerPaymentService");
        CustomerPayment custPay = custPayService.create();

        AddressService addressService = (AddressService) context.getBean("blAddressService");
        Address billingAddress = addressService.readAddressById(this.billingAddress.getId());
        custPay.setBillingAddress(billingAddress);

        CustomerService customerService = (CustomerService) context.getBean("blCustomerService");
        Customer cust = customerService.readCustomerById(this.customer.getId());
        custPay.setCustomer(cust);

        custPay.setIsDefault(this.isDefault);
        custPay.setPaymentToken(this.getPaymentToken());
        super.transferAdditionalFieldsFromWrapper(this, custPay);
        return custPay;

    }

    @Override
    public void wrapSummary(CustomerPayment model, HttpServletRequest request) {
        wrapDetails(model, request);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPaymentToken() {
        return paymentToken;
    }

    public void setPaymentToken(String paymentToken) {
        this.paymentToken = paymentToken;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public AddressWrapper getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(AddressWrapper billingAddress) {
        this.billingAddress = billingAddress;
    }

    public CustomerWrapper getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerWrapper customer) {
        this.customer = customer;
    }

    @Override
    public List<MapElementWrapper> getAdditionalFields() {
        return additionalFields;
    }

    @Override
    public void setAdditionalFields(List<MapElementWrapper> additionalFields) {
        this.additionalFields = additionalFields;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getLastFour() {
        return lastFour;
    }

    public void setLastFour(String lastFour) {
        this.lastFour = lastFour;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getPaymentGatewayType() {
        return paymentGatewayType;
    }

    public void setPaymentGatewayType(String paymentGatewayType) {
        this.paymentGatewayType = paymentGatewayType;
    }
}
