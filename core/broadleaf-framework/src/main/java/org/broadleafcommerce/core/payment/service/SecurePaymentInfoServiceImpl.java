/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.payment.service;

import org.broadleafcommerce.core.payment.dao.SecurePaymentInfoDao;
import org.broadleafcommerce.core.payment.domain.BankAccountPaymentInfo;
import org.broadleafcommerce.core.payment.domain.CreditCardPaymentInfo;
import org.broadleafcommerce.core.payment.domain.GiftCardPaymentInfo;
import org.broadleafcommerce.core.payment.domain.Referenced;
import org.broadleafcommerce.core.payment.service.type.PaymentInfoType;
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
@Service("blSecurePaymentInfoService")
public class SecurePaymentInfoServiceImpl implements SecurePaymentInfoService {

    @Resource(name = "blSecurePaymentInfoDao")
    protected SecurePaymentInfoDao securePaymentInfoDao;

    public Referenced save(Referenced securePaymentInfo) {
        return securePaymentInfoDao.save(securePaymentInfo);
    }

    public Referenced create(PaymentInfoType paymentInfoType) {
        if (paymentInfoType.equals(PaymentInfoType.CREDIT_CARD)) {
            CreditCardPaymentInfo ccinfo = securePaymentInfoDao.createCreditCardPaymentInfo();
            return ccinfo;
        } else if (paymentInfoType.equals(PaymentInfoType.BANK_ACCOUNT)) {
            BankAccountPaymentInfo bankinfo = securePaymentInfoDao.createBankAccountPaymentInfo();
            return bankinfo;
        } else if (paymentInfoType.equals(PaymentInfoType.GIFT_CARD)) {
            GiftCardPaymentInfo gcinfo = securePaymentInfoDao.createGiftCardPaymentInfo();
            return gcinfo;
        }

        return null;
    }

    public Referenced findSecurePaymentInfo(String referenceNumber, PaymentInfoType paymentInfoType) throws WorkflowException {
        if (paymentInfoType == PaymentInfoType.CREDIT_CARD) {
            CreditCardPaymentInfo ccinfo = findCreditCardInfo(referenceNumber);
            if (ccinfo == null) {
                throw new WorkflowException("No credit card info associated with credit card payment type with reference number: " + referenceNumber);
            }
            return ccinfo;
        } else if (paymentInfoType == PaymentInfoType.BANK_ACCOUNT) {
            BankAccountPaymentInfo bankinfo = findBankAccountInfo(referenceNumber);
            if (bankinfo == null) {
                throw new WorkflowException("No bank account info associated with bank account payment type with reference number: " + referenceNumber);
            }
            return bankinfo;
        } else if (paymentInfoType == PaymentInfoType.GIFT_CARD) {
            GiftCardPaymentInfo gcinfo = findGiftCardInfo(referenceNumber);
            if (gcinfo == null) {
                throw new WorkflowException("No bank account info associated with gift card payment type with reference number: " + referenceNumber);
            }
            return gcinfo;
        }

        return null;
    }

    public void findAndRemoveSecurePaymentInfo(String referenceNumber, PaymentInfoType paymentInfoType) throws WorkflowException {
        Referenced referenced = findSecurePaymentInfo(referenceNumber, paymentInfoType);
        if (referenced != null) {
            remove(referenced);
        }

    }

    public void remove(Referenced securePaymentInfo) {
        securePaymentInfoDao.delete(securePaymentInfo);
    }

    protected BankAccountPaymentInfo findBankAccountInfo(String referenceNumber) {
        return securePaymentInfoDao.findBankAccountInfo(referenceNumber);
    }

    protected CreditCardPaymentInfo findCreditCardInfo(String referenceNumber) {
        return securePaymentInfoDao.findCreditCardInfo(referenceNumber);
    }

    protected GiftCardPaymentInfo findGiftCardInfo(String referenceNumber) {
        return securePaymentInfoDao.findGiftCardInfo(referenceNumber);
    }
}
