/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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
package org.broadleafcommerce.core.payment.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.currency.util.BroadleafCurrencyUtils;
import org.broadleafcommerce.common.currency.util.CurrencyCodeIdentifiable;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.payment.PaymentGatewayType;
import org.broadleafcommerce.common.payment.PaymentTransactionType;
import org.broadleafcommerce.common.payment.PaymentType;
import org.broadleafcommerce.common.persistence.ArchiveStatus;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminPresentationCollection;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeEntry;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverride;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverrides;
import org.broadleafcommerce.common.presentation.override.PropertyType;
import org.broadleafcommerce.common.util.ApplicationContextHolder;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupImpl;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderImpl;
import org.broadleafcommerce.core.payment.service.OrderPaymentStatusService;
import org.broadleafcommerce.core.payment.service.type.OrderPaymentStatus;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.AddressImpl;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.context.ApplicationContext;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
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
        })
    }
)
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "OrderPaymentImpl_baseOrderPayment")
@SQLDelete(sql="UPDATE BLC_ORDER_PAYMENT SET ARCHIVED = 'Y' WHERE ORDER_PAYMENT_ID = ?")
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

    @ManyToOne(targetEntity = OrderImpl.class, optional = true)
    @JoinColumn(name = "ORDER_ID", nullable = true)
    @Index(name="ORDERPAYMENT_ORDER_INDEX", columnNames={"ORDER_ID"})
    @AdminPresentation(excluded = true)
    protected Order order;

    @ManyToOne(targetEntity = AddressImpl.class, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "ADDRESS_ID")
    @Index(name="ORDERPAYMENT_ADDRESS_INDEX", columnNames={"ADDRESS_ID"})
    protected Address billingAddress;

    @Column(name = "AMOUNT", precision=19, scale=5)
    @AdminPresentation(friendlyName = "OrderPaymentImpl_Payment_Amount", order=2000, gridOrder = 2000, prominent=true,
            fieldType=SupportedFieldType.MONEY)
    protected BigDecimal amount;

    @Column(name = "REFERENCE_NUMBER")
    @Index(name="ORDERPAYMENT_REFERENCE_INDEX", columnNames={"REFERENCE_NUMBER"})
    @AdminPresentation(friendlyName = "OrderPaymentImpl_Payment_Reference_Number")
    protected String referenceNumber;

    @Column(name = "PAYMENT_TYPE", nullable = false)
    @Index(name="ORDERPAYMENT_TYPE_INDEX", columnNames={"PAYMENT_TYPE"})
    @AdminPresentation(friendlyName = "OrderPaymentImpl_Payment_Type", order=3000, gridOrder = 3000, prominent=true,
            fieldType= SupportedFieldType.BROADLEAF_ENUMERATION,
            broadleafEnumeration="org.broadleafcommerce.common.payment.PaymentType")
    protected String type;
    
    @Column(name = "GATEWAY_TYPE")
    @AdminPresentation(friendlyName = "OrderPaymentImpl_Gateway_Type", order=1000, gridOrder = 1000, prominent=true,
            fieldType = SupportedFieldType.BROADLEAF_ENUMERATION,
            broadleafEnumeration="org.broadleafcommerce.common.payment.PaymentGatewayType")
    protected String gatewayType;

    @OneToMany(mappedBy = "orderPayment", targetEntity = PaymentTransactionImpl.class, cascade = { CascadeType.ALL }, orphanRemoval = true)
    @Where(clause = "archived != 'Y'")
    @AdminPresentationCollection(friendlyName="OrderPaymentImpl_Details",
            tab = Presentation.Tab.Name.Log, tabOrder = Presentation.Tab.Order.Log)
    protected List<PaymentTransaction> transactions = new ArrayList<PaymentTransaction>();

    @Embedded
    protected ArchiveStatus archiveStatus = new ArchiveStatus();
    
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
        this.type = type == null ? null : type.getType();
    }
    
    @Override
    public PaymentGatewayType getGatewayType() {
        return PaymentGatewayType.getInstance(gatewayType);
    }

    @Override
    public void setPaymentGatewayType(PaymentGatewayType gatewayType) {
        this.gatewayType = gatewayType == null ? null : gatewayType.getType();
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
    public void addTransaction(PaymentTransaction transaction) {
        getTransactions().add(transaction);
    }

    @Override
    public List<PaymentTransaction> getTransactionsForType(PaymentTransactionType type) {
        List<PaymentTransaction> result = new ArrayList<PaymentTransaction>();
        for (PaymentTransaction tx : getTransactions()) {
            if (tx.getType().equals(type)) {
                result.add(tx);
            }
        }
        return result;
    }

    @Override
    public PaymentTransaction getInitialTransaction() {
        for (PaymentTransaction tx : getTransactions()) {
            if (tx.getParentTransaction() == null) {
                return tx;
            }
        }
        return null;
    }

    @Override
    public PaymentTransaction getAuthorizeTransaction() {
        for (PaymentTransaction tx : getTransactions()){
            if (PaymentTransactionType.AUTHORIZE.equals(tx.getType())
                    || PaymentTransactionType.AUTHORIZE_AND_CAPTURE.equals(tx.getType())){
                return tx;
            }
        }
        return null;
    }

    @Override
    public Money getTransactionAmountForType(PaymentTransactionType type) {
        Money amount = BroadleafCurrencyUtils.getMoney(BigDecimal.ZERO, getOrder().getCurrency());
        for (PaymentTransaction tx : getTransactions()){
            if (type.equals(tx.getType())) {
                amount = amount.add(tx.getAmount());
            }
        }
        return amount;
    }
    
    @Override
    public Money getSuccessfulTransactionAmountForType(PaymentTransactionType type) {
        Money amount = BroadleafCurrencyUtils.getMoney(BigDecimal.ZERO, getOrder().getCurrency());
        for (PaymentTransaction tx : getTransactions()){
            if (type.equals(tx.getType()) && tx.getSuccess()){
                amount = amount.add(tx.getAmount());
            }
        }
        return amount;
    }

    @Override
    public OrderPaymentStatus getStatus() {
        ApplicationContext ctx = ApplicationContextHolder.getApplicationContext();
        if (ctx == null) {
            return null;
        }

        OrderPaymentStatusService svc = ctx.getBean("blOrderPaymentStatusService", OrderPaymentStatusService.class);
        return svc.determineOrderPaymentStatus(this);
    }

    @Override
    public boolean isConfirmed() {
        for (PaymentTransaction tx : getTransactions()){
            if ((PaymentTransactionType.AUTHORIZE_AND_CAPTURE.equals(tx.getType()) ||
                    PaymentTransactionType.AUTHORIZE.equals(tx.getType()))
                    && tx.getSuccess()){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isFinalPayment() {
        return getType().getIsFinalPayment();
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
    public void setArchived(Character archived) {
        if (archiveStatus == null) {
            archiveStatus = new ArchiveStatus();
        }
        archiveStatus.setArchived(archived);
    }

    @Override
    public boolean isActive() {
        return 'Y' != getArchived();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj != null && getClass().isAssignableFrom(obj.getClass())) {
            OrderPaymentImpl that = (OrderPaymentImpl) obj;
            return new EqualsBuilder()
                .append(this.id, that.id)
                .append(this.referenceNumber, that.referenceNumber)
                .append(this.type, that.type)
                .append(this.archiveStatus, that.archiveStatus)
                .build();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(referenceNumber)
            .append(type)
            .append(archiveStatus)
            .build();
    }

    @Override
    public <G extends OrderPayment> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        OrderPayment cloned = createResponse.getClone();
        //some payment types will not have a billing address, e.g. cash on delivery
        cloned.setBillingAddress(billingAddress == null ? null : billingAddress.createOrRetrieveCopyInstance(context).getClone());
        cloned.setReferenceNumber(referenceNumber);
        cloned.setAmount(amount == null ? null : new Money(amount));
        cloned.setOrder(order);
        cloned.setPaymentGatewayType(PaymentGatewayType.getInstance(gatewayType));
        cloned.setType(PaymentType.getInstance(type));
        cloned.setArchived(getArchived());
        for (PaymentTransaction transaction : transactions) {
            if (transaction.isActive()) {
                PaymentTransaction cpt = transaction.createOrRetrieveCopyInstance(context).getClone();
                cpt.setOrderPayment(cloned);
                cloned.getTransactions().add(cpt);
            }
        }

        return createResponse;
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
