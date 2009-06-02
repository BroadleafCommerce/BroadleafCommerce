/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

@Repository("blSecurePaymentInfoDao")
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

    @Override
    public void delete(Referenced securePaymentInfo) {
        em.remove(securePaymentInfo);
    }
}
