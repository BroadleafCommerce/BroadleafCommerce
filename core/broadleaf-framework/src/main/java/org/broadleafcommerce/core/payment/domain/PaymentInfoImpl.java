/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.payment.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderImpl;
import org.broadleafcommerce.core.payment.service.type.PaymentInfoType;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.AddressImpl;
import org.broadleafcommerce.profile.core.domain.Phone;
import org.broadleafcommerce.profile.core.domain.PhoneImpl;
import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.MapKey;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_ORDER_PAYMENT")
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "basePaymentInfo")
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
    @AdminPresentation(excluded = true, visibility = VisibilityEnum.HIDDEN_ALL)
    protected Order order;

    @ManyToOne(targetEntity = AddressImpl.class, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "ADDRESS_ID")
    @Index(name="ORDERPAYMENT_ADDRESS_INDEX", columnNames={"ADDRESS_ID"})
    @AdminPresentation(friendlyName="PaymentInfoImpl_Payment_Address", order=1, group="Address")
    protected Address address;

    @ManyToOne(targetEntity = PhoneImpl.class, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "PHONE_ID")
    @Index(name="ORDERPAYMENT_PHONE_INDEX", columnNames={"PHONE_ID"})
    @AdminPresentation(friendlyName="PaymentInfoImpl_Payment_Phone", order=1, group="Phone")
    protected Phone phone;

    @Column(name = "AMOUNT", precision=19, scale=5)
    @AdminPresentation(friendlyName="PaymentInfoImpl_Payment_Amount", order=3, group="Description", prominent=true, fieldType=SupportedFieldType.MONEY)
    protected BigDecimal amount;

    @Column(name = "REFERENCE_NUMBER")
    @Index(name="ORDERPAYMENT_REFERENCE_INDEX", columnNames={"REFERENCE_NUMBER"})
    @AdminPresentation(friendlyName="PaymentInfoImpl_Payment_Reference_Number", order=1, group="Description", prominent=true)
    protected String referenceNumber;

    @Column(name = "PAYMENT_TYPE", nullable = false)
    @Index(name="ORDERPAYMENT_TYPE_INDEX", columnNames={"PAYMENT_TYPE"})
    @AdminPresentation(friendlyName="PaymentInfoImpl_Payment_Type", order=2, group="Description", prominent=true, fieldType= SupportedFieldType.BROADLEAF_ENUMERATION, broadleafEnumeration="org.broadleafcommerce.core.payment.service.type.PaymentInfoType")
    protected String type;
    
    @OneToMany(mappedBy = "paymentInfo", targetEntity = AmountItemImpl.class, cascade = {CascadeType.ALL})
    protected List<AmountItem> amountItems = new ArrayList<AmountItem>();
    
    @Column(name = "CUSTOMER_IP_ADDRESS", nullable = true)
    @AdminPresentation(friendlyName="PaymentInfoImpl_Payment_IP_Address", order=4, group="Description")
    protected String customerIpAddress;
    
    @CollectionOfElements
    @JoinTable(name = "BLC_PAYINFO_ADDITIONAL_FIELDS", joinColumns = @JoinColumn(name = "PAYMENT_ID"))
    @MapKey(columns = { @Column(name = "FIELD_NAME", length = 150, nullable = false) })
    @Column(name = "FIELD_VALUE")
    protected Map<String, String> additionalFields = new HashMap<String, String>();

    public Money getAmount() {
        return amount == null ? null : new Money(amount);
    }

    public void setAmount(Money amount) {
        this.amount = Money.toAmount(amount);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Phone getPhone() {
        return phone;
    }

    public void setPhone(Phone phone) {
        this.phone = phone;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public PaymentInfoType getType() {
        return PaymentInfoType.getInstance(type);
    }

    public void setType(PaymentInfoType type) {
        this.type = type.getType();
    }

    public List<AmountItem> getAmountItems() {
		return amountItems;
	}

	public void setAmountItems(List<AmountItem> amountItems) {
		this.amountItems = amountItems;
	}

	public String getCustomerIpAddress() {
		return customerIpAddress;
	}

	public void setCustomerIpAddress(String customerIpAddress) {
		this.customerIpAddress = customerIpAddress;
	}

	public Map<String, String> getAdditionalFields() {
		return additionalFields;
	}

	public void setAdditionalFields(Map<String, String> additionalFields) {
		this.additionalFields = additionalFields;
	}

	public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PaymentInfoImpl other = (PaymentInfoImpl) obj;

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (referenceNumber == null) {
            if (other.referenceNumber != null)
                return false;
        } else if (!referenceNumber.equals(other.referenceNumber))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((referenceNumber == null) ? 0 : referenceNumber.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    public Referenced createEmptyReferenced() {
        if (getReferenceNumber() == null) {
            throw new RuntimeException("referenceNumber must be already set");
        }
        EmptyReferenced emptyReferenced = new EmptyReferenced();
        emptyReferenced.setReferenceNumber(getReferenceNumber());

        return emptyReferenced;
    }
}
