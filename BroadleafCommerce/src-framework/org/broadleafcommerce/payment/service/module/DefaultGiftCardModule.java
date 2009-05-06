package org.broadleafcommerce.payment.service.module;

import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.payment.service.exception.PaymentException;
import org.broadleafcommerce.payment.service.exception.PaymentProcessorException;

public class DefaultGiftCardModule implements GiftCardModule {

    public static final String MODULENAME = "defaultGiftCardModule";

    protected String name = MODULENAME;

    @Override
    public GiftCardResponse authorize(PaymentInfo paymentInfo) throws PaymentException, PaymentProcessorException {
        throw new PaymentException("authorize not implemented.");
    }

    @Override
    public GiftCardResponse debit(PaymentInfo paymentInfo) throws PaymentException, PaymentProcessorException {
        throw new PaymentException("debit not implemented.");
    }

    @Override
    public GiftCardResponse authorizeAndDebit(PaymentInfo paymentInfo) throws PaymentException, PaymentProcessorException {
        throw new PaymentException("authorizeAndDebit not implemented.");
    }

    @Override
    public GiftCardResponse credit(PaymentInfo paymentInfo) throws PaymentException, PaymentProcessorException {
        throw new PaymentException("credit not implemented.");
    }

    @Override
    public GiftCardResponse voidPayment(PaymentInfo paymentInfo) throws PaymentException, PaymentProcessorException {
        throw new PaymentException("voidPayment not implemented.");
    }

    @Override
    public GiftCardResponse balance(PaymentInfo paymentInfo) throws PaymentException, PaymentProcessorException {
        throw new PaymentException("balance not implemented.");
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

}
