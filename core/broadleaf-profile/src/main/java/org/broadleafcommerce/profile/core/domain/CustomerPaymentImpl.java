/*
 * #%L
 * BroadleafCommerce Profile
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

package org.broadleafcommerce.profile.core.domain;

import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.payment.PaymentAdditionalFieldType;
import org.broadleafcommerce.common.payment.PaymentGatewayType;
import org.broadleafcommerce.common.payment.PaymentType;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationMap;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeEntry;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverride;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverrides;
import org.broadleafcommerce.common.presentation.override.PropertyType;
import org.broadleafcommerce.common.time.domain.TemporalTimestampListener;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.MapKeyType;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

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
@EntityListeners(value = { TemporalTimestampListener.class, CustomerPaymentPersistedEntityListener.class })
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_CUSTOMER_PAYMENT", uniqueConstraints = @UniqueConstraint(name = "CSTMR_PAY_UNIQUE_CNSTRNT", columnNames = { "CUSTOMER_ID", "PAYMENT_TOKEN" }))
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blOrderElements")
@AdminPresentationMergeOverrides(
{
        @AdminPresentationMergeOverride(name = "billingAddress.addressLine1", mergeEntries = {
                @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.PROMINENT, booleanOverrideValue = true),
                @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.GRIDORDER, intOverrideValue = 3000)
        }),
        @AdminPresentationMergeOverride(name = "billingAddress.", mergeEntries = {
                @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.TAB, overrideValue = CustomerPaymentAdminPresentation.TabName.BillingAddress),
                @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.TABORDER, intOverrideValue = CustomerPaymentAdminPresentation.TabOrder.BillingAddress)
        })
})
public class CustomerPaymentImpl implements CustomerPayment, CustomerPaymentAdminPresentation {

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
    @AdminPresentation(excluded = true, visibility = VisibilityEnum.HIDDEN_ALL)
    protected Customer customer;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH }, targetEntity = AddressImpl.class, optional = true)
    @JoinColumn(name = "ADDRESS_ID")
    protected Address billingAddress;

    @Column(name = "PAYMENT_TOKEN")
    @AdminPresentation(friendlyName = "CustomerPaymentImpl_paymentToken",
            tooltip = "CustomerPaymentImpl_paymentToken_tooltip",
            group = GroupName.Payment, order = FieldOrder.PAYMENT_TOKEN)
    protected String paymentToken;

    @Column(name = "PAYMENT_TYPE")
    @Index(name="CUSTOMERPAYMENT_TYPE_INDEX", columnNames={"PAYMENT_TYPE"})
    @AdminPresentation(friendlyName = "CustomerPaymentImpl_Payment_Type",
            group = GroupName.Payment, order = FieldOrder.PAYMENT_TYPE,
            fieldType= SupportedFieldType.BROADLEAF_ENUMERATION,
            broadleafEnumeration="org.broadleafcommerce.common.payment.PaymentType",
            prominent=true, gridOrder = 1000)
    protected String paymentType;

    @Column(name = "GATEWAY_TYPE")
    @AdminPresentation(friendlyName = "CustomerPaymentImpl_Gateway_Type",
            group = GroupName.Payment, order = FieldOrder.PAYMENT_GATEWAY_TYPE,
            fieldType = SupportedFieldType.BROADLEAF_ENUMERATION,
            broadleafEnumeration="org.broadleafcommerce.common.payment.PaymentGatewayType",
            prominent=true, gridOrder = 2000)
    protected String paymentGatewayType;

    @Column(name = "IS_DEFAULT")
    @AdminPresentation(friendlyName = "CustomerPaymentImpl_isDefault",
            group = GroupName.Payment, order = FieldOrder.IS_DEFAULT)
    protected boolean isDefault = false;

    @ElementCollection()
    @MapKeyType(@Type(type = "java.lang.String"))
    @Lob
    @Type(type = "org.hibernate.type.StringClobType")
    @CollectionTable(name = "BLC_CUSTOMER_PAYMENT_FIELDS", joinColumns = @JoinColumn(name = "CUSTOMER_PAYMENT_ID"))
    @MapKeyColumn(name = "FIELD_NAME", nullable = false)
    @Column(name = "FIELD_VALUE", length = Integer.MAX_VALUE - 1)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blStandardElements")
    @AdminPresentationMap(friendlyName = "CustomerPaymentImpl_additionalFields",
            tab = TabName.Payment,
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
    public PaymentType getPaymentType() {
        if (PaymentType.getInstance(paymentType) != null) {
            return PaymentType.getInstance(paymentType);
        }

        //support legacy customer payments that may have stored the type on the additional fields map
        return !additionalFields.containsKey(PaymentAdditionalFieldType.PAYMENT_TYPE.getType()) ? null :
                PaymentType.getInstance(additionalFields.get(PaymentAdditionalFieldType.PAYMENT_TYPE.getType()));
    }

    @Override
    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType == null ? null : paymentType.getType();
    }

    @Override
    public PaymentGatewayType getPaymentGatewayType() {
        return PaymentGatewayType.getInstance(paymentGatewayType);
    }

    @Override
    public void setPaymentGatewayType(PaymentGatewayType paymentGatewayType) {
        this.paymentGatewayType = paymentGatewayType == null ? null : paymentGatewayType.getType();
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
    public Map<String, String> getAdditionalFields() {
        return additionalFields;
    }

    @Override
    public void setAdditionalFields(Map<String, String> additionalFields) {
        this.additionalFields = additionalFields;
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

}
