/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.payment.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.currency.util.BroadleafCurrencyUtils;
import org.broadleafcommerce.common.currency.util.CurrencyCodeIdentifiable;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminPresentationCollection;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeEntry;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverride;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverrides;
import org.broadleafcommerce.common.presentation.override.PropertyType;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupImpl;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderImpl;
import org.broadleafcommerce.core.payment.service.type.PaymentType;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.AddressImpl;
import org.broadleafcommerce.profile.core.domain.CustomerPayment;
import org.broadleafcommerce.profile.core.domain.CustomerPaymentImpl;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_ORDER_PAYMENT")
@AdminPresentationMergeOverrides(
    {
        @AdminPresentationMergeOverride(name = "", mergeEntries =
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.READONLY,
                                            booleanOverrideValue = true)),
        @AdminPresentationMergeOverride(name = "billingAddress", mergeEntries = {
                @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.TAB,
                        overrideValue = FulfillmentGroupImpl.Presentation.Tab.Name.Address),
                @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.TABORDER,
                        intOverrideValue = FulfillmentGroupImpl.Presentation.Tab.Order.Address)
        }),
        @AdminPresentationMergeOverride(name = "billingAddress.isDefault", mergeEntries = {
                @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.EXCLUDED,
                        booleanOverrideValue = true)
        }),
        @AdminPresentationMergeOverride(name = "billingAddress.isActive", mergeEntries = {
                @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.EXCLUDED,
                        booleanOverrideValue = true)
        }),
        @AdminPresentationMergeOverride(name = "billingAddress.isBusiness", mergeEntries = {
                @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.EXCLUDED,
                        booleanOverrideValue = true)
        }),
    }
)
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "PaymentInfoImpl_basePaymentInfo")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_SITE)
})
public class OrderPaymentImpl implements OrderPayment, CurrencyCodeIdentifiable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "OrderPaymentId")
    @GenericGenerator(
        name="OrderPaymentId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="OrderPaymentImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.payment.domain.OrderPaymentImpl")
        }
    )
    @Column(name = "ORDER_PAYMENT_ID")
    protected Long id;

    @ManyToOne(targetEntity = OrderImpl.class, optional = false)
    @JoinColumn(name = "ORDER_ID")
    @Index(name="ORDERPAYMENT_ORDER_INDEX", columnNames={"ORDER_ID"})
    @AdminPresentation(excluded = true)
    protected Order order;

    @ManyToOne(targetEntity = AddressImpl.class, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "ADDRESS_ID")
    @Index(name="ORDERPAYMENT_ADDRESS_INDEX", columnNames={"ADDRESS_ID"})
    protected Address billingAddress;

    @Column(name = "AMOUNT", precision=19, scale=5)
    @AdminPresentation(friendlyName = "PaymentInfoImpl_Payment_Amount", order=2000, gridOrder = 2000, prominent=true,
            fieldType=SupportedFieldType.MONEY)
    protected BigDecimal amount;

    @Index(name="ORDERPAYMENT_REFERENCE_INDEX", columnNames={"REFERENCE_NUMBER"})
    @AdminPresentation(friendlyName = "PaymentInfoImpl_Payment_Reference_Number", order=1000, prominent=true,
            gridOrder = 1000)
    protected String referenceNumber;

    @Column(name = "PAYMENT_TYPE", nullable = false)
    @Index(name="ORDERPAYMENT_TYPE_INDEX", columnNames={"PAYMENT_TYPE"})
    @AdminPresentation(friendlyName = "PaymentInfoImpl_Payment_Type", order=3000, gridOrder = 3000, prominent=true,
            fieldType= SupportedFieldType.BROADLEAF_ENUMERATION,
            broadleafEnumeration="org.broadleafcommerce.core.payment.service.type.PaymentType")
    protected String type;
    
    @OneToMany(mappedBy = "orderPayment", targetEntity = PaymentTransactionImpl.class, cascade = { CascadeType.ALL }, orphanRemoval = true)
    @AdminPresentationCollection(friendlyName="PaymentInfoImpl_Details",
            tab = Presentation.Tab.Name.Log, tabOrder = Presentation.Tab.Order.Log)
    protected List<PaymentTransaction> transactions = new ArrayList<PaymentTransaction>();
    
    @ManyToOne(targetEntity = CustomerPaymentImpl.class)
    @JoinColumn(name = "CUSTOMER_PAYMENT_ID")
    @Index(name="CUSTOMER_PAYMENT", columnNames={"CUSTOMER_PAYMENT_ID"})
    @AdminPresentation(excluded = true) //don't display the payment token info in the admin by default
    protected CustomerPayment customerPayment;

    @Override
    public Money getAmount() {
        return amount == null ? null : BroadleafCurrencyUtils.getMoney(amount, getOrder().getCurrency());
    }

    @Override
    public void setAmount(Money amount) {
        this.amount = Money.toAmount(amount);
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Order getOrder() {
        return order;
    }

    @Override
    public void setOrder(Order order) {
        this.order = order;
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
    public String getReferenceNumber() {
        return referenceNumber;
    }

    @Override
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    @Override
    public PaymentType getType() {
        return PaymentType.getInstance(type);
    }

    @Override
    public void setType(PaymentType type) {
        this.type = type.getType();
    }

    @Override
    public CustomerPayment getCustomerPayment() {
        return customerPayment;
    }

    @Override
    public void setCustomerPayment(CustomerPayment customerPayment) {
        this.customerPayment = customerPayment;
    }

    @Override
    public List<PaymentTransaction> getTransactions() {
        return transactions;
    }

    @Override
    public void setTransactions(List<PaymentTransaction> transactions) {
        this.transactions = transactions;
    }
    
    @Override
    public Money getTransactionAmountForType(PaymentTransactionType type) {
        Money amount = BroadleafCurrencyUtils.getMoney(BigDecimal.ZERO, getOrder().getCurrency());
        for (PaymentTransaction trans : getTransactions()){
            if (type.equals(trans.getType())){
                amount = amount.add(trans.getAmount());
            }
        }
        return amount;
    }

    @Override
    public BroadleafCurrency getCurrency() {
        if (order != null) {
            return order.getCurrency();
        }
        return null;
    }

    @Override
    public String getCurrencyCode() {
        if (getCurrency() != null) {
            return getCurrency().getCurrencyCode();
        }
        return null;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof OrderPaymentImpl) {
            OrderPaymentImpl that = (OrderPaymentImpl) obj;
            return new EqualsBuilder()
                .append(this.id, that.id)
                .append(this.referenceNumber, that.referenceNumber)
                .append(this.type, that.type)
                .build();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(referenceNumber)
            .append(type)
            .build();
    }

    public static class Presentation {
        public static class Tab {
            public static class Name {
                public static final String Address = "PaymentInfoImpl_Address_Tab";
                public static final String Log = "PaymentInfoImpl_Log_Tab";
                public static final String Advanced = "PaymentInfoImpl_Advanced_Tab";
            }

            public static class Order {
                public static final int Address = 2000;
                public static final int Log = 4000;
                public static final int Advanced = 5000;
            }
        }

        public static class Group {
            public static class Name {
                public static final String Items = "PaymentInfoImpl_Items";
            }

            public static class Order {
                public static final int Items = 1000;
            }
        }

        public static class FieldOrder {
            public static final int REFNUMBER = 3000;
        }
    }
}
