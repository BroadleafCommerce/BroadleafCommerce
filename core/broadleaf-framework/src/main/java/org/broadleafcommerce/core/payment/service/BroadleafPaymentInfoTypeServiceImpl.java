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

import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.payment.domain.BankAccountPaymentInfo;
import org.broadleafcommerce.core.payment.domain.CreditCardPaymentInfo;
import org.broadleafcommerce.core.payment.domain.EmptyReferenced;
import org.broadleafcommerce.core.payment.domain.GiftCardPaymentInfo;
import org.broadleafcommerce.core.payment.domain.PaymentInfo;
import org.broadleafcommerce.core.payment.domain.Referenced;
import org.broadleafcommerce.core.payment.service.type.PaymentInfoType;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jerry Ocanas (jocanas)
 */
@Service("blPaymentInfoTypeService")
public class BroadleafPaymentInfoTypeServiceImpl implements BroadleafPaymentInfoTypeService {

    /* Services */
    @Resource(name = "blSecurePaymentInfoService")
    protected SecurePaymentInfoService securePaymentInfoService;

    @Override
    public Map<PaymentInfo, Referenced> getPaymentsMap(Order order) {
        Map<PaymentInfo, Referenced> payments = new HashMap<PaymentInfo, Referenced>();

        for(PaymentInfo paymentInfo : order.getPaymentInfos()){
            if(PaymentInfoType.ACCOUNT.equals(paymentInfo.getType())){
                Referenced referenceInfo = createAccountReferenceInfo(paymentInfo);
                payments.put(paymentInfo, referenceInfo);
            }

            if(PaymentInfoType.BANK_ACCOUNT.equals(paymentInfo.getType())){
                Referenced referenceInfo = createBankAccountReferenceInfo(paymentInfo);
                payments.put(paymentInfo, referenceInfo);
            }

            if(PaymentInfoType.CHECK.equals(paymentInfo.getType())){
                Referenced referenceInfo = createCheckReferenceInfo(paymentInfo);
                payments.put(paymentInfo, referenceInfo);
            }

            if(PaymentInfoType.CREDIT_CARD.equals(paymentInfo.getType())){
                Referenced referenceInfo = createCreditCardReferenceInfo(paymentInfo);
                payments.put(paymentInfo, referenceInfo);
            }

            if(PaymentInfoType.CUSTOMER_CREDIT.equals(paymentInfo.getType())){
                Referenced referenceInfo = createCustomerCreditReferenceInfo(paymentInfo);
                payments.put(paymentInfo, referenceInfo);
            }

            if(PaymentInfoType.ELECTRONIC_CHECK.equals(paymentInfo.getType())){
                Referenced referenceInfo = createElectronicCheckReferenceInfo(paymentInfo);
                payments.put(paymentInfo, referenceInfo);
            }

            if(PaymentInfoType.GIFT_CARD.equals(paymentInfo.getType())){
                Referenced referenceInfo = createGiftCardReferenceInfo(paymentInfo);
                payments.put(paymentInfo, referenceInfo);
            }

            if(PaymentInfoType.MONEY_ORDER.equals(paymentInfo.getType())){
                Referenced referenceInfo = createMoneyOrderReferenceInfo(paymentInfo);
                payments.put(paymentInfo, referenceInfo);
            }

            if(PaymentInfoType.PAYPAL.equals(paymentInfo.getType())){
                Referenced referenceInfo = createPayPalReferenceInfo(paymentInfo);
                payments.put(paymentInfo, referenceInfo);
            }

            if(PaymentInfoType.WIRE.equals(paymentInfo.getType())){
                Referenced referenceInfo = createWireReferenceInfo(paymentInfo);
                payments.put(paymentInfo, referenceInfo);
            }
        }

        return payments;
    }

    public Referenced createAccountReferenceInfo(PaymentInfo paymentInfo){
        Referenced blankReference = new EmptyReferenced();
        blankReference.setReferenceNumber(paymentInfo.getReferenceNumber());
        return blankReference;
    }

    public Referenced createBankAccountReferenceInfo(PaymentInfo paymentInfo){
        BankAccountPaymentInfo blankReference = (BankAccountPaymentInfo) securePaymentInfoService.create(PaymentInfoType.BANK_ACCOUNT);
        blankReference.setReferenceNumber(paymentInfo.getReferenceNumber());
        return blankReference;
    }

    public Referenced createCheckReferenceInfo(PaymentInfo paymentInfo){
        Referenced blankReference = new EmptyReferenced();
        blankReference.setReferenceNumber(paymentInfo.getReferenceNumber());
        return blankReference;
    }

    public Referenced createCreditCardReferenceInfo(PaymentInfo paymentInfo){
        CreditCardPaymentInfo blankReference = (CreditCardPaymentInfo) securePaymentInfoService.create(PaymentInfoType.CREDIT_CARD);
        blankReference.setReferenceNumber(paymentInfo.getReferenceNumber()); 
        return blankReference;
    }

    public Referenced createCustomerCreditReferenceInfo(PaymentInfo paymentInfo){
        Referenced blankReference = new EmptyReferenced();
        blankReference.setReferenceNumber(paymentInfo.getReferenceNumber());
        return blankReference;
    }

    public Referenced createElectronicCheckReferenceInfo(PaymentInfo paymentInfo){
        Referenced blankReference = new EmptyReferenced();
        blankReference.setReferenceNumber(paymentInfo.getReferenceNumber());
        return blankReference;
    }

    public Referenced createGiftCardReferenceInfo(PaymentInfo paymentInfo){
        GiftCardPaymentInfo blankReference = (GiftCardPaymentInfo) securePaymentInfoService.create(PaymentInfoType.GIFT_CARD);
        blankReference.setReferenceNumber(paymentInfo.getReferenceNumber());
        return blankReference;
    }

    public Referenced createMoneyOrderReferenceInfo(PaymentInfo paymentInfo){
        Referenced blankReference = new EmptyReferenced();
        blankReference.setReferenceNumber(paymentInfo.getReferenceNumber());
        return blankReference;
    }

    public Referenced createPayPalReferenceInfo(PaymentInfo paymentInfo){
        Referenced blankReference = new EmptyReferenced();
        blankReference.setReferenceNumber(paymentInfo.getReferenceNumber());
        return blankReference;
    }

    public Referenced createWireReferenceInfo(PaymentInfo paymentInfo){
        Referenced blankReference = new EmptyReferenced();
        blankReference.setReferenceNumber(paymentInfo.getReferenceNumber());
        return blankReference;
    }
}
