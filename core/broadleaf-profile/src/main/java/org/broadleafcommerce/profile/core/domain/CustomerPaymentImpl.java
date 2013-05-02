/*
 * Copyright 2008-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.profile.core.domain;

import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.common.time.domain.TemporalTimestampListener;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Index;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;

@Entity
@EntityListeners(value = { TemporalTimestampListener.class })
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_CUSTOMER_PAYMENT", uniqueConstraints = @UniqueConstraint(columnNames = {"CUSTOMER_ID", "PAYMENT_TOKEN"}))
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE)
public class CustomerPaymentImpl implements CustomerPayment {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "CustomerPaymentId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "CustomerPaymentId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "CustomerPaymentImpl", allocationSize = 50)
    @Column(name = "CUSTOMER_PAYMENT_ID")
    protected Long id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, targetEntity = CustomerImpl.class, optional=false)
    @JoinColumn(name = "CUSTOMER_ID")
    @AdminPresentation(excluded = true, visibility = VisibilityEnum.HIDDEN_ALL)
    protected Customer customer;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = AddressImpl.class, optional = false)
    @JoinColumn(name = "ADDRESS_ID")
    @Index(name="CUSTOMERPAYMENT_ADDRESS_INDEX", columnNames={"ADDRESS_ID"})
    protected Address billingAddress;

    @Column(name = "PAYMENT_TOKEN")
    @AdminPresentation(friendlyName = "CustomerPaymentImpl_Payment_Token", order=1, group = "CustomerPaymentImpl_Identification", groupOrder = 1)
    protected String paymentToken;

    @Column(name = "IS_DEFAULT")
    @AdminPresentation(friendlyName = "CustomerPaymentImpl_Default_Customer_Payment")
    protected boolean isDefault = false;

    @ElementCollection
    @CollectionTable(name = "BLC_CUSTOMER_PAYMENT_FIELDS", joinColumns=@JoinColumn(name="CUSTOMER_PAYMENT_ID"))
    @MapKeyColumn(name = "FIELD_NAME", nullable = false)
    @Column(name="FIELD_VALUE")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    protected Map<String, String> additionalFields = new HashMap<String, String>();

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public Customer getCustomer() {
        return customer;
    }

    @Override
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public Address getBillingAddress() {
        return billingAddress;
    }

    @Override
    public void setBillingAddress(Address billingAddress) {
        this.billingAddress = billingAddress;
    }

    @Override
    public String getPaymentToken() {
        return paymentToken;
    }

    @Override
    public void setPaymentToken(String paymentToken) {
        this.paymentToken = paymentToken;
    }

    @Override
    public boolean isDefault() {
        return isDefault;
    }

    @Override
    public void setDefault(boolean aDefault) {
        this.isDefault = aDefault;
    }

    @Override
    public Map<String, String> getAdditionalFields() {
        return additionalFields;
    }

    @Override
    public void setAdditionalFields(Map<String, String> additionalFields) {
        this.additionalFields = additionalFields;
    }

}
