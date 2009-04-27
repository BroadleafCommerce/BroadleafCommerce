package org.broadleafcommerce.payment.service;

import javax.annotation.Resource;

import org.broadleafcommerce.payment.dao.SecurePaymentInfoDao;
import org.broadleafcommerce.payment.domain.BankAccountPaymentInfo;
import org.broadleafcommerce.payment.domain.CreditCardPaymentInfo;
import org.springframework.stereotype.Service;

/**
 * Acquisition of Primary Account Number (PAN) and other sensitive information
 * is retrieved through a service separate from the order. This conceptual separation facilitates
 * the physical separation of this sensitive data from the order. As a result, implementors
 * may host sensitive user account information in a datastore separate from the datastore
 * housing the order. This measure goes towards achieving a PCI compliant architecture.
 * 
 * @author jfischer
 *
 */
@Service("securePaymentInfoService")
public class SecurePaymentInfoServiceImpl implements SecurePaymentInfoService {

    @Resource
    private SecurePaymentInfoDao securePaymentInfoDao;

    /* (non-Javadoc)
     * @see org.broadleafcommerce.payment.secure.service.SecurePaymentInfoService#findBankAccountInfo(java.lang.String)
     */
    @Override
    public BankAccountPaymentInfo findBankAccountInfo(String referenceNumber) {
        return securePaymentInfoDao.findBankAccountInfo(referenceNumber);
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.payment.secure.service.SecurePaymentInfoService#findCreditCardInfo(java.lang.String)
     */
    @Override
    public CreditCardPaymentInfo findCreditCardInfo(String referenceNumber) {
        return securePaymentInfoDao.findCreditCardInfo(referenceNumber);
    }

}
