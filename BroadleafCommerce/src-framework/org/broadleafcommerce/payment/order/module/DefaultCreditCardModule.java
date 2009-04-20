package org.broadleafcommerce.payment.order.module;

import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.payment.order.exception.PaymentException;
import org.broadleafcommerce.payment.secure.domain.CreditCardPaymentInfo;

public class DefaultCreditCardModule implements CreditCardModule {

    public static final String MODULENAME = "defaultCreditCardModule";

    protected String name = MODULENAME;

    /* (non-Javadoc)
     * @see org.broadleafcommerce.payment.order.module.CreditCardModule#authorize(org.broadleafcommerce.order.domain.PaymentInfo, org.broadleafcommerce.payment.secure.domain.CreditCardPaymentInfo)
     */
    @Override
    public void authorize(PaymentInfo paymentInfo, CreditCardPaymentInfo creditCardPaymentInfo) throws PaymentException {
        //throw new PaymentException("authorize not implemented.");
        //do nothing
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.payment.order.module.CreditCardModule#authorizeAndDebit(org.broadleafcommerce.order.domain.PaymentInfo, org.broadleafcommerce.payment.secure.domain.CreditCardPaymentInfo)
     */
    @Override
    public void authorizeAndDebit(PaymentInfo paymentInfo, CreditCardPaymentInfo creditCardPaymentInfo) throws PaymentException {
        //throw new PaymentException("authorizeAndDebit not implemented.");
        //do nothing
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.payment.order.module.CreditCardModule#debit(org.broadleafcommerce.order.domain.PaymentInfo, org.broadleafcommerce.payment.secure.domain.CreditCardPaymentInfo)
     */
    @Override
    public void debit(PaymentInfo paymentInfo, CreditCardPaymentInfo creditCardPaymentInfo) throws PaymentException {
        //throw new PaymentException("debit not implemented.");
        //do nothing
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.payment.order.module.CreditCardModule#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.payment.order.module.CreditCardModule#setName(java.lang.String)
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

}
