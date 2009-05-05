package org.broadleafcommerce.order.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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

    @ManyToOne(targetEntity = AddressImpl.class)
    @JoinColumn(name = "ADDRESS_ID")
    private Address address;

    @ManyToOne(targetEntity = PhoneImpl.class)
    @JoinColumn(name = "PHONE_ID")
    private Phone phone;

    @Column(name = "AMOUNT")
    private BigDecimal amount;

    @Column(name = "PIN")
    private String pin;

    @Column(name = "REFERENCE_NUMBER")
    private String referenceNumber;

    @Column(name = "PAYMENT_TYPE")
    private String type;

    /*
     * TODO need to add new fields for amount and priority. This will allow the implementor
     * to define specific amounts to charge for each payment info, or specify the order in
     * which the payment infos are utilized.
     */

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
    
    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || !(other instanceof PaymentInfoImpl)) return false;

        PaymentInfoImpl item = (PaymentInfoImpl) other;

        if (referenceNumber != null ? !referenceNumber.equals(item.referenceNumber) : item.referenceNumber != null) return false;
        if (type != null ? !type.equals(item.type) : item.type != null) return false;
        if (order != null ? !order.equals(item.order) : item.order != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = referenceNumber != null ? referenceNumber.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (order != null ? order.hashCode() : 0);

        return result;
    }
}
