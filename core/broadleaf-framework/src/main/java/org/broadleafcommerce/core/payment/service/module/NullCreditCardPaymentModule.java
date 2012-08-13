/*
 * Copyright 2008-2009 the original author or authors.
 *
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
 */

package org.broadleafcommerce.core.payment.service.module;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.CreditCardValidator;
import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.core.payment.domain.CreditCardPaymentInfo;
import org.broadleafcommerce.core.payment.domain.PaymentInfo;
import org.broadleafcommerce.core.payment.domain.PaymentResponseItem;
import org.broadleafcommerce.core.payment.domain.PaymentResponseItemImpl;
import org.broadleafcommerce.core.payment.service.PaymentContext;
import org.broadleafcommerce.core.payment.service.exception.PaymentException;
import org.broadleafcommerce.core.payment.service.type.PaymentInfoAdditionalFieldType;
import org.broadleafcommerce.core.payment.service.type.PaymentInfoType;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.Map;

public class NullCreditCardPaymentModule extends AbstractModule {

    public PaymentResponseItem authorize(PaymentContext paymentContext) throws PaymentException {
        throw new PaymentException("authorize not implemented.");
    }

    public PaymentResponseItem reverseAuthorize(PaymentContext paymentContext) throws PaymentException {
        throw new PaymentException("reverse authorize not implemented.");
    }

    /**
     * The following method:
     * validates a credit card number against the Apache Commons CreditCardValidator.
     * validates the expiration date.
     * validates that the cvv != "000" in order to demonstrate a PaymentException
     *
     * This method will set the TransactionSuccess to false on the PaymentResponseItem
     * if any of the conditions above are invalid.
     *
     * This does NOT integrate with any Payment Gateway and should not be used in any production environment.
     * This class is for demonstration purposes only.
     *
     * @param paymentContext - the payment context injected from the workflow (see: blAuthorizeAndDebitWorkflow in bl-framework-applicationContext-workflow.xml)
     * @return PaymentResponseItem - the response item
     */
    public PaymentResponseItem authorizeAndDebit(PaymentContext paymentContext) throws PaymentException {
        //Note that you cannot perform operations on paymentContext.getPaymentInfo() directly because that is a copy of the actual payment on the order.
        //In order to persist custom attributes to the credit card payment info on the order we must look it up first.
        PaymentInfo paymentInfo = null;
        for (PaymentInfo pi : paymentContext.getPaymentInfo().getOrder().getPaymentInfos()) {
            if (PaymentInfoType.CREDIT_CARD.equals(pi.getType())) {
                paymentInfo = pi;
            }
        }

        if (paymentInfo == null) {
            throw new PaymentException("PaymentInfo of type CREDIT_CARD must be on the order");
        }

        CreditCardPaymentInfo ccInfo = (CreditCardPaymentInfo) paymentContext.getReferencedPaymentInfo();
        String nameOnCard = ccInfo.getNameOnCard();
        String ccNumber = ccInfo.getPan().replaceAll("[\\s-]+", "");
        Integer expMonth = ccInfo.getExpirationMonth();
        Integer expYear = ccInfo.getExpirationYear();
        String cvv = ccInfo.getCvvCode();

        CreditCardValidator visaValidator = new CreditCardValidator(CreditCardValidator.VISA);
        CreditCardValidator amexValidator = new CreditCardValidator(CreditCardValidator.AMEX);
        CreditCardValidator mcValidator = new CreditCardValidator(CreditCardValidator.MASTERCARD);
        CreditCardValidator discoverValidator = new CreditCardValidator(CreditCardValidator.DISCOVER);

        boolean validCard = false;
        String cardType = "UNKNOWN";
        if (visaValidator.isValid(ccNumber)){
            validCard = true;
            cardType = "VISA";
        } else if (amexValidator.isValid(ccNumber)) {
            validCard = true;
            cardType = "AMEX";
        } else if (mcValidator.isValid(ccNumber)) {
            validCard = true;
            cardType = "MASTERCARD";
        } else if (discoverValidator.isValid(ccNumber)) {
            validCard = true;
            cardType = "DISCOVER";
        }

        DateTime expirationDate = new DateTime(expYear, expMonth, 1, 0, 0);
        boolean validDate = expirationDate.isAfterNow();

        boolean validCVV = !cvv.equals("000");

        PaymentResponseItem responseItem = new PaymentResponseItemImpl();
        responseItem.setTransactionTimestamp(SystemTime.asDate());
        responseItem.setTransactionSuccess(validDate && validCard && validCVV);
        responseItem.setAmountPaid(paymentInfo.getAmount());
        if (responseItem.getTransactionSuccess()) {
            Map<String, String> additionalFields = new HashMap<String, String>();
            additionalFields.put(PaymentInfoAdditionalFieldType.NAME_ON_CARD.getType(), nameOnCard);
            additionalFields.put(PaymentInfoAdditionalFieldType.CARD_TYPE.getType(), cardType);
            additionalFields.put(PaymentInfoAdditionalFieldType.EXP_MONTH.getType(), expMonth+"");
            additionalFields.put(PaymentInfoAdditionalFieldType.EXP_YEAR.getType(), expYear+"");
            additionalFields.put(PaymentInfoAdditionalFieldType.LAST_FOUR.getType(), StringUtils.right(ccNumber, 4));
            paymentInfo.setAdditionalFields(additionalFields);
        }

        return responseItem;
    }

    public PaymentResponseItem debit(PaymentContext paymentContext) throws PaymentException {
        throw new PaymentException("debit not implemented.");
    }

    public PaymentResponseItem credit(PaymentContext paymentContext) throws PaymentException {
        throw new PaymentException("credit not implemented.");
    }

    public PaymentResponseItem voidPayment(PaymentContext paymentContext) throws PaymentException {
        throw new PaymentException("voidPayment not implemented.");
    }

    public PaymentResponseItem balance(PaymentContext paymentContext) throws PaymentException {
        throw new PaymentException("balance not implemented.");
    }

    public Boolean isValidCandidate(PaymentInfoType paymentType) {
        return PaymentInfoType.CREDIT_CARD.equals(paymentType);
    }
}
