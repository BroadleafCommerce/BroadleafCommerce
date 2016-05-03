/*
 * #%L
 * BroadleafCommerce Integration
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
import org.broadleafcommerce.common.payment.service.AbstractPaymentGatewayTransactionService;
import org.broadleafcommerce.common.payment.service.FailureCountExposable;
import org.broadleafcommerce.common.payment.service.PaymentGatewayTransactionService;
import org.broadleafcommerce.common.vendor.service.exception.PaymentException;
import org.broadleafcommerce.common.vendor.service.type.ServiceStatusType;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

@Service("blNullIntegrationGatewayTransactionService")
public class NullIntegrationGatewayTransactionServiceImpl extends AbstractPaymentGatewayTransactionService implements FailureCountExposable {

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

        PaymentResponseDTO responseDTO = new PaymentResponseDTO(PaymentType.THIRD_PARTY_ACCOUNT, NullIntegrationGatewayType.NULL_INTEGRATION_GATEWAY);

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
        PaymentResponseDTO responseDTO = new PaymentResponseDTO(PaymentType.CREDIT_CARD, NullIntegrationGatewayType.NULL_INTEGRATION_GATEWAY);
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
     */
    protected PaymentResponseDTO commonCreditCardProcessing(PaymentRequestDTO requestDTO, PaymentTransactionType paymentTransactionType) {
        PaymentResponseDTO responseDTO = new PaymentResponseDTO(PaymentType.CREDIT_CARD, NullIntegrationGatewayType.NULL_INTEGRATION_GATEWAY);
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

    protected Integer failureCount = 0;
    protected Boolean isUp = true;

    public synchronized void clearStatus() {
        isUp = true;
        failureCount = 0;
    }

    /**
     * arbitrarily set a failure threshold value of "3"
     */
    public synchronized void incrementFailure() {
        if (failureCount >= getFailureReportingThreshold()) {
            isUp = false;
        } else {
            failureCount++;
        }
    }

    @Override
    public synchronized ServiceStatusType getServiceStatus() {
        if (isUp) {
            return ServiceStatusType.UP;
        } else {
            return ServiceStatusType.DOWN;
        }
    }

    @Override
    public Integer getFailureReportingThreshold() {
        return 3;
    }

}
