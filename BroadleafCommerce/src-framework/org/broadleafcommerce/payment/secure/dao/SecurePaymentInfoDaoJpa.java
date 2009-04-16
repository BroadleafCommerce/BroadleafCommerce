package org.broadleafcommerce.payment.secure.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.broadleafcommerce.payment.secure.domain.BankAccountPaymentInfo;
import org.broadleafcommerce.payment.secure.domain.CreditCardPaymentInfo;
import org.springframework.stereotype.Repository;

@Repository("securePaymentInfoDao")
public class SecurePaymentInfoDaoJpa implements SecurePaymentInfoDao {

    /**
     * don't use persistence context annotation here - it's unlikely this entity manager will be
     * the same as that used by the rest of the framework
     */
    private EntityManager em;

    public SecurePaymentInfoDaoJpa(EntityManagerFactory emf) {
        this.em = emf.createEntityManager();
    }

    public SecurePaymentInfoDaoJpa() {
        throw new RuntimeException("This class must be instantiated using a valid EntityManager instance");
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.payment.secure.dao.SecurePaymentInfoDao#findBankAccountInfo(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    @Override
    public BankAccountPaymentInfo findBankAccountInfo(String referenceNumber) {
        Query query = em.createNamedQuery("READ_BANK_ACCOUNT_BY_REFERENCE_NUMBER");
        query.setParameter("referenceNumber", referenceNumber);
        List<BankAccountPaymentInfo> infos = query.getResultList();
        return (infos==null || infos.size()==0)?null:infos.get(0);
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.payment.secure.dao.SecurePaymentInfoDao#findCreditCardInfo(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    @Override
    public CreditCardPaymentInfo findCreditCardInfo(String referenceNumber) {
        Query query = em.createNamedQuery("READ_CREDIT_CARD_BY_REFERENCE_NUMBER");
        query.setParameter("referenceNumber", referenceNumber);
        List<CreditCardPaymentInfo> infos = query.getResultList();
        return (infos==null || infos.size()==0)?null:infos.get(0);
    }

}
