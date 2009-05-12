package org.broadleafcommerce.payment.dao;

import org.broadleafcommerce.payment.domain.BankAccountPaymentInfo;
import org.broadleafcommerce.payment.domain.CreditCardPaymentInfo;
import org.broadleafcommerce.payment.domain.GiftCardPaymentInfo;
import org.broadleafcommerce.payment.domain.Referenced;

public interface SecurePaymentInfoDao {

    public BankAccountPaymentInfo findBankAccountInfo(String referenceNumber);

    public CreditCardPaymentInfo findCreditCardInfo(String referenceNumber);

    public GiftCardPaymentInfo findGiftCardInfo(String referenceNumber);

    public Referenced save(Referenced securePaymentInfo);

    public BankAccountPaymentInfo createBankAccountPaymentInfo();

    public GiftCardPaymentInfo createGiftCardPaymentInfo();

    public CreditCardPaymentInfo createCreditCardPaymentInfo();

}
