package org.broadleafcommerce.payment.service;

import org.broadleafcommerce.payment.domain.BankAccountPaymentInfo;
import org.broadleafcommerce.payment.domain.CreditCardPaymentInfo;

public interface SecurePaymentInfoService {

    BankAccountPaymentInfo findBankAccountInfo(String referenceNumber);

    CreditCardPaymentInfo findCreditCardInfo(String referenceNumber);

}
