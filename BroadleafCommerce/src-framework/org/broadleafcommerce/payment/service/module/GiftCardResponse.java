package org.broadleafcommerce.payment.service.module;

import org.broadleafcommerce.util.money.Money;

public interface GiftCardResponse extends PaymentResponse {

    public Money getRemainingBalance();

    public void setRemainingBalance(Money remainingBalance);

}
