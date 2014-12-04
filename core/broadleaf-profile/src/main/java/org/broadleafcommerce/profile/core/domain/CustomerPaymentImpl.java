/*
 * #%L
 * BroadleafCommerce Profile
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
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
 * #L%
 */
package org.broadleafcommerce.profile.core.domain;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@EntityListeners(value = { TemporalTimestampListener.class })
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_CUSTOMER_PAYMENT", uniqueConstraints = @UniqueConstraint(name = "CSTMR_PAY_UNIQUE_CNSTRNT", columnNames = { "CUSTOMER_ID", "PAYMENT_TOKEN" }))
@AdminPresentationMergeOverrides(
{
        @AdminPresentationMergeOverride(name = "billingAddress.addressLine1", mergeEntries =
                @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.PROMINENT, booleanOverrideValue = true)),
        @AdminPresentationMergeOverride(name = "billingAddress.", mergeEntries = {
                @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.TAB, overrideValue = CustomerPaymentImpl.Presentation.Tab.Name.BILLING_ADDRESS),
                @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.TABORDER, intOverrideValue = CustomerPaymentImpl.Presentation.Tab.Order.BILLING_ADDRESS)                
        })
})
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE)
public class CustomerPaymentImpl implements CustomerPayment {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "CustomerPaymentId")
    @GenericGenerator(
            name = "CustomerPaymentId",
            strategy = "org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
            parameters = {
                    @Parameter(name = "segment_value", value = "CustomerPaymentImpl"),
                    @Parameter(name = "entity_name", value = "org.broadleafcommerce.profile.core.domain.CustomerPaymentImpl")
            })
    @Column(name = "CUSTOMER_PAYMENT_ID")
    protected Long id;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = CustomerImpl.class, optional = false)
    @JoinColumn(name = "CUSTOMER_ID")
    @AdminPresentation(excluded = true)
    protected Customer customer;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = AddressImpl.class, optional = false)
    @JoinColumn(name = "ADDRESS_ID")
    protected Address billingAddress;

    @Column(name = "PAYMENT_TOKEN")
    @AdminPresentation(friendlyName = "CustomerPaymentImpl_paymentToken",
            tooltip = "CustomerPaymentImpl_paymentToken_tooltip",
            tab = Presentation.Tab.Name.PAYMENT,
            tabOrder = Presentation.Tab.Order.PAYMENT,
            group = Presentation.Group.Name.PAYMENT,
            groupOrder = Presentation.Group.Order.PAYMENT)
    protected String paymentToken;

    @Column(name = "IS_DEFAULT")
    @AdminPresentation(friendlyName = "CustomerPaymentImpl_isDefault",
            tab = Presentation.Tab.Name.PAYMENT,
            tabOrder = Presentation.Tab.Order.PAYMENT,
            group = Presentation.Group.Name.PAYMENT,
            groupOrder = Presentation.Group.Order.PAYMENT)
    protected boolean isDefault = false;

    @ElementCollection
    @MapKeyType(@Type(type = "java.lang.String"))
    @Lob
    @Type(type = "org.hibernate.type.StringClobType")
    @CollectionTable(name = "BLC_CUSTOMER_PAYMENT_FIELDS", joinColumns = @JoinColumn(name = "CUSTOMER_PAYMENT_ID"))
    @MapKeyColumn(name = "FIELD_NAME", nullable = false)
    @Column(name = "FIELD_VALUE")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blStandardElements")
    @AdminPresentationMap(friendlyName = "CustomerPaymentImpl_additionalFields",
            tab = Presentation.Tab.Name.PAYMENT,
            tabOrder = Presentation.Tab.Order.PAYMENT,
            keyPropertyFriendlyName = "CustomerPaymentImpl_additional_field_key",
            forceFreeFormKeys = true)
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
    
    public static class Presentation {
        public static class Group {
            public static class Name {
                public static final String PAYMENT = "CustomerPaymentImpl_payment";

            }
            
            public static class Order {
                public static final int PAYMENT = 1000;
            }

        }
        
        public static class Tab {
            public static class Name {
                public static final String PAYMENT = "CustomerPaymentImpl_payment";
                public static final String BILLING_ADDRESS = "CustomerPaymentImpl_billingAddress";
            }
            
            public static class Order {
                public static final int PAYMENT = 1000;
                public static final int BILLING_ADDRESS = 2000;
            }
        }

    }

}
