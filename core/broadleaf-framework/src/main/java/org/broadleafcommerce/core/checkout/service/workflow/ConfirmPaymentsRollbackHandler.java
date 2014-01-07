/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
package org.broadleafcommerce.core.checkout.service.workflow;

import java.util.Collection;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.payment.PaymentTransactionType;
import org.broadleafcommerce.common.payment.dto.PaymentRequestDTO;
import org.broadleafcommerce.common.payment.service.PaymentGatewayConfigurationService;
import org.broadleafcommerce.common.payment.service.PaymentGatewayConfigurationServiceProvider;
import org.broadleafcommerce.common.vendor.service.exception.PaymentException;
import org.broadleafcommerce.core.payment.domain.PaymentTransaction;
import org.broadleafcommerce.core.payment.service.OrderToPaymentRequestDTOService;
import org.broadleafcommerce.core.workflow.Activity;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.broadleafcommerce.core.workflow.state.RollbackFailureException;
import org.broadleafcommerce.core.workflow.state.RollbackHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Rolls back all payments that were confirmed in {@link ValidateAndConfirmPaymentActivity}.
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
@Component("blConfirmPaymentsRollbackHandler")
public class ConfirmPaymentsRollbackHandler implements RollbackHandler<CheckoutSeed> {

    protected static final Log LOG = LogFactory.getLog(ConfirmPaymentsRollbackHandler.class);
    
    @Autowired(required = false)
    @Qualifier("blPaymentGatewayConfigurationServiceProvider")
    protected PaymentGatewayConfigurationServiceProvider paymentConfigurationServiceProvider;
    
    @Resource(name = "blOrderToPaymentRequestDTOService")
    protected OrderToPaymentRequestDTOService transactionToPaymentRequestDTOService;
    
    @Override
    public void rollbackState(Activity<? extends ProcessContext<CheckoutSeed>> activity, ProcessContext<CheckoutSeed> processContext, Map<String, Object> stateConfiguration) throws RollbackFailureException {
        CheckoutSeed seed = processContext.getSeedData();
        
        if (paymentConfigurationServiceProvider == null) {
            throw new RollbackFailureException("There is no rollback service configured for the payment gateway configuration, cannot rollback unconfirmed"
                    + " payments");
        }
        
        Collection<PaymentTransaction> transactions = (Collection<PaymentTransaction>) stateConfiguration.get(ValidateAndConfirmPaymentActivity.CONFIRMED_TRANSACTIONS);
        for (PaymentTransaction tx : transactions) {
            PaymentRequestDTO rollbackRequest = transactionToPaymentRequestDTOService.translatePaymentTransaction(tx.getAmount(), tx);
            
            PaymentGatewayConfigurationService cfg = paymentConfigurationServiceProvider.getGatewayConfigurationService(tx.getOrderPayment().getGatewayType());
            try {
                if (PaymentTransactionType.AUTHORIZE.equals(tx.getType())) {
                    cfg.getRollbackService().rollbackAuthorize(rollbackRequest);
                } else if (PaymentTransactionType.AUTHORIZE_AND_CAPTURE.equals(tx.getType())) {
                    cfg.getRollbackService().rollbackAuthorizeAndCapture(rollbackRequest);
                } else {
                    LOG.warn("The transaction with id " + tx.getId() + " will NOT rolled back as it is not an AUTHORIZE or AUTHORIZE_AND_CAPTURE transaction but is"
                            + " of type " + tx.getType() + ". If you need to roll back transactions of this type then provide a customized rollback handler for"
                                    + " confirming transactions.");
                }
            } catch (PaymentException e) {
                throw new RollbackFailureException("The transaction with id " + tx.getId() + " encountered and exception when it was attempted to roll back"
                        + " its confirmation", e);
            }
        }
    }

}
