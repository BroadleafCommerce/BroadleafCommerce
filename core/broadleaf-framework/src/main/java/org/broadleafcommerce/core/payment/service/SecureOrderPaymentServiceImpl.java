/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.payment.service;

import org.broadleafcommerce.common.payment.PaymentType;
import org.broadleafcommerce.common.util.TransactionUtils;
import org.broadleafcommerce.core.payment.dao.SecureOrderPaymentDao;
import org.broadleafcommerce.core.payment.domain.secure.BankAccountPayment;
import org.broadleafcommerce.core.payment.domain.secure.CreditCardPayment;
import org.broadleafcommerce.core.payment.domain.secure.GiftCardPayment;
import org.broadleafcommerce.core.payment.domain.secure.Referenced;
import org.broadleafcommerce.core.workflow.WorkflowException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(TransactionUtils.SECURE_TRANSACTION_MANAGER)
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
    @Transactional(TransactionUtils.SECURE_TRANSACTION_MANAGER)
    public void findAndRemoveSecurePaymentInfo(String referenceNumber, PaymentType paymentType) throws WorkflowException {
        Referenced referenced = findSecurePaymentInfo(referenceNumber, paymentType);
        if (referenced != null) {
            remove(referenced);
        }

    }

    @Override
    @Transactional(TransactionUtils.SECURE_TRANSACTION_MANAGER)
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
