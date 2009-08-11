/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.payment.service.workflow;

import java.util.Iterator;
import java.util.Map;

import org.broadleafcommerce.payment.domain.PaymentInfo;
import org.broadleafcommerce.payment.domain.PaymentResponseItem;
import org.broadleafcommerce.payment.domain.Referenced;
import org.broadleafcommerce.payment.service.PaymentContextImpl;
import org.broadleafcommerce.payment.service.PaymentService;
import org.broadleafcommerce.payment.service.exception.PaymentException;
import org.broadleafcommerce.util.money.Money;
import org.broadleafcommerce.workflow.BaseActivity;
import org.broadleafcommerce.workflow.ProcessContext;

public class PaymentActivity extends BaseActivity {

    protected PaymentService paymentService;
    protected String userName;

    /* (non-Javadoc)
     * @see org.broadleafcommerce.workflow.Activity#execute(org.broadleafcommerce.workflow.ProcessContext)
     */
    public ProcessContext execute(ProcessContext context) throws Exception {
        CombinedPaymentContextSeed seed = ((WorkflowPaymentContext) context).getSeedData();
        Map<PaymentInfo, Referenced> infos = seed.getInfos();
        Money orderTotal = seed.getOrderTotal();
        Money remainingTotal = seed.getOrderTotal();
        Iterator<PaymentInfo> itr = infos.keySet().iterator();
        while(itr.hasNext()) {
            PaymentInfo info = itr.next();
            if (paymentService.isValidCandidate(info.getType())) {
                PaymentContextImpl paymentContext = new PaymentContextImpl(orderTotal, remainingTotal, info, infos.get(info), userName);
                PaymentResponseItem paymentResponseItem;
                if (seed.getActionType().equals(PaymentActionType.AUTHORIZE)) {
                    paymentResponseItem = paymentService.authorize(paymentContext);
                } else if (seed.getActionType().equals(PaymentActionType.AUTHORIZEANDDEBIT)) {
                    paymentResponseItem = paymentService.authorizeAndDebit(paymentContext);
                } else if (seed.getActionType().equals(PaymentActionType.BALANCE)) {
                    paymentResponseItem = paymentService.balance(paymentContext);
                } else if (seed.getActionType().equals(PaymentActionType.CREDIT)) {
                    paymentResponseItem = paymentService.credit(paymentContext);
                } else if (seed.getActionType().equals(PaymentActionType.DEBIT)) {
                    paymentResponseItem = paymentService.debit(paymentContext);
                } else if (seed.getActionType().equals(PaymentActionType.VOID)) {
                    paymentResponseItem = paymentService.voidPayment(paymentContext);
                } else {
                    throw new PaymentException("Module ("+paymentService.getClass().getName()+") does not support payment type of: " + seed.getActionType().toString());
                }
                if (paymentResponseItem != null) {
                    //validate payment response item
                    if (paymentResponseItem.getAmountPaid() == null || paymentResponseItem.getTransactionTimestamp() == null || paymentResponseItem.getTransactionSuccess() == null) {
                        throw new PaymentException("The PaymentResponseItem instance did not contain one or more of the following: amountPaid, transactionTimestamp or transactionSuccess");
                    }
                    seed.getPaymentResponse().addPaymentResponseItem(info, paymentResponseItem);
                    remainingTotal = remainingTotal.subtract(paymentResponseItem.getAmountPaid());
                }
            }
        }

        return context;
    }

    public PaymentService getPaymentService() {
        return paymentService;
    }

    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
