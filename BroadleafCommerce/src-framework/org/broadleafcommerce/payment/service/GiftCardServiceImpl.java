package org.broadleafcommerce.payment.service;

import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.payment.service.exception.PaymentException;
import org.broadleafcommerce.payment.service.exception.PaymentProcessorException;
import org.broadleafcommerce.payment.service.module.GiftCardModule;
import org.broadleafcommerce.payment.service.module.GiftCardResponse;

public class GiftCardServiceImpl implements GiftCardService {

    protected GiftCardModule giftCardModule;

    public GiftCardResponse authorize(PaymentInfo paymentInfo) throws PaymentException, PaymentProcessorException {
        return giftCardModule.authorize(paymentInfo);
    }

    public GiftCardResponse authorizeAndDebit(PaymentInfo paymentInfo) throws PaymentException, PaymentProcessorException {
        return giftCardModule.authorizeAndDebit(paymentInfo);
    }

    public GiftCardResponse balance(PaymentInfo paymentInfo) throws PaymentException, PaymentProcessorException {
        return giftCardModule.balance(paymentInfo);
    }

    public GiftCardResponse credit(PaymentInfo paymentInfo) throws PaymentException, PaymentProcessorException {
        return giftCardModule.credit(paymentInfo);
    }

    public GiftCardResponse debit(PaymentInfo paymentInfo) throws PaymentException, PaymentProcessorException {
        return giftCardModule.debit(paymentInfo);
    }

    public GiftCardResponse voidPayment(PaymentInfo paymentInfo) throws PaymentException, PaymentProcessorException {
        return giftCardModule.voidPayment(paymentInfo);
    }

    public GiftCardModule getGiftCardModule() {
        return giftCardModule;
    }

    public void setGiftCardModule(GiftCardModule giftCardModule) {
        this.giftCardModule = giftCardModule;
    }
}
