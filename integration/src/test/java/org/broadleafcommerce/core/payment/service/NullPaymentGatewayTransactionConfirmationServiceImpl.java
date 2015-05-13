/*
 * #%L
 * BroadleafCommerce Framework Web
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

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.payment.PaymentAdditionalFieldType;
import org.broadleafcommerce.common.payment.PaymentDeclineType;
import org.broadleafcommerce.common.payment.PaymentTransactionType;
import org.broadleafcommerce.common.payment.PaymentType;
import org.broadleafcommerce.common.payment.dto.PaymentRequestDTO;
import org.broadleafcommerce.common.payment.dto.PaymentResponseDTO;
import org.broadleafcommerce.common.payment.service.PaymentGatewayTransactionConfirmationService;
import org.broadleafcommerce.common.vendor.service.exception.PaymentException;
import org.springframework.stereotype.Service;



/**
 * @author Elbert Bautista (elbertbautista)
 */
@Service("blNullPaymentGatewayHostedTransactionConfirmationService")
public class NullPaymentGatewayTransactionConfirmationServiceImpl implements PaymentGatewayTransactionConfirmationService {

    protected static final Log LOG = LogFactory.getLog(NullPaymentGatewayTransactionConfirmationServiceImpl.class);

    private static Money oneHundred = new Money(100);
    private static Money twoHundred = new Money(200);

    @Resource(name = "blNullPaymentGatewayHostedConfiguration")
    protected NullPaymentGatewayHostedConfiguration configuration;

    /**
     * for the test implementation, and in order to test different response scenarios, we generate the following results:
     * <ul>
     *   <li>for payments with a total that is less than $100, we return an error with HARD decline</li>
     *   <li>for payments with a total between $100 and $200 (inclusive) we return an error with SOFT decline</li>
     *   <li>for payments with more than $200 we return success</li>
     * </ul>
     */
    @Override
    public PaymentResponseDTO confirmTransaction(PaymentRequestDTO paymentRequestDTO) throws PaymentException {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Null Payment Hosted Gateway - Confirming Transaction with amount: " + paymentRequestDTO.getTransactionTotal());
        }

        PaymentTransactionType type = PaymentTransactionType.AUTHORIZE_AND_CAPTURE;
        if (!configuration.isPerformAuthorizeAndCapture()) {
            type = PaymentTransactionType.AUTHORIZE;
        }

        Money transactionTotal = new Money(paymentRequestDTO.getTransactionTotal());

        PaymentResponseDTO responseDTO = new PaymentResponseDTO(PaymentType.THIRD_PARTY_ACCOUNT, NullPaymentGatewayType.NULL_GATEWAY);
        responseDTO.paymentTransactionType(type);
        responseDTO.amount(new Money(paymentRequestDTO.getTransactionTotal()));

        if (transactionTotal.lessThan(oneHundred)) {
            responseDTO.rawResponse("confirmation - failure - hard decline");
            responseDTO.successful(false);
            responseDTO.responseMap(PaymentAdditionalFieldType.DECLINE_TYPE.getType(), PaymentDeclineType.HARD.getType());
        } else if (transactionTotal.lessThanOrEqual(twoHundred)) {
            responseDTO.rawResponse("confirmation - failure - soft decline");
            responseDTO.successful(false);
            responseDTO.responseMap(PaymentAdditionalFieldType.DECLINE_TYPE.getType(), PaymentDeclineType.SOFT.getType());
        } else {
            responseDTO.rawResponse("confirmation - success");
            responseDTO.successful(true);
        }

        return responseDTO;
    }
}
