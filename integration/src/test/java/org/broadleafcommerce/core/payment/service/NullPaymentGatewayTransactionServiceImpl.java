/*
 * #%L
 * BroadleafCommerce Integration
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.CreditCardValidator;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.payment.PaymentAdditionalFieldType;
import org.broadleafcommerce.common.payment.PaymentDeclineType;
import org.broadleafcommerce.common.payment.PaymentTransactionType;
import org.broadleafcommerce.common.payment.PaymentType;
import org.broadleafcommerce.common.payment.dto.CreditCardDTO;
import org.broadleafcommerce.common.payment.dto.PaymentRequestDTO;
import org.broadleafcommerce.common.payment.dto.PaymentResponseDTO;
import org.broadleafcommerce.common.payment.service.PaymentGatewayTransactionService;
import org.broadleafcommerce.common.vendor.service.exception.PaymentException;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

/**
 * This is an example implementation of a {@link org.broadleafcommerce.common.payment.service.PaymentGatewayTransactionService}.
 * This handles the scenario where the implementation is PCI-Compliant and
 * the server directly handles the Credit Card PAN. If so, this service should make
 * a server to server call to charge the card against the configured gateway.
 *
 * In order to use load this demo service, you will need to component scan
 * the package "com.mycompany.sample".
 *
 * This should NOT be used in production, and is meant solely for demonstration
 * purposes only.
 *
 * @author Elbert Bautista (elbertbautista)
 */
@Service("blNullPaymentGatewayTransactionService")
public class NullPaymentGatewayTransactionServiceImpl implements PaymentGatewayTransactionService {

    private static Money oneHundred = new Money(100);
    private static Money twoHundred = new Money(200);

    @Override
    public PaymentResponseDTO authorize(PaymentRequestDTO paymentRequestDTO) throws PaymentException {
        return commonCreditCardProcessing(paymentRequestDTO, PaymentTransactionType.AUTHORIZE);
    }

    /**
     * for the test implementation, and in order to test different failed response scenarios, check for the presence of a "desired outcome"
     * entry in the request's additional fields
     */
    @Override
    public PaymentResponseDTO capture(PaymentRequestDTO paymentRequestDTO) throws PaymentException {

        PaymentResponseDTO responseDTO = new PaymentResponseDTO(PaymentType.THIRD_PARTY_ACCOUNT, NullPaymentGatewayType.NULL_GATEWAY);

        responseDTO.paymentTransactionType(PaymentTransactionType.AUTHORIZE_AND_CAPTURE);
        responseDTO.amount(new Money(paymentRequestDTO.getTransactionTotal()));

        Map<String, Object> additionalFields = paymentRequestDTO.getAdditionalFields();

        if (additionalFields != null) {
            if (additionalFields.containsKey("desiredOutcome")) {
                String desiredOutome = (String) additionalFields.get("desiredOutcome");
                if (desiredOutome.equals("SOFT DECLINE")) {
                    responseDTO.successful(false);
                    responseDTO.rawResponse("confirmation - failure - soft decline");
                    responseDTO.responseMap(PaymentAdditionalFieldType.DECLINE_TYPE.getType(), PaymentDeclineType.SOFT.getType());
                } else if (desiredOutome.equals("HARD DECLINE")) {
                    responseDTO.successful(false);
                    responseDTO.rawResponse("confirmation - failure - hard decline");
                    responseDTO.responseMap(PaymentAdditionalFieldType.DECLINE_TYPE.getType(), PaymentDeclineType.HARD.getType());
                }

            }
        } else {
            responseDTO.rawResponse("confirmation - success");
            responseDTO.successful(true);
        }
        return responseDTO;

    }

    @Override
    public PaymentResponseDTO authorizeAndCapture(PaymentRequestDTO paymentRequestDTO) throws PaymentException {
        return commonCreditCardProcessing(paymentRequestDTO, PaymentTransactionType.AUTHORIZE_AND_CAPTURE);
    }

    @Override
    public PaymentResponseDTO reverseAuthorize(PaymentRequestDTO paymentRequestDTO) throws PaymentException {
        throw new PaymentException("The Rollback authorize method is not supported for this module");
    }

    @Override
    public PaymentResponseDTO refund(PaymentRequestDTO paymentRequestDTO) throws PaymentException {
        PaymentResponseDTO responseDTO = new PaymentResponseDTO(PaymentType.CREDIT_CARD, NullPaymentGatewayType.NULL_GATEWAY);
        responseDTO.valid(true)
                .paymentTransactionType(PaymentTransactionType.REFUND)
                .amount(new Money(paymentRequestDTO.getTransactionTotal()))
                .rawResponse("Successful Refund")
                .successful(true);

        return responseDTO;
    }

    @Override
    public PaymentResponseDTO voidPayment(PaymentRequestDTO paymentRequestDTO) throws PaymentException {
        throw new PaymentException("The void method is not supported for this module");
    }

    /**
     * Does minimal Credit Card Validation (luhn check and expiration date is after today).
     * Mimics the Response of a real Payment Gateway.
     *
     * @param creditCardDTO
     * @return
     */
    protected PaymentResponseDTO commonCreditCardProcessing(PaymentRequestDTO requestDTO, PaymentTransactionType paymentTransactionType) {
        PaymentResponseDTO responseDTO = new PaymentResponseDTO(PaymentType.CREDIT_CARD, NullPaymentGatewayType.NULL_GATEWAY);
        responseDTO.valid(true)
                .paymentTransactionType(paymentTransactionType);

        CreditCardDTO creditCardDTO = requestDTO.getCreditCard();
        String transactionAmount = requestDTO.getTransactionTotal();

        CreditCardValidator visaValidator = new CreditCardValidator(CreditCardValidator.VISA);
        CreditCardValidator amexValidator = new CreditCardValidator(CreditCardValidator.AMEX);
        CreditCardValidator mcValidator = new CreditCardValidator(CreditCardValidator.MASTERCARD);
        CreditCardValidator discoverValidator = new CreditCardValidator(CreditCardValidator.DISCOVER);

        if (StringUtils.isNotBlank(transactionAmount) &&
                StringUtils.isNotBlank(creditCardDTO.getCreditCardNum()) &&
                (StringUtils.isNotBlank(creditCardDTO.getCreditCardExpDate()) ||
                (StringUtils.isNotBlank(creditCardDTO.getCreditCardExpMonth()) &&
                StringUtils.isNotBlank(creditCardDTO.getCreditCardExpYear())))) {

            boolean validCard = false;
            if (visaValidator.isValid(creditCardDTO.getCreditCardNum())) {
                validCard = true;
            } else if (amexValidator.isValid(creditCardDTO.getCreditCardNum())) {
                validCard = true;
            } else if (mcValidator.isValid(creditCardDTO.getCreditCardNum())) {
                validCard = true;
            } else if (discoverValidator.isValid(creditCardDTO.getCreditCardNum())) {
                validCard = true;
            }

            boolean validDateFormat = false;
            boolean validDate = false;
            String[] parsedDate = null;
            if (StringUtils.isNotBlank(creditCardDTO.getCreditCardExpDate())) {
                parsedDate = creditCardDTO.getCreditCardExpDate().split("/");
            } else {
                parsedDate = new String[2];
                parsedDate[0] = creditCardDTO.getCreditCardExpMonth();
                parsedDate[1] = creditCardDTO.getCreditCardExpYear();
            }

            if (parsedDate.length == 2) {
                String expMonth = parsedDate[0];
                String expYear = parsedDate[1];
                try {
                    DateTime expirationDate = new DateTime(Integer.parseInt("20" + expYear), Integer.parseInt(expMonth), 1, 0, 0);
                    expirationDate = expirationDate.dayOfMonth().withMaximumValue();
                    validDate = expirationDate.isAfterNow();
                    validDateFormat = true;
                } catch (Exception e) {
                    //invalid date format
                }
            }

            if (!validDate || !validDateFormat) {
                responseDTO.amount(new Money(0))
                        .rawResponse("cart.payment.expiration.invalid")
                        .successful(false);
            } else if (!validCard) {
                responseDTO.amount(new Money(0))
                        .rawResponse("cart.payment.card.invalid")
                        .successful(false);
            } else {
                responseDTO.amount(new Money(requestDTO.getTransactionTotal()))
                        .rawResponse("Success!")
                        .successful(true);
            }

        } else {
            responseDTO.amount(new Money(0))
                    .rawResponse("cart.payment.invalid")
                    .successful(false);
        }

        return responseDTO;
    }
}
