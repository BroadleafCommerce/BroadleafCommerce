package org.broadleafcommerce.payment.dao;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.broadleafcommerce.payment.domain.BankAccountPaymentInfo;
import org.broadleafcommerce.payment.domain.CreditCardPaymentInfo;
import org.broadleafcommerce.payment.domain.GiftCardPaymentInfo;
import org.broadleafcommerce.payment.domain.Referenced;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.stereotype.Repository;

@Repository("securePaymentInfoDao")
public class SecurePaymentInfoDaoJpa implements SecurePaymentInfoDao {

    @PersistenceContext(unitName = "blSecurePU")
    private EntityManager em;

    @Resource
    private EntityConfiguration entityConfiguration;

    public Referenced save(Referenced securePaymentInfo) {
        securePaymentInfo = em.merge(securePaymentInfo);
        return securePaymentInfo;
    }

    public BankAccountPaymentInfo createBankAccountPaymentInfo() {
        return (BankAccountPaymentInfo) entityConfiguration.createEntityInstance("org.broadleafcommerce.payment.domain.BankAccountPaymentInfo");
    }

    public GiftCardPaymentInfo createGiftCardPaymentInfo() {
        return (GiftCardPaymentInfo) entityConfiguration.createEntityInstance("org.broadleafcommerce.payment.domain.GiftCardPaymentInfo");
    }

    public CreditCardPaymentInfo createCreditCardPaymentInfo() {
        return (CreditCardPaymentInfo) entityConfiguration.createEntityInstance("org.broadleafcommerce.payment.domain.CreditCardPaymentInfo");
    }

    @SuppressWarnings("unchecked")
    @Override
    public BankAccountPaymentInfo findBankAccountInfo(String referenceNumber) {
        Query query = em.createNamedQuery("READ_BANK_ACCOUNT_BY_REFERENCE_NUMBER");
        query.setParameter("referenceNumber", referenceNumber);
        List<BankAccountPaymentInfo> infos = query.getResultList();
        return (infos==null || infos.size()==0)?null:infos.get(0);
    }

    @SuppressWarnings("unchecked")
    @Override
    public CreditCardPaymentInfo findCreditCardInfo(String referenceNumber) {
        Query query = em.createNamedQuery("READ_CREDIT_CARD_BY_REFERENCE_NUMBER");
        query.setParameter("referenceNumber", referenceNumber);
        List<CreditCardPaymentInfo> infos = query.getResultList();
        return (infos==null || infos.size()==0)?null:infos.get(0);
    }

    @SuppressWarnings("unchecked")
    @Override
    public GiftCardPaymentInfo findGiftCardInfo(String referenceNumber) {
        Query query = em.createNamedQuery("READ_GIFT_CARD_BY_REFERENCE_NUMBER");
        query.setParameter("referenceNumber", referenceNumber);
        List<GiftCardPaymentInfo> infos = query.getResultList();
        return (infos==null || infos.size()==0)?null:infos.get(0);
    }

}
