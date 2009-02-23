package org.broadleafcommerce.order.domain;

import java.math.BigDecimal;

import org.broadleafcommerce.profile.domain.Address;

public interface PaymentInfo {

    //	public Object getPayment();
    //
    //	public void setPayment(Object payment);

    public Long getId();

    public void setId(Long id);

    public Order getOrder();

    public void setOrder(Order order);

    public Address getAddress();

    public void setAddress(Address address);

    public BigDecimal getAmount();

    public void setAmount(BigDecimal amount);

    public String getReferenceNumber();

    public void setReferenceNumber(String referenceNumber);
}
