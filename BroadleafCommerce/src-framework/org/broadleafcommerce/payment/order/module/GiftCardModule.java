package org.broadleafcommerce.payment.order.module;

import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.payment.order.exception.PaymentException;

public interface GiftCardModule {

    public String getName();

    public void setName(String name);

    public void authorize(PaymentInfo paymentInfo) throws PaymentException;

    public void debit(PaymentInfo paymentInfo) throws PaymentException;

    public void authorizeAndDebit(PaymentInfo paymentInfo) throws PaymentException;

}
