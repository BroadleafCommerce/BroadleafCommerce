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

package org.broadleafcommerce.core.payment.service.module;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.CreditCardValidator;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.payment.domain.CreditCardPaymentInfo;
import org.broadleafcommerce.core.payment.domain.PaymentResponseItem;
import org.broadleafcommerce.core.payment.service.PaymentContext;
import org.broadleafcommerce.core.payment.service.exception.PaymentException;
import org.broadleafcommerce.core.payment.service.type.PaymentInfoAdditionalFieldType;
import org.broadleafcommerce.core.payment.service.type.PaymentInfoType;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.Map;

/**
 * The following methods simulates a PaymentModule by overriding the process methods of the PaymentModule.
 *
 * This NullCreditCardModule will:
 *      validate a credit card number against the Apache Commons CreditCardValidator.
 *      validate the expiration date.
 *      validate that the cvv != "000" in order to demonstrate a PaymentException.
 *      set the TransactionSuccess to false on the PaymentResponseItem if any of the conditions above are invalid.
 *      creates a PaymentInfoDetail logging the transaction and puts it on the PaymentInfo
 *
 * This does NOT integrate with any Payment Gateway and should not be used in any production environment.
 * This class is for demonstration purposes only.
 *
 */
public class NullCreditCardPaymentModule extends AbstractModule {

    @Override
    public PaymentResponseItem processAuthorize(PaymentContext paymentContext, Money amountToAuthorize, PaymentResponseItem responseItem) throws PaymentException {
        Map<String, String> additionalFields = validateNullCreditCard(paymentContext);
        responseItem.setTransactionSuccess(additionalFields != null);
        if (responseItem.getTransactionSuccess()) {
            findPaymentInfoFromContext(paymentContext).setAdditionalFields(additionalFields);
        } else {
            throw new PaymentException("Problem processing Credit Card.");
        }
        return responseItem;
    }

    @Override
    public PaymentResponseItem processReverseAuthorize(PaymentContext paymentContext, Money amountToReverseAuthorize, PaymentResponseItem responseItem) throws PaymentException {
        Map<String, String> additionalFields = validateNullCreditCard(paymentContext);
        findPaymentInfoFromContext(paymentContext).setAdditionalFields(additionalFields);
        return responseItem;
    }

    @Override
    public PaymentResponseItem processAuthorizeAndDebit(PaymentContext paymentContext, Money amountToDebit, PaymentResponseItem responseItem) throws PaymentException {
        PaymentResponseItem authorizeResponseItem = authorize(paymentContext);
        if (authorizeResponseItem.getTransactionSuccess()) {
            return debit(paymentContext);
        } else {
            throw new PaymentException("Problem processing Credit Card.");
        }
    }

    @Override
    public PaymentResponseItem processDebit(PaymentContext paymentContext, Money amountToDebit, PaymentResponseItem responseItem) throws PaymentException {
        responseItem.setTransactionSuccess(true);
        Map<String, String> additionalFields = validateNullCreditCard(paymentContext);
        findPaymentInfoFromContext(paymentContext).setAdditionalFields(additionalFields);
        return responseItem;
    }

    @Override
    public PaymentResponseItem processCredit(PaymentContext paymentContext, Money amountToCredit, PaymentResponseItem responseItem) throws PaymentException {
        responseItem.setTransactionSuccess(true);
        Map<String, String> additionalFields = validateNullCreditCard(paymentContext);
        findPaymentInfoFromContext(paymentContext).setAdditionalFields(additionalFields);
        return responseItem;
    }

    @Override
    public PaymentResponseItem processVoidPayment(PaymentContext paymentContext, Money amountToVoid, PaymentResponseItem responseItem) throws PaymentException {
        if (amountToVoid.greaterThan(Money.ZERO)){
            return credit(paymentContext);
        } else {
            return reverseAuthorize(paymentContext);
        }
    }

    @Override
    public PaymentResponseItem processBalance(PaymentContext paymentContext, PaymentResponseItem responseItem) throws PaymentException {
        throw new PaymentException("balance not implemented.");
    }

    @Override
    public PaymentResponseItem processPartialPayment(PaymentContext paymentContext, Money amountToDebit, PaymentResponseItem responseItem) throws PaymentException {
        throw new PaymentException("partial payment not implemented.");
    }

    @Override
    public Boolean isValidCandidate(PaymentInfoType paymentType) {
        return PaymentInfoType.CREDIT_CARD.equals(paymentType);
    }

    protected String validateCardType(String ccNumber){
        CreditCardValidator visaValidator = new CreditCardValidator(CreditCardValidator.VISA);
        CreditCardValidator amexValidator = new CreditCardValidator(CreditCardValidator.AMEX);
        CreditCardValidator mcValidator = new CreditCardValidator(CreditCardValidator.MASTERCARD);
        CreditCardValidator discoverValidator = new CreditCardValidator(CreditCardValidator.DISCOVER);

        if (visaValidator.isValid(ccNumber)){
            return "VISA";
        } else if (amexValidator.isValid(ccNumber)) {
            return "AMEX";
        } else if (mcValidator.isValid(ccNumber)) {
            return "MASTERCARD";
        } else if (discoverValidator.isValid(ccNumber)) {
            return "DISCOVER";
        }
        return "UNKNOWN";
    }

    protected Map<String, String> validateNullCreditCard(PaymentContext paymentContext){
        CreditCardPaymentInfo ccInfo = (CreditCardPaymentInfo) paymentContext.getReferencedPaymentInfo();
        if ((ccInfo == null) || (ccInfo.getPan() == null)) {
            return null;
        }
        String nameOnCard = ccInfo.getNameOnCard();
        String ccNumber = ccInfo.getPan().replaceAll("[\\s-]+", "");
        Integer expMonth = ccInfo.getExpirationMonth();
        Integer expYear = ccInfo.getExpirationYear();
        String cvv = ccInfo.getCvvCode();

        String cardType = validateCardType(ccNumber);
        boolean validCard = !cardType.contains("UNKNOWN");

        DateTime expirationDate = new DateTime(expYear, expMonth, 1, 0, 0);
        boolean validDate = expirationDate.isAfterNow();

        boolean validCVV = !"000".equals(cvv);

        if (validDate && validCard && validCVV){
            Map<String, String> additionalFields = new HashMap<String, String>();
            additionalFields.put(PaymentInfoAdditionalFieldType.NAME_ON_CARD.getType(), nameOnCard);
            additionalFields.put(PaymentInfoAdditionalFieldType.CARD_TYPE.getType(), cardType);
            additionalFields.put(PaymentInfoAdditionalFieldType.EXP_MONTH.getType(), expMonth+"");
            additionalFields.put(PaymentInfoAdditionalFieldType.EXP_YEAR.getType(), expYear+"");
            additionalFields.put(PaymentInfoAdditionalFieldType.LAST_FOUR.getType(), StringUtils.right(ccNumber, 4));
            return additionalFields;
        } else {
            return null;
        }
    }
}
