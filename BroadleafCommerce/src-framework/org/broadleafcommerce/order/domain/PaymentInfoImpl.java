package org.broadleafcommerce.order.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.domain.AddressImpl;
import org.broadleafcommerce.profile.domain.Phone;
import org.broadleafcommerce.profile.domain.PhoneImpl;
import org.broadleafcommerce.util.money.Money;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_ORDER_PAYMENT")
public class PaymentInfoImpl implements PaymentInfo, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "PaymentId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "PaymentId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "PaymentInfoImpl", allocationSize = 1)
    @Column(name = "PAYMENT_ID")
    private Long id;

    @ManyToOne(targetEntity = OrderImpl.class)
    @JoinColumn(name = "ORDER_ID")
    private Order order;

    @ManyToOne(targetEntity = AddressImpl.class, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "ADDRESS_ID")
    private Address address;

    @ManyToOne(targetEntity = PhoneImpl.class)
    @JoinColumn(name = "PHONE_ID")
    private Phone phone;

    @Column(name = "AMOUNT")
    private BigDecimal amount;

    @Column(name = "REFERENCE_NUMBER")
    private String referenceNumber;

    @Column(name = "PAYMENT_TYPE")
    private String type;

    @OneToMany(mappedBy = "paymentInfo", targetEntity = PaymentResponseItemImpl.class, cascade = {CascadeType.ALL})
    private List<PaymentResponseItem> paymentResponseItems = new ArrayList<PaymentResponseItem>();

    @Override
    public Money getAmount() {
        return amount == null ? null : new Money(amount);
    }

    @Override
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<PaymentResponseItem> getPaymentResponseItems() {
        return paymentResponseItems;
    }

    public void setPaymentResponseItems(List<PaymentResponseItem> paymentResponseItems) {
        this.paymentResponseItems = paymentResponseItems;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PaymentInfoImpl other = (PaymentInfoImpl) obj;
        if (order == null) {
            if (other.order != null)
                return false;
        } else if (!order.equals(other.order))
            return false;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((order == null) ? 0 : order.hashCode());
        result = prime * result + ((referenceNumber == null) ? 0 : referenceNumber.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }
}
