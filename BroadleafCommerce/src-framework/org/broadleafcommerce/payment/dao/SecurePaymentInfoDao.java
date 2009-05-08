package org.broadleafcommerce.payment.dao;

import org.broadleafcommerce.payment.domain.BankAccountPaymentInfo;
import org.broadleafcommerce.payment.domain.CreditCardPaymentInfo;
import org.broadleafcommerce.payment.domain.GiftCardPaymentInfo;

public interface SecurePaymentInfoDao {

    BankAccountPaymentInfo findBankAccountInfo(String referenceNumber);

    CreditCardPaymentInfo findCreditCardInfo(String referenceNumber);

    GiftCardPaymentInfo findGiftCardInfo(String referenceNumber);
}
