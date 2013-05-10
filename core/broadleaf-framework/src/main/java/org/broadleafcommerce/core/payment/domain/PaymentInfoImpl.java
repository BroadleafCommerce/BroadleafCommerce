/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.payment.domain;

import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.currency.util.BroadleafCurrencyUtils;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminPresentationCollection;
import org.broadleafcommerce.common.presentation.AdminPresentationMap;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMerge;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeEntry;
import org.broadleafcommerce.common.presentation.override.AdminPresentationOverride;
import org.broadleafcommerce.common.presentation.override.AdminPresentationOverrides;
import org.broadleafcommerce.common.presentation.override.AdminPresentationPropertyType;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupImpl;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderImpl;
import org.broadleafcommerce.core.payment.service.type.PaymentInfoType;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.AddressImpl;
import org.broadleafcommerce.profile.core.domain.CustomerPayment;
import org.broadleafcommerce.profile.core.domain.CustomerPaymentImpl;
import org.broadleafcommerce.profile.core.domain.Phone;
import org.broadleafcommerce.profile.core.domain.PhoneImpl;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Index;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_ORDER_PAYMENT")
@AdminPresentationOverrides(
    value = {
        @AdminPresentationOverride(name="address", mergeValue = @AdminPresentationMerge(
                mergeEntries = {
                        @AdminPresentationMergeEntry(propertyType = AdminPresentationPropertyType.tab,
                                overrideValue = FulfillmentGroupImpl.Presentation.Tab.Name.Address),
                        @AdminPresentationMergeEntry(propertyType = AdminPresentationPropertyType.tabOrder,
                                intOverrideValue = FulfillmentGroupImpl.Presentation.Tab.Order.Address)
                })
        ),
        @AdminPresentationOverride(name="address.isDefault", mergeValue = @AdminPresentationMerge(
                mergeEntries = {
                        @AdminPresentationMergeEntry(propertyType = AdminPresentationPropertyType.excluded,
                                booleanOverrideValue = true)
                })
        ),
        @AdminPresentationOverride(name="address.isActive", mergeValue = @AdminPresentationMerge(
                mergeEntries = {
                        @AdminPresentationMergeEntry(propertyType = AdminPresentationPropertyType.excluded,
                                booleanOverrideValue = true)
                })
        ),
        @AdminPresentationOverride(name="address.isBusiness", mergeValue = @AdminPresentationMerge(
                mergeEntries = {
                        @AdminPresentationMergeEntry(propertyType = AdminPresentationPropertyType.excluded,
                                booleanOverrideValue = true)
                })
        ),
        @AdminPresentationOverride(name="phone", mergeValue = @AdminPresentationMerge(
                mergeEntries = {
                        @AdminPresentationMergeEntry(propertyType = AdminPresentationPropertyType.excluded,
                                booleanOverrideValue = true)
                })
        ),
        @AdminPresentationOverride(name="phone.phoneNumber", mergeValue = @AdminPresentationMerge(
                mergeEntries = {
                        @AdminPresentationMergeEntry(propertyType = AdminPresentationPropertyType.excluded,
                                booleanOverrideValue = false),
                        @AdminPresentationMergeEntry(propertyType = AdminPresentationPropertyType.order,
                                intOverrideValue = FulfillmentGroupImpl.Presentation.FieldOrder.PHONE),
                        @AdminPresentationMergeEntry(propertyType = AdminPresentationPropertyType.group,
                                overrideValue = "General"),
                        @AdminPresentationMergeEntry(propertyType = AdminPresentationPropertyType.requiredOverride,
                                overrideValue = "NOT_REQUIRED")
                })
        )
    }
)
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "PaymentInfoImpl_basePaymentInfo")
public class PaymentInfoImpl implements PaymentInfo {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "PaymentInfoId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "PaymentInfoId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "PaymentInfoImpl", allocationSize = 50)
    @Column(name = "PAYMENT_ID")
    protected Long id;

    @ManyToOne(targetEntity = OrderImpl.class, optional = false)
    @JoinColumn(name = "ORDER_ID")
    @Index(name="ORDERPAYMENT_ORDER_INDEX", columnNames={"ORDER_ID"})
    @AdminPresentation(excluded = true)
    protected Order order;

    @ManyToOne(targetEntity = AddressImpl.class, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "ADDRESS_ID")
    @Index(name="ORDERPAYMENT_ADDRESS_INDEX", columnNames={"ADDRESS_ID"})
    protected Address address;

    @ManyToOne(targetEntity = PhoneImpl.class, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "PHONE_ID")
    @Index(name="ORDERPAYMENT_PHONE_INDEX", columnNames={"PHONE_ID"})
    protected Phone phone;

    @Column(name = "AMOUNT", precision=19, scale=5)
    @AdminPresentation(friendlyName = "PaymentInfoImpl_Payment_Amount", order=2000, gridOrder = 2000, prominent=true, fieldType=SupportedFieldType.MONEY)
    protected BigDecimal amount;

    @Column(name = "REFERENCE_NUMBER")
    @Index(name="ORDERPAYMENT_REFERENCE_INDEX", columnNames={"REFERENCE_NUMBER"})
    @AdminPresentation(friendlyName = "PaymentInfoImpl_Payment_Reference_Number", order=1000, prominent=true, gridOrder = 1000)
    protected String referenceNumber;

    @Column(name = "PAYMENT_TYPE", nullable = false)
    @Index(name="ORDERPAYMENT_TYPE_INDEX", columnNames={"PAYMENT_TYPE"})
    @AdminPresentation(friendlyName = "PaymentInfoImpl_Payment_Type", order=3000, gridOrder = 3000, prominent=true, fieldType= SupportedFieldType.BROADLEAF_ENUMERATION, broadleafEnumeration="org.broadleafcommerce.core.payment.service.type.PaymentInfoType")
    protected String type;
    
    @OneToMany(mappedBy = "paymentInfo", targetEntity = AmountItemImpl.class, cascade = {CascadeType.ALL})
    @AdminPresentationCollection(friendlyName="PaymentInfoImpl_Amount_Items",
            tab = Presentation.Tab.Name.Advanced, tabOrder = Presentation.Tab.Order.Advanced)
    protected List<AmountItem> amountItems = new ArrayList<AmountItem>();
    
    @Column(name = "CUSTOMER_IP_ADDRESS", nullable = true)
    @AdminPresentation(friendlyName = "PaymentInfoImpl_Payment_IP_Address", order=4000)
    protected String customerIpAddress;

    @ElementCollection
    @MapKeyColumn(name="FIELD_NAME")
    @Column(name="FIELD_VALUE")
    @CollectionTable(name="BLC_PAYINFO_ADDITIONAL_FIELDS", joinColumns=@JoinColumn(name="PAYMENT_ID"))
    @BatchSize(size = 50)
    @AdminPresentationMap(friendlyName = "PaymentInfoImpl_Additional_Fields",
        forceFreeFormKeys = true, keyPropertyFriendlyName = "PaymentInfoImpl_Additional_Fields_Name"
    )
    protected Map<String, String> additionalFields = new HashMap<String, String>();

    @OneToMany(mappedBy = "paymentInfo", targetEntity = PaymentInfoDetailImpl.class, cascade = {CascadeType.ALL})
    @AdminPresentationCollection(friendlyName="PaymentInfoImpl_Details",
            tab = Presentation.Tab.Name.Log, tabOrder = Presentation.Tab.Order.Log)
    protected List<PaymentInfoDetail> details = new ArrayList<PaymentInfoDetail>();

    @ManyToOne(targetEntity = CustomerPaymentImpl.class)
    @JoinColumn(name = "CUSTOMER_PAYMENT_ID")
    @Index(name="CUSTOMER_PAYMENT", columnNames={"CUSTOMER_PAYMENT_ID"})
    @AdminPresentation(excluded = true) //don't display the payment token info in the admin by default
    protected CustomerPayment customerPayment;

    @Transient
    protected Map<String, String[]> requestParameterMap = new HashMap<String, String[]>();

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
    public Address getAddress() {
        return address;
    }

    @Override
    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public Phone getPhone() {
        return phone;
    }

    @Override
    public void setPhone(Phone phone) {
        this.phone = phone;
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
    public PaymentInfoType getType() {
        return PaymentInfoType.getInstance(type);
    }

    @Override
    public void setType(PaymentInfoType type) {
        this.type = type.getType();
    }

    @Override
    public List<AmountItem> getAmountItems() {
        return amountItems;
    }

    @Override
    public void setAmountItems(List<AmountItem> amountItems) {
        this.amountItems = amountItems;
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
    public Map<String, String> getAdditionalFields() {
        return additionalFields;
    }

    @Override
    public void setAdditionalFields(Map<String, String> additionalFields) {
        this.additionalFields = additionalFields;
    }

    @Override
    public Map<String, String[]> getRequestParameterMap() {
        return requestParameterMap;
    }

    @Override
    public void setRequestParameterMap(Map<String, String[]> requestParameterMap) {
        this.requestParameterMap = requestParameterMap;
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
    public List<PaymentInfoDetail> getPaymentInfoDetails() {
        return details;
    }

    @Override
    public void setPaymentInfoDetails(List<PaymentInfoDetail> details) {
        this.details = details;
    }

    @Override
    public Money getPaymentCapturedAmount() {
        return getDetailsAmountForType(PaymentInfoDetailType.CAPTURE);
    }

    @Override
    public Money getPaymentCreditedAmount() {
        return getDetailsAmountForType(PaymentInfoDetailType.REFUND);
    }

    @Override
    public Money getReverseAuthAmount() {
        return getDetailsAmountForType(PaymentInfoDetailType.REVERSE_AUTH);
    }

    public Money getDetailsAmountForType(PaymentInfoDetailType type){
        Money amount = BroadleafCurrencyUtils.getMoney(BigDecimal.ZERO, getOrder().getCurrency());
        for (PaymentInfoDetail detail : details){
            if (type.equals(detail.getType())){
                amount = amount.add(detail.getAmount());
            }
        }
        return amount;
    }

    public BroadleafCurrency getCurrency() {
        if (order != null) {
            return order.getCurrency();
        } else {
            return null;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        PaymentInfoImpl other = (PaymentInfoImpl) obj;

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (referenceNumber == null) {
            if (other.referenceNumber != null) {
                return false;
            }
        } else if (!referenceNumber.equals(other.referenceNumber)) {
            return false;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((referenceNumber == null) ? 0 : referenceNumber.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public Referenced createEmptyReferenced() {
        if (getReferenceNumber() == null) {
            throw new RuntimeException("referenceNumber must be already set");
        }
        EmptyReferenced emptyReferenced = new EmptyReferenced();
        emptyReferenced.setReferenceNumber(getReferenceNumber());

        return emptyReferenced;
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
