package org.broadleafcommerce.payment.secure.dao;

import javax.persistence.EntityManager;

import org.broadleafcommerce.payment.secure.domain.BankAccountPaymentInfo;
import org.broadleafcommerce.payment.secure.domain.CreditCardPaymentInfo;
import org.broadleafcommerce.profile.util.EntityConfiguration;

//@Repository("securePaymentInfoDao")
public class SecurePaymentInfoDaoJpa implements SecurePaymentInfoDao {

    /**
     * don't user persistence context annotation here - it's unlikely this entity manager will be
     * the same as that used by the rest of the framework
     */
    private EntityManager em;

    //@Resource
    private EntityConfiguration entityConfiguration;

    public SecurePaymentInfoDaoJpa(EntityManager em) {
        this.em = em;
    }

    public SecurePaymentInfoDaoJpa() {
        throw new RuntimeException("This class must be instantiated using a valid EntityManager instance");
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.payment.secure.dao.SecurePaymentInfoDao#findBankAccountInfo(java.lang.String)
     */
    @Override
    public BankAccountPaymentInfo findBankAccountInfo(String referenceNumber) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.payment.secure.dao.SecurePaymentInfoDao#findCreditCardInfo(java.lang.String)
     */
    @Override
    public CreditCardPaymentInfo findCreditCardInfo(String referenceNumber) {
        // TODO Auto-generated method stub
        return null;
    }

}
