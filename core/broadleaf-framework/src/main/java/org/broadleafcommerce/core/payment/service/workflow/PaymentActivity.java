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

package org.broadleafcommerce.core.payment.service.workflow;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.payment.domain.PaymentInfo;
import org.broadleafcommerce.core.payment.domain.PaymentResponseItem;
import org.broadleafcommerce.core.payment.domain.Referenced;
import org.broadleafcommerce.core.payment.service.PaymentContextImpl;
import org.broadleafcommerce.core.payment.service.PaymentService;
import org.broadleafcommerce.core.payment.service.exception.InsufficientFundsException;
import org.broadleafcommerce.core.payment.service.exception.PaymentException;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.state.ActivityStateManagerImpl;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PaymentActivity extends BaseActivity<WorkflowPaymentContext> {

    public static final String ROLLBACK_PAYMENTCONTEXT = "rollback_paymentcontext";
    public static final String ROLLBACK_RESPONSEITEM = "rollback_responseitem";
    public static final String ROLLBACK_ACTIONTYPE = "rollback_actiontype";

    protected PaymentService paymentService;
    protected String userName;
    protected boolean automaticallyRegisterRollbackHandlerForPayment = true;

    public PaymentActivity() {
        setAutomaticallyRegisterRollbackHandler(false);
    }

    /* (non-Javadoc)
         * @see org.broadleafcommerce.core.workflow.Activity#execute(org.broadleafcommerce.core.workflow.ProcessContext)
         */
    @Override
    public WorkflowPaymentContext execute(WorkflowPaymentContext context) throws Exception {
        CombinedPaymentContextSeed seed = context.getSeedData();
        Map<PaymentInfo, Referenced> infos = seed.getInfos();

        // If seed has a specified total, use that; otherwise use order total.
        Money transactionTotal, remainingTotal;
        if (seed.getTransactionAmount() != null) {
            transactionTotal = seed.getTransactionAmount();
            remainingTotal = seed.getTransactionAmount();
        } else {
            transactionTotal = seed.getOrderTotal();
            remainingTotal = seed.getOrderTotal();
        }
        Map<PaymentInfo, Referenced> replaceItems = new HashMap<PaymentInfo, Referenced>();
        try {
            Iterator<PaymentInfo> itr = infos.keySet().iterator();
            while(itr.hasNext()) {
                PaymentInfo info = itr.next();
                if (paymentService.isValidCandidate(info.getType())) {
                    Referenced referenced = infos.get(info);
                    itr.remove();
                    infos.remove(info);
                    PaymentContextImpl paymentContext = new PaymentContextImpl(transactionTotal, remainingTotal, info, referenced, userName);
                    PaymentResponseItem paymentResponseItem;
                    if (seed.getActionType().equals(PaymentActionType.AUTHORIZE)) {
                        try {
                            paymentResponseItem = paymentService.authorize(paymentContext);
                        } finally {
                            referenced.setReferenceNumber(info.getReferenceNumber());
                            replaceItems.put(info, referenced);
                        }
                    } else if (seed.getActionType().equals(PaymentActionType.AUTHORIZEANDDEBIT)) {
                        try {
                            paymentResponseItem = paymentService.authorizeAndDebit(paymentContext);
                        } finally {
                            referenced.setReferenceNumber(info.getReferenceNumber());
                            replaceItems.put(info, referenced);
                        }
                    } else if (seed.getActionType().equals(PaymentActionType.BALANCE)) {
                        try {
                            paymentResponseItem = paymentService.balance(paymentContext);
                        } finally {
                            referenced.setReferenceNumber(info.getReferenceNumber());
                            replaceItems.put(info, referenced);
                        }
                    } else if (seed.getActionType().equals(PaymentActionType.CREDIT)) {
                        try {
                            paymentResponseItem = paymentService.credit(paymentContext);
                        } finally {
                            referenced.setReferenceNumber(info.getReferenceNumber());
                            replaceItems.put(info, referenced);
                        }
                    } else if (seed.getActionType().equals(PaymentActionType.DEBIT)) {
                        try {
                            paymentResponseItem = paymentService.debit(paymentContext);
                        } finally {
                            referenced.setReferenceNumber(info.getReferenceNumber());
                            replaceItems.put(info, referenced);
                        }
                    } else if (seed.getActionType().equals(PaymentActionType.VOID)) {
                        try {
                            paymentResponseItem = paymentService.voidPayment(paymentContext);
                        } finally {
                            referenced.setReferenceNumber(info.getReferenceNumber());
                            replaceItems.put(info, referenced);
                        }
                    } else if (seed.getActionType().equals(PaymentActionType.REVERSEAUTHORIZE)) {
                        try {
                            paymentResponseItem = paymentService.reverseAuthorize(paymentContext);
                        } finally {
                            referenced.setReferenceNumber(info.getReferenceNumber());
                            replaceItems.put(info, referenced);
                        }
                    } else if (seed.getActionType().equals(PaymentActionType.PARTIALPAYMENT)) {
                        try {
                            paymentResponseItem = paymentService.partialPayment(paymentContext);
                        } finally {
                            referenced.setReferenceNumber(info.getReferenceNumber());
                            replaceItems.put(info, referenced);
                        }
                    } else {
                        referenced.setReferenceNumber(info.getReferenceNumber());
                        replaceItems.put(info, referenced);
                        throw new PaymentException("Module ("+paymentService.getClass().getName()+") does not support payment type of: " + seed.getActionType().toString());
                    }
                    if (getRollbackHandler() != null && automaticallyRegisterRollbackHandlerForPayment) {
                        Map<String, Object> myState = new HashMap<String, Object>();
                        if (getStateConfiguration() != null && !getStateConfiguration().isEmpty()) {
                            myState.putAll(getStateConfiguration());
                        }
                        myState.put(ROLLBACK_ACTIONTYPE, seed.getActionType());
                        myState.put(ROLLBACK_PAYMENTCONTEXT, paymentContext);
                        myState.put(ROLLBACK_RESPONSEITEM, paymentResponseItem);

                        ActivityStateManagerImpl.getStateManager().registerState(this, context, getRollbackHandler(), myState);
                    }
                    if (paymentResponseItem != null) {
                        //validate payment response item
                        if (paymentResponseItem.getTransactionAmount() == null || paymentResponseItem.getTransactionTimestamp() == null || paymentResponseItem.getTransactionSuccess() == null) {
                            throw new PaymentException("The PaymentResponseItem instance did not contain one or more of the following: transactionAmount, transactionTimestamp or transactionSuccess");
                        }
                        seed.getPaymentResponse().addPaymentResponseItem(info, paymentResponseItem);
                        if (paymentResponseItem.getTransactionSuccess()) {
                            remainingTotal = remainingTotal.subtract(paymentResponseItem.getTransactionAmount());
                        } else {
                            if (paymentResponseItem.getTransactionAmount().lessThan(transactionTotal.getAmount())) {
                                throw new InsufficientFundsException(String.format("Transaction amount was [%s] but paid amount was [%s]",
                                        transactionTotal.getAmount(), paymentResponseItem.getTransactionAmount()));
                            }
                        }
                    }
                }
            }
        } finally {
            infos.putAll(replaceItems);
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

    public boolean getAutomaticallyRegisterRollbackHandlerForPayment() {
        return automaticallyRegisterRollbackHandlerForPayment;
    }

    public void setAutomaticallyRegisterRollbackHandlerForPayment(boolean automaticallyRegisterRollbackHandlerForPayment) {
        this.automaticallyRegisterRollbackHandlerForPayment = automaticallyRegisterRollbackHandlerForPayment;
    }
}
