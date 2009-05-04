package org.broadleafcommerce.order.domain;

import org.broadleafcommerce.payment.service.type.PaymentInfoType;
import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.domain.Phone;
import org.broadleafcommerce.util.money.Money;

public interface PaymentInfo {

    public Long getId();

    public void setId(Long id);

    public Order getOrder();

    public void setOrder(Order order);

    public Address getAddress();

    public void setAddress(Address address);

    public Phone getPhone();

    public void setPhone(Phone phone);

    public Money getAmount();

    public void setAmount(Money amount);

    public String getPin();

    public void setPin(String pin);

    public String getReferenceNumber();

    public void setReferenceNumber(String referenceNumber);

    public PaymentInfoType getType();

    public void setType(PaymentInfoType type);
}
