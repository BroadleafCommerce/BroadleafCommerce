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

import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.persistence.ArchiveStatus;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminPresentationMap;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeEntry;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverride;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverrides;
import org.broadleafcommerce.common.presentation.override.PropertyType;
import org.broadleafcommerce.common.time.domain.TemporalTimestampListener;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.MapKeyType;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
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
public class CustomerPaymentImpl implements CustomerPayment, AdditionalFields {

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

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = AddressImpl.class, optional = true)
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

    @Column(name = "LAST_PAYMENT_STATUS")
    @AdminPresentation(friendlyName = "CustomerPaymentImpl_Status", fieldType = SupportedFieldType.BROADLEAF_ENUMERATION,
            broadleafEnumeration = "org.broadleafcommerce.profile.core.domain.LastPaymentStatus",
            prominent = false)
    protected String lastPaymentStatus;

    @Column(name = "EXPIRATION_DATE")
    @AdminPresentation(friendlyName = "CustomerPaymentImpl_Expiration_Date", order = 1000)
    protected Date expirationDate;

    @Column(name = "NAME")
    @AdminPresentation(friendlyName = "CustomerPaymentImpl_Name", order = 1000)
    protected String name;

    @Column(name = "LAST_FOUR")
    @AdminPresentation(friendlyName = "CustomerPaymentImpl_Last_Four", order = 1000)
    protected String lastFour;

    @Column(name = "CARD_TYPE")
    @AdminPresentation(friendlyName = "CustomerPaymentImpl_Card_Type", fieldType = SupportedFieldType.BROADLEAF_ENUMERATION,
            broadleafEnumeration = "org.broadleafcommerce.common.payment.CreditCardType",
            prominent = false)
    protected String cardType;

    @Column(name = "LAST_EXPIRATION_NOTIFICATION")
    @AdminPresentation(friendlyName = "CustomerPaymentImpl_Last_Expiration_Notification", order = 1001)
    protected Date lastExpirationNotification;

    /**
     * indicates whether the lastExpirationNotification date refers to an actual expiration, or
     * to a warning about a future expiration
     */
    @Column(name = "ACTUAL_EXPIRATION")
    protected boolean actualExpiration;

    @ElementCollection(fetch = FetchType.EAGER)
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

    @Embedded
    protected ArchiveStatus archiveStatus = new ArchiveStatus();

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
    public void setIsDefault(boolean aDefault) {
        this.isDefault = aDefault;
    }

    @Override
    public String getLastPaymentStatus() {
        return lastPaymentStatus;
    }

    @Override
    public void setLastPaymentStatus(String aDefault) {
        this.lastPaymentStatus = aDefault;
    }

    @Override
    public Date getExpirationDate() {
        return expirationDate;
    }

    @Override
    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getLastFour() {
        return lastFour;
    }

    @Override
    public void setLastFour(String lastFour) {
        this.lastFour = lastFour;
    }

    @Override
    public String getCardType() {
        return cardType;
    }

    @Override
    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    @Override
    public Map<String, String> getAdditionalFields() {
        return additionalFields;
    }

    @Override
    public void setAdditionalFields(Map<String, String> additionalFields) {
        this.additionalFields = additionalFields;
    }

    @Override
    public Character getArchived() {
        ArchiveStatus temp;
        if (archiveStatus == null) {
            temp = new ArchiveStatus();
        } else {
            temp = archiveStatus;
        }
        return temp.getArchived();
    }

    @Override
    public boolean isActive() {
        return 'Y'!=getArchived();
    }

    @Override
    public void setArchived(Character archived) {
        if (archiveStatus == null) {
            archiveStatus = new ArchiveStatus();
        }
        archiveStatus.setArchived(archived);
    }
    
    @Override
    public <G extends CustomerPayment> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        CustomerPayment cloned = createResponse.getClone();
        // dont clone
        cloned.setCustomer(customer);
        cloned.setBillingAddress(billingAddress.createOrRetrieveCopyInstance(context).getClone());
        cloned.setIsDefault(isDefault);
        cloned.setPaymentToken(paymentToken);
        for (Map.Entry<String, String> entry : additionalFields.entrySet()) {
            cloned.getAdditionalFields().put(entry.getKey(), entry.getValue());
        }
        return createResponse;
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

    @Override
    public Date getLastExpirationNotification() {
        return lastExpirationNotification;
    }

    @Override
    public void setLastExpirationNotification(Date lastExpirationNotification) {
        this.lastExpirationNotification = lastExpirationNotification;
    }

    @Override
    public boolean isActualExpiration() {
        return actualExpiration;
    }

    @Override
    public void setActualExpiration(boolean actualExpiration) {
        this.actualExpiration = actualExpiration;
    }

}
