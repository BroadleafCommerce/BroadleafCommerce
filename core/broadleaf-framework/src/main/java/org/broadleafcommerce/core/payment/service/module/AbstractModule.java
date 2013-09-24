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

import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.core.payment.domain.PaymentInfo;
import org.broadleafcommerce.core.payment.domain.PaymentInfoDetail;
import org.broadleafcommerce.core.payment.domain.PaymentInfoDetailImpl;
import org.broadleafcommerce.core.payment.domain.PaymentInfoDetailType;
import org.broadleafcommerce.core.payment.domain.PaymentResponseItem;
import org.broadleafcommerce.core.payment.service.PaymentContext;
import org.broadleafcommerce.core.payment.service.PaymentInfoService;
import org.broadleafcommerce.core.payment.service.exception.PaymentException;

import javax.annotation.Resource;

public abstract class AbstractModule implements PaymentModule {

    @Resource(name="blPaymentInfoService")
    private PaymentInfoService paymentInfoService;

    @Override
    public PaymentResponseItem authorize(PaymentContext paymentContext) throws PaymentException {
        Money amountToAuthorize = paymentContext.getRemainingTransactionAmount();
        PaymentResponseItem responseItem = getNewResponseItem(amountToAuthorize, findPaymentInfoFromContext(paymentContext).getCurrency());
        return processAuthorize(paymentContext, amountToAuthorize, responseItem);
    }

    /**
     * Typically payment module implementors would not override this method.   They would instead override the
     * processReverseAuthorize() method.
     *
     * @param paymentContext
     * @return
     * @throws PaymentException
     */
    @Override
    public PaymentResponseItem reverseAuthorize(PaymentContext paymentContext) throws PaymentException {
        Money amountAvailableToReverseAuthorize = getAmountAvailableToReverseAuthorize(paymentContext);
        PaymentInfo paymentInfo = findPaymentInfoFromContext(paymentContext);

        PaymentResponseItem responseItem = getNewResponseItem(amountAvailableToReverseAuthorize, paymentInfo.getCurrency());

        //Add PaymentInfoDetail - ReverseAuth
        paymentInfo.getPaymentInfoDetails().add(getNewReverseAuthPaymentInfoDetail(paymentInfo, amountAvailableToReverseAuthorize));
        return processReverseAuthorize(paymentContext, amountAvailableToReverseAuthorize, responseItem);
    }

    @Override
    public PaymentResponseItem debit(PaymentContext paymentContext) throws PaymentException {
        Money amountAvailableToDebit = getAmountAvailableToDebit(paymentContext);
        PaymentInfo paymentInfo = findPaymentInfoFromContext(paymentContext);

        PaymentResponseItem responseItem = getNewResponseItem(amountAvailableToDebit, paymentInfo.getCurrency());
        //Add PaymentInfoDetail - Capture
        paymentInfo.getPaymentInfoDetails().add(getNewCapturePaymentInfoDetail(paymentInfo, amountAvailableToDebit));

        return processDebit(paymentContext, amountAvailableToDebit, responseItem);
    }

    @Override
    public PaymentResponseItem authorizeAndDebit(PaymentContext paymentContext) throws PaymentException {
        return processAuthorizeAndDebit(paymentContext, getAmountAvailableToDebit(paymentContext), getNewResponseItem());
    }

    @Override
    public PaymentResponseItem credit(PaymentContext paymentContext) throws PaymentException {
        Money amountAvailableToCredit = getAmountAvailableToCredit(paymentContext);
        PaymentInfo paymentInfo = findPaymentInfoFromContext(paymentContext);

        PaymentResponseItem responseItem = getNewResponseItem(amountAvailableToCredit, paymentInfo.getCurrency());
        //Add PaymentInfoDetail - Refund
        paymentInfo.getPaymentInfoDetails().add(getNewRefundPaymentInfoDetail(paymentInfo, amountAvailableToCredit));

        return processCredit(paymentContext, amountAvailableToCredit, responseItem);
    }

    @Override
    public PaymentResponseItem voidPayment(PaymentContext paymentContext) throws PaymentException {
        PaymentInfo paymentInfo = findPaymentInfoFromContext(paymentContext);
        Money amountAlreadyCaptured = paymentInfo.getPaymentCapturedAmount();
        PaymentResponseItem responseItem = getNewResponseItem(amountAlreadyCaptured, paymentInfo.getCurrency());
        return processVoidPayment(paymentContext, amountAlreadyCaptured, responseItem);
    }

    @Override
    public PaymentResponseItem balance(PaymentContext paymentContext) throws PaymentException {
        return processBalance(paymentContext, getNewResponseItem());
    }

    @Override
    public PaymentResponseItem partialPayment(PaymentContext paymentContext) throws PaymentException {
        Money amountAvailableToDebit = getAmountAvailableToDebit(paymentContext);
        PaymentInfo paymentInfo = findPaymentInfoFromContext(paymentContext);

        PaymentResponseItem responseItem = getNewResponseItem(amountAvailableToDebit, paymentInfo.getCurrency());
        //Add PaymentInfoDetail - Capture
        paymentInfo.getPaymentInfoDetails().add(getNewCapturePaymentInfoDetail(paymentInfo, amountAvailableToDebit));

        return processPartialPayment(paymentContext, amountAvailableToDebit, responseItem);
    }

    protected PaymentResponseItem getNewResponseItem() {
        return getNewResponseItem(null, null);
    }

    protected PaymentResponseItem getNewResponseItem(Money amount, BroadleafCurrency currency) {
        PaymentResponseItem responseItem = paymentInfoService.createResponseItem();
        responseItem.setTransactionAmount(amount);
        responseItem.setCurrency(currency);
        return responseItem;
    }
    
    protected PaymentInfoDetail getNewPaymentInfoDetail(PaymentInfo paymentInfo, PaymentInfoDetailType type, Money amount) {
        PaymentInfoDetail paymentInfoDetail = new PaymentInfoDetailImpl();
        paymentInfoDetail.setPaymentInfo(paymentInfo);
        paymentInfoDetail.setType(type);
        paymentInfoDetail.setDate(SystemTime.asDate());
        paymentInfoDetail.setAmount(amount);
        return paymentInfoDetail;
    }
    
    protected PaymentInfoDetail getNewCapturePaymentInfoDetail(PaymentInfo paymentInfo, Money amount) {
        return getNewPaymentInfoDetail(paymentInfo, PaymentInfoDetailType.CAPTURE, amount);
    }

    protected PaymentInfoDetail getNewRefundPaymentInfoDetail(PaymentInfo paymentInfo, Money amount){
        return getNewPaymentInfoDetail(paymentInfo, PaymentInfoDetailType.REFUND, amount);
    }

    protected PaymentInfoDetail getNewReverseAuthPaymentInfoDetail(PaymentInfo paymentInfo, Money amount) {
        return getNewPaymentInfoDetail(paymentInfo, PaymentInfoDetailType.REVERSE_AUTH, amount);
    }

    protected PaymentInfo findPaymentInfoFromContext(PaymentContext paymentContext){
        PaymentInfo paymentInfo = paymentContext.getPaymentInfo();
        for (PaymentInfo pi : paymentContext.getPaymentInfo().getOrder().getPaymentInfos()) {
            if (paymentInfo.equals(pi)) {
                return pi;
            }
        }
        return null;
    }
    
    protected Money getAmountAvailableToDebit(PaymentContext paymentContext) {
        // Get the remaining amount to debit for the current transaction
        Money amountToDebit = paymentContext.getRemainingTransactionAmount();
        PaymentInfo paymentInfo = findPaymentInfoFromContext(paymentContext);
        // Get the amount available to debit = [(amount) - (amount already captured) - (amount already reversed)]
        Money amountCaptured = paymentInfo.getPaymentCapturedAmount();
        Money amountAlreadyReversed = paymentInfo.getReverseAuthAmount();
        Money amount = paymentInfo.getAmount();

        // Factor in credits that have been applied to the order through other different payment modules.
        Money orderTotal = paymentInfo.getOrder().getTotal();
        Money appliedCreditAdjustment = orderTotal.subtract(amount);
        Money adjustedAmountToDebit = amountToDebit.subtract(appliedCreditAdjustment).abs();
        if (adjustedAmountToDebit.lessThan(amountToDebit) && paymentInfo.getOrder().getCapturedTotal().equals(appliedCreditAdjustment)) {
            amountToDebit = adjustedAmountToDebit;
        }

        Money amountAvailableToDebit = amount.subtract(amountCaptured).subtract(amountAlreadyReversed);
        // Return the minimum of (amountToDebit, amountAvailableToDebit)
        if (amountAvailableToDebit.lessThan(amountToDebit)){
            return amountAvailableToDebit;
        } else {
            return amountToDebit;
        }
    }

    protected Money getAmountAvailableToCredit(PaymentContext paymentContext) {
        // Get the remaining amount to credit for the current transaction
        Money amountToCredit = paymentContext.getRemainingTransactionAmount();
        PaymentInfo paymentInfo = findPaymentInfoFromContext(paymentContext);
        // Get the amount available to credit = [(amountCaptured) - (amount already refunded)]
        Money amountCaptured = paymentInfo.getPaymentCapturedAmount();
        Money amountAlreadyCredited = paymentInfo.getPaymentCreditedAmount();
        Money amountAvailableToCredit = amountCaptured.subtract(amountAlreadyCredited);
        // Return the minimum of (amountToCredit, amountAvailableToCredit)
        if (amountAvailableToCredit.lessThan(amountToCredit)){
            return amountAvailableToCredit;
        } else {
            return amountToCredit;
        }
    }

    protected Money getAmountAvailableToReverseAuthorize(PaymentContext paymentContext) {
        // Get the remaining amount to reverse for the current transaction
        Money amountToReverse = paymentContext.getRemainingTransactionAmount();
        PaymentInfo paymentInfo = findPaymentInfoFromContext(paymentContext);
        // Get the amount available to reverse = [(amount) - (amount already captured) - (amount already reversed)]
        Money amountCaptured = paymentInfo.getPaymentCapturedAmount();
        Money amountAlreadyReversed = paymentInfo.getReverseAuthAmount();
        Money amount = paymentInfo.getAmount();
        Money amountAvailableToReverse = amount.subtract(amountCaptured).subtract(amountAlreadyReversed);
        // Return the minimum of (amountToReverse, amountAvailableToReverse)
        if (amountAvailableToReverse.lessThan(amountToReverse)){
            return amountAvailableToReverse;
        } else {
            return amountToReverse;
        }
    }
}
