package org.broadleafcommerce.payment.order.module;

import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.payment.order.exception.PaymentException;

public class DefaultGiftCardModule implements GiftCardModule {

    public static final String MODULENAME = "defaultGiftCardModule";

    protected String name = MODULENAME;

    /* (non-Javadoc)
     * @see org.broadleafcommerce.payment.order.module.GiftCardModule#authorize(org.broadleafcommerce.order.domain.PaymentInfo)
     */
    @Override
    public void authorize(PaymentInfo paymentInfo) throws PaymentException {
        throw new PaymentException("authorize not implemented.");
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.payment.order.module.GiftCardModule#authorizeAndDebit(org.broadleafcommerce.order.domain.PaymentInfo)
     */
    @Override
    public void authorizeAndDebit(PaymentInfo paymentInfo) throws PaymentException {
        throw new PaymentException("authorizeAndDebit not implemented.");
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.payment.order.module.GiftCardModule#debit(org.broadleafcommerce.order.domain.PaymentInfo)
     */
    @Override
    public void debit(PaymentInfo paymentInfo) throws PaymentException {
        throw new PaymentException("debit not implemented.");
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.payment.order.module.GiftCardModule#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.payment.order.module.GiftCardModule#setName(java.lang.String)
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

}
