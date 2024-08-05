/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.payment.domain;

import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.currency.util.BroadleafCurrencyUtils;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.payment.PaymentTransactionType;
import org.broadleafcommerce.common.persistence.ArchiveStatus;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationMap;
import org.broadleafcommerce.common.presentation.AdminPresentationToOneLookup;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.UnspecifiedBooleanType;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeEntry;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverride;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverrides;
import org.broadleafcommerce.common.presentation.override.PropertyType;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.MapKeyType;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;

/**
 * @author Jerry Ocanas (jocanas)
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_ORDER_PAYMENT_TRANSACTION")
@SQLDelete(sql="UPDATE BLC_ORDER_PAYMENT_TRANSACTION SET ARCHIVED = 'Y' WHERE PAYMENT_TRANSACTION_ID = ?")
@AdminPresentationMergeOverrides(
    {
        @AdminPresentationMergeOverride(name = "", mergeEntries =
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.READONLY,
                                            booleanOverrideValue = true))
    }
)
public class PaymentTransactionImpl implements PaymentTransaction {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "PaymentTransactionId")
    @GenericGenerator(
        name="PaymentTransactionId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="PaymentTransactionImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.payment.domain.PaymentTransactionImpl")
        }
    )
    @Column(name = "PAYMENT_TRANSACTION_ID")
    protected Long id;

    @Column(name = "TRANSACTION_TYPE")
    @AdminPresentation(friendlyName = "PaymentTransactionImpl_Type",
            fieldType = SupportedFieldType.BROADLEAF_ENUMERATION,
            broadleafEnumeration = "org.broadleafcommerce.common.payment.PaymentTransactionType",
            prominent = true, gridOrder = 1000)
    protected String type;

    @Column(name = "TRANSACTION_AMOUNT")
    @AdminPresentation(friendlyName = "PaymentTransactionImpl_Amount", fieldType = SupportedFieldType.MONEY,
        prominent = true, gridOrder = 2000)
    protected BigDecimal amount;

    @Column(name = "DATE_RECORDED")
    @AdminPresentation(friendlyName = "PaymentTransactionImpl_Date", prominent = true, gridOrder = 3000)
    protected Date date;
    
    @Column(name = "CUSTOMER_IP_ADDRESS", nullable = true)
    @AdminPresentation(friendlyName = "PaymentTransactionImpl_Payment_IP_Address", order=4000)
    protected String customerIpAddress;
    
    @Column(name = "RAW_RESPONSE", length = Integer.MAX_VALUE - 1)
    @Lob
    @Type(type = "org.hibernate.type.MaterializedClobType")
    @AdminPresentation(friendlyName = "PaymentTransactionImpl_Raw_Response")
    protected String rawResponse;
    
    @Column(name = "SUCCESS")
    @AdminPresentation(friendlyName = "PaymentTransactionImpl_Success")
    protected Boolean success = true;
    
    @Embedded
    protected ArchiveStatus archiveStatus = new ArchiveStatus();
    
    @ManyToOne(targetEntity = OrderPaymentImpl.class, optional = false)
    @JoinColumn(name = "ORDER_PAYMENT")
    @AdminPresentation(excluded = true)
    protected OrderPayment orderPayment;
    
    /**
     * Necessary for operations on a payment that require something to have happened beforehand. For instance, an AUTHORIZE
     * would not have a parent but a CAPTURE must have an AUTHORIZE parent and a REFUND must have a CAPTURE parent
     */
    @ManyToOne(targetEntity = PaymentTransactionImpl.class)
    @JoinColumn(name = "PARENT_TRANSACTION")
    @AdminPresentation(friendlyName = "Parent Transaction")
    @AdminPresentationToOneLookup()
    protected PaymentTransaction parentTransaction;

    @ElementCollection
    @MapKeyColumn(name="FIELD_NAME")
    @MapKeyType(@Type(type = "java.lang.String"))
    @Lob
    @Type(type = "org.hibernate.type.MaterializedClobType")
    @Column(name="FIELD_VALUE", length = Integer.MAX_VALUE - 1)
    @CollectionTable(name="BLC_TRANS_ADDITNL_FIELDS", joinColumns=@JoinColumn(name="PAYMENT_TRANSACTION_ID"))
    @BatchSize(size = 50)
    @AdminPresentationMap(friendlyName = "PaymentTransactionImpl_Additional_Fields", isSimpleValue = UnspecifiedBooleanType.TRUE,
        forceFreeFormKeys = true, keyPropertyFriendlyName = "PaymentTransactionImpl_Additional_Fields_Name"
    )
    protected Map<String, String> additionalFields = new HashMap<String, String>();

    @Column(name = "SAVE_TOKEN")
    @AdminPresentation(friendlyName = "PaymentTransactionImpl_Save_Token")
    protected Boolean saveToken = false;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public OrderPayment getOrderPayment() {
        if (orderPayment == null && parentTransaction != null) {
            return parentTransaction.getOrderPayment();
        }
        return orderPayment;
    }

    @Override
    public void setOrderPayment(OrderPayment orderPayment) {
        this.orderPayment = orderPayment;
    }
    
    @Override
    public PaymentTransaction getParentTransaction() {
        return parentTransaction;
    }

    @Override
    public void setParentTransaction(PaymentTransaction parentTransaction) {
        this.parentTransaction = parentTransaction;
    }

    @Override
    public PaymentTransactionType getType() {
        return PaymentTransactionType.getInstance(type);
    }

    @Override
    public void setType(PaymentTransactionType type) {
        this.type = (type == null) ? null : type.getType();
    }

    @Override
    public Money getAmount() {
        return amount == null ? BroadleafCurrencyUtils.getMoney(BigDecimal.ZERO, getOrderPayment().getCurrency()) : BroadleafCurrencyUtils.getMoney(amount, getOrderPayment().getCurrency());
    }

    @Override
    public void setAmount(Money amount) {
        if (amount != null) {
            this.amount = amount.getAmount();
        }
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public void setDate(Date date) {
        this.date = date;
    }
    
    @Override
    public String getCustomerIpAddress() {
        return customerIpAddress;
    }

    @Override
    public void setCustomerIpAddress(String customerIpAddress) {
        this.customerIpAddress = customerIpAddress;
    }
    
    @Override
    public String getRawResponse() {
        return rawResponse;
    }
    
    @Override
    public void setRawResponse(String rawResponse) {
        this.rawResponse = rawResponse;
    }
    
    @Override
    public Boolean getSuccess() {
        return success;
    }

    @Override
    public void setSuccess(Boolean success) {
        this.success = success;
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
    public boolean isSaveToken() {
        return saveToken == null ? false : saveToken;
    }

    @Override
    public void setSaveToken(boolean saveToken) {
        this.saveToken = saveToken;
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
    public <G extends PaymentTransaction> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        PaymentTransaction cloned = createResponse.getClone();
        cloned.setOrderPayment(orderPayment.createOrRetrieveCopyInstance(context).getClone());
        cloned.setArchived(getArchived());
        cloned.setAmount(amount == null ? null : new Money(amount));
        cloned.setCustomerIpAddress(customerIpAddress);
        cloned.setDate(date);
        cloned.setParentTransaction(parentTransaction);
        cloned.setRawResponse(rawResponse);
        cloned.setSuccess(success);
        cloned.setType(PaymentTransactionType.getInstance(type));

        for(Map.Entry<String, String> entry : additionalFields.entrySet()){
            cloned.getAdditionalFields().put(entry.getKey(),entry.getValue());
        }
        return createResponse;
    }
}
