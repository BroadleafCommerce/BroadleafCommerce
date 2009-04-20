package org.broadleafcommerce.payment.order.module;

import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.payment.order.exception.PaymentException;
import org.broadleafcommerce.payment.secure.domain.BankAccountPaymentInfo;

public class DefaultBankAccountModule implements BankAccountModule {

    public static final String MODULENAME = "defaultBankAccountModule";

    protected String name = MODULENAME;

    /* (non-Javadoc)
     * @see org.broadleafcommerce.payment.order.module.BankAccountModule#authorize(org.broadleafcommerce.order.domain.PaymentInfo, org.broadleafcommerce.payment.secure.domain.BankAccountPaymentInfo)
     */
    @Override
    public void authorize(PaymentInfo paymentInfo, BankAccountPaymentInfo bankAccountPaymentInfo) throws PaymentException {
        //throw new PaymentException("authorize not implemented.");
        //do nothing
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.payment.order.module.BankAccountModule#authorizeAndDebit(org.broadleafcommerce.order.domain.PaymentInfo, org.broadleafcommerce.payment.secure.domain.BankAccountPaymentInfo)
     */
    @Override
    public void authorizeAndDebit(PaymentInfo paymentInfo, BankAccountPaymentInfo bankAccountPaymentInfo) throws PaymentException {
        //throw new PaymentException("authorizeAndDebit not implemented.");
        //do nothing
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.payment.order.module.BankAccountModule#debit(org.broadleafcommerce.order.domain.PaymentInfo, org.broadleafcommerce.payment.secure.domain.BankAccountPaymentInfo)
     */
    @Override
    public void debit(PaymentInfo paymentInfo, BankAccountPaymentInfo bankAccountPaymentInfo) throws PaymentException {
        //throw new PaymentException("debit not implemented.");
        //do nothing
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.payment.order.module.BankAccountModule#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.payment.order.module.BankAccountModule#setName(java.lang.String)
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

}
