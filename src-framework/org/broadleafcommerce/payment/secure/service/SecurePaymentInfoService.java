package org.broadleafcommerce.payment.secure.service;

import org.broadleafcommerce.payment.secure.domain.BankAccountPaymentInfo;
import org.broadleafcommerce.payment.secure.domain.CreditCardPaymentInfo;

public interface SecurePaymentInfoService {

    BankAccountPaymentInfo findBankAccountInfo(String referenceNumber);

    CreditCardPaymentInfo findCreditCardInfo(String referenceNumber);

}
