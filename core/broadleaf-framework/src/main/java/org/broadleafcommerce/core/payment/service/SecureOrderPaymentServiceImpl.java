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
package org.broadleafcommerce.core.payment.service;

import org.broadleafcommerce.core.payment.dao.SecureOrderPaymentDao;
import org.broadleafcommerce.core.payment.domain.secure.BankAccountPayment;
import org.broadleafcommerce.core.payment.domain.secure.CreditCardPayment;
import org.broadleafcommerce.core.payment.domain.secure.GiftCardPayment;
import org.broadleafcommerce.core.payment.domain.secure.Referenced;
import org.broadleafcommerce.core.payment.service.type.PaymentType;
import org.broadleafcommerce.core.workflow.WorkflowException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Acquisition of Primary Account Number (PAN) and other sensitive information
 * is retrieved through a service separate from the order. This conceptual
 * separation facilitates the physical separation of this sensitive data from
 * the order. As a result, implementors may host sensitive user account
 * information in a datastore separate from the datastore housing the order.
 * This measure goes towards achieving a PCI compliant architecture.
 * @author jfischer
 */
@Service("blSecureOrderPaymentService")
public class SecureOrderPaymentServiceImpl implements SecureOrderPaymentService {

    @Resource(name = "blSecureOrderPaymentDao")
    protected SecureOrderPaymentDao securePaymentInfoDao;

    @Override
    public Referenced save(Referenced securePaymentInfo) {
        return securePaymentInfoDao.save(securePaymentInfo);
    }

    @Override
    public Referenced create(PaymentType paymentType) {
        if (paymentType.equals(PaymentType.CREDIT_CARD)) {
            CreditCardPayment ccinfo = securePaymentInfoDao.createCreditCardPayment();
            return ccinfo;
        } else if (paymentType.equals(PaymentType.BANK_ACCOUNT)) {
            BankAccountPayment bankinfo = securePaymentInfoDao.createBankAccountPayment();
            return bankinfo;
        } else if (paymentType.equals(PaymentType.GIFT_CARD)) {
            GiftCardPayment gcinfo = securePaymentInfoDao.createGiftCardPayment();
            return gcinfo;
        }

        return null;
    }

    @Override
    public Referenced findSecurePaymentInfo(String referenceNumber, PaymentType paymentType) throws WorkflowException {
        if (paymentType == PaymentType.CREDIT_CARD) {
            CreditCardPayment ccinfo = findCreditCardInfo(referenceNumber);
            if (ccinfo == null) {
                throw new WorkflowException("No credit card info associated with credit card payment type with reference number: " + referenceNumber);
            }
            return ccinfo;
        } else if (paymentType == PaymentType.BANK_ACCOUNT) {
            BankAccountPayment bankinfo = findBankAccountInfo(referenceNumber);
            if (bankinfo == null) {
                throw new WorkflowException("No bank account info associated with bank account payment type with reference number: " + referenceNumber);
            }
            return bankinfo;
        } else if (paymentType == PaymentType.GIFT_CARD) {
            GiftCardPayment gcinfo = findGiftCardInfo(referenceNumber);
            if (gcinfo == null) {
                throw new WorkflowException("No bank account info associated with gift card payment type with reference number: " + referenceNumber);
            }
            return gcinfo;
        }

        return null;
    }

    @Override
    public void findAndRemoveSecurePaymentInfo(String referenceNumber, PaymentType paymentInfoType) throws WorkflowException {
        Referenced referenced = findSecurePaymentInfo(referenceNumber, paymentInfoType);
        if (referenced != null) {
            remove(referenced);
        }

    }

    @Override
    public void remove(Referenced securePaymentInfo) {
        securePaymentInfoDao.delete(securePaymentInfo);
    }

    protected BankAccountPayment findBankAccountInfo(String referenceNumber) {
        return securePaymentInfoDao.findBankAccountPayment(referenceNumber);
    }

    protected CreditCardPayment findCreditCardInfo(String referenceNumber) {
        return securePaymentInfoDao.findCreditCardPayment(referenceNumber);
    }

    protected GiftCardPayment findGiftCardInfo(String referenceNumber) {
        return securePaymentInfoDao.findGiftCardPayment(referenceNumber);
    }
}
