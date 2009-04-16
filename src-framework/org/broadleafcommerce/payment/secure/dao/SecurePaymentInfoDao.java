package org.broadleafcommerce.payment.secure.dao;

import org.broadleafcommerce.payment.secure.domain.BankAccountPaymentInfo;
import org.broadleafcommerce.payment.secure.domain.CreditCardPaymentInfo;

public interface SecurePaymentInfoDao {

    BankAccountPaymentInfo findBankAccountInfo(String referenceNumber);

    CreditCardPaymentInfo findCreditCardInfo(String referenceNumber);

}
