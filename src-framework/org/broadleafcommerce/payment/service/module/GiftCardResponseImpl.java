package org.broadleafcommerce.payment.service.module;

import org.broadleafcommerce.util.money.Money;

public class GiftCardResponseImpl extends PaymentResponseImpl implements GiftCardResponse {

    protected Money remainingBalance;

    public Money getRemainingBalance() {
        return remainingBalance;
    }

    public void setRemainingBalance(Money remainingBalance) {
        this.remainingBalance = remainingBalance;
    }

}
