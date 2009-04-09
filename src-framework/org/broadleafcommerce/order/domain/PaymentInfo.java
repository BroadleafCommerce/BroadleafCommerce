package org.broadleafcommerce.order.domain;

import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.util.money.Money;

public interface PaymentInfo {

    public Long getId();

    public void setId(Long id);

    public Order getOrder();

    public void setOrder(Order order);

    public Address getAddress();

    public void setAddress(Address address);

    public Money getAmount();

    public void setAmount(Money amount);

    public String getReferenceNumber();

    public void setReferenceNumber(String referenceNumber);
}
