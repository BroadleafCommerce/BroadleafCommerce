/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.core.payment.dao;

import org.broadleafcommerce.common.encryption.EncryptionModule;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.core.payment.domain.secure.BankAccountPayment;
import org.broadleafcommerce.core.payment.domain.secure.CreditCardPayment;
import org.broadleafcommerce.core.payment.domain.secure.GiftCardPayment;
import org.broadleafcommerce.core.payment.domain.secure.Referenced;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository("blSecureOrderPaymentDao")
public class SecureOrderPaymentDaoImpl implements SecureOrderPaymentDao {

    @PersistenceContext(unitName = "blSecurePU")
    protected EntityManager em;

    @Resource(name = "blEncryptionModule")
    protected EncryptionModule encryptionModule;

    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    public Referenced save(Referenced securePaymentInfo) {
        return em.merge(securePaymentInfo);
    }

    public BankAccountPayment createBankAccountPayment() {
        BankAccountPayment response = entityConfiguration.createEntityInstance(BankAccountPayment.class.getName(), BankAccountPayment.class);
        response.setEncryptionModule(encryptionModule);
        return response;
    }

    public GiftCardPayment createGiftCardPayment() {
        GiftCardPayment response = entityConfiguration.createEntityInstance(GiftCardPayment.class.getName(), GiftCardPayment.class);
        response.setEncryptionModule(encryptionModule);
        return response;
    }

    public CreditCardPayment createCreditCardPayment() {
        CreditCardPayment response = entityConfiguration.createEntityInstance(CreditCardPayment.class.getName(), CreditCardPayment.class);
        response.setEncryptionModule(encryptionModule);
        return response;
    }

    @SuppressWarnings("unchecked")
    public BankAccountPayment findBankAccountPayment(String referenceNumber) {
        Query query = em.createNamedQuery("BC_READ_BANK_ACCOUNT_BY_REFERENCE_NUMBER");
        query.setParameter("referenceNumber", referenceNumber);
        List<BankAccountPayment> infos = query.getResultList();
        BankAccountPayment response = (infos == null || infos.size() == 0) ? null : infos.get(0);
        if (response != null) {
            response.setEncryptionModule(encryptionModule);
        }
        return response;
    }

    @SuppressWarnings("unchecked")
    public CreditCardPayment findCreditCardPayment(String referenceNumber) {
        Query query = em.createNamedQuery("BC_READ_CREDIT_CARD_BY_REFERENCE_NUMBER");
        query.setParameter("referenceNumber", referenceNumber);
        List<CreditCardPayment> infos = query.getResultList();
        CreditCardPayment response = (infos == null || infos.size() == 0) ? null : infos.get(0);
        if (response != null) {
            response.setEncryptionModule(encryptionModule);
        }
        return response;
    }

    @SuppressWarnings("unchecked")
    public GiftCardPayment findGiftCardPayment(String referenceNumber) {
        Query query = em.createNamedQuery("BC_READ_GIFT_CARD_BY_REFERENCE_NUMBER");
        query.setParameter("referenceNumber", referenceNumber);
        List<GiftCardPayment> infos = query.getResultList();
        GiftCardPayment response = (infos == null || infos.size() == 0) ? null : infos.get(0);
        if (response != null) {
            response.setEncryptionModule(encryptionModule);
        }
        return response;
    }

    public void delete(Referenced securePayment) {
        if (!em.contains(securePayment)) {
            securePayment = em.find(securePayment.getClass(), securePayment.getId());
        }
        em.remove(securePayment);
    }

}
