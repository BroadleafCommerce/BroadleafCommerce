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
/**
 * @author Austin Rooke(austinrooke)
 */
package org.broadleafcommerce.core.spec.checkout.service.workflow

import org.broadleafcommerce.common.money.Money
import org.broadleafcommerce.common.payment.PaymentGatewayType
import org.broadleafcommerce.common.payment.PaymentTransactionType
import org.broadleafcommerce.common.payment.dto.PaymentRequestDTO
import org.broadleafcommerce.common.payment.dto.PaymentResponseDTO
import org.broadleafcommerce.common.payment.service.PaymentGatewayCheckoutService
import org.broadleafcommerce.common.payment.service.PaymentGatewayConfiguration
import org.broadleafcommerce.common.payment.service.PaymentGatewayConfigurationService
import org.broadleafcommerce.common.payment.service.PaymentGatewayConfigurationServiceProvider
import org.broadleafcommerce.common.payment.service.PaymentGatewayCreditCardService
import org.broadleafcommerce.common.payment.service.PaymentGatewayCustomerService
import org.broadleafcommerce.common.payment.service.PaymentGatewayFraudService
import org.broadleafcommerce.common.payment.service.PaymentGatewayHostedService
import org.broadleafcommerce.common.payment.service.PaymentGatewayReportingService
import org.broadleafcommerce.common.payment.service.PaymentGatewayRollbackService
import org.broadleafcommerce.common.payment.service.PaymentGatewaySubscriptionService
import org.broadleafcommerce.common.payment.service.PaymentGatewayTransactionConfirmationService
import org.broadleafcommerce.common.payment.service.PaymentGatewayTransactionService
import org.broadleafcommerce.common.payment.service.PaymentGatewayTransparentRedirectService
import org.broadleafcommerce.common.payment.service.PaymentGatewayWebResponseService
import org.broadleafcommerce.common.vendor.service.exception.PaymentException
import org.broadleafcommerce.common.web.payment.expression.PaymentGatewayFieldExtensionHandler
import org.broadleafcommerce.common.web.payment.processor.CreditCardTypesExtensionHandler
import org.broadleafcommerce.common.web.payment.processor.TRCreditCardExtensionHandler
import org.broadleafcommerce.core.checkout.service.workflow.ConfirmPaymentsRollbackHandler
import org.broadleafcommerce.core.checkout.service.workflow.ValidateAndConfirmPaymentActivity
import org.broadleafcommerce.core.order.domain.Order
import org.broadleafcommerce.core.order.service.OrderService
import org.broadleafcommerce.core.payment.domain.OrderPaymentImpl
import org.broadleafcommerce.core.payment.domain.PaymentTransaction
import org.broadleafcommerce.core.payment.domain.PaymentTransactionImpl
import org.broadleafcommerce.core.payment.service.OrderPaymentService
import org.broadleafcommerce.core.payment.service.OrderToPaymentRequestDTOService
import org.broadleafcommerce.core.pricing.service.exception.PricingException
import org.broadleafcommerce.core.workflow.state.RollbackFailureException
import org.broadleafcommerce.core.workflow.state.RollbackHandler


class ConfirmPaymentsRollbackHandlerSpec extends BaseCheckoutRollbackSpec{

    PaymentGatewayConfigurationServiceProvider mockPaymentConfigurationServiceProvider
    OrderToPaymentRequestDTOService mockOrderToPaymentRequestDTOService
    OrderPaymentService mockOrderPaymentService
    PaymentGatewayCheckoutService mockPaymentGatewayCheckoutService
    OrderService mockOrderService
    Order order
    Collection<PaymentTransaction> paymentTransactions

    def setup() {
        mockPaymentConfigurationServiceProvider = Mock()
        mockOrderToPaymentRequestDTOService = Mock()
        mockPaymentGatewayCheckoutService = Mock()

        stateConfiguration = new HashMap<String, Collection<PaymentTransaction>>()

        PaymentGatewayConfigurationService cfg = new PaymentGatewayConfigurationService() {

            PaymentGatewayRollbackService paymentGatewayRollbackService = new PaymentGatewayRollbackService() {

                public PaymentResponseDTO rollbackRefund(PaymentRequestDTO paymentRequestDTO) {
                    PaymentResponseDTO paymentResponseDTO = new PaymentResponseDTO(null, null)
                    paymentResponseDTO.amount= new Money(1.00)
                    paymentResponseDTO.rawResponse("rawResponse")
                    paymentResponseDTO.successful = true
                    paymentResponseDTO.paymentTransactionType = PaymentTransactionType.REVERSE_AUTH
                    paymentResponseDTO.responseMap("key","value")

                    return paymentResponseDTO
                }

                public PaymentResponseDTO rollbackCapture(PaymentRequestDTO paymentRequestDTO) {
                    PaymentResponseDTO paymentResponseDTO = new PaymentResponseDTO(null, null)
                    paymentResponseDTO.amount(new Money(1.00))
                    paymentResponseDTO.rawResponse("rawResponse")
                    paymentResponseDTO.successful(true)
                    paymentResponseDTO.paymentTransactionType(PaymentTransactionType.CAPTURE)
                    paymentResponseDTO.responseMap("key","value")

                    return paymentResponseDTO
                }

                public PaymentResponseDTO rollbackAuthorize(PaymentRequestDTO paymentRequestDTO) throws PaymentException {
                    if(paymentRequestDTO != null){
                        throw new PaymentException()
                    }

                    PaymentResponseDTO paymentResponseDTO = new PaymentResponseDTO(null, null)
                    paymentResponseDTO.amount(new Money(1.00))
                    paymentResponseDTO.rawResponse("rawResponse")
                    paymentResponseDTO.successful(true)
                    paymentResponseDTO.paymentTransactionType(PaymentTransactionType.REVERSE_AUTH)
                    paymentResponseDTO.responseMap("key","value")

                    return paymentResponseDTO
                }

                public PaymentResponseDTO rollbackAuthorizeAndCapture(PaymentRequestDTO paymentRequestDTO) {
                    PaymentResponseDTO paymentResponseDTO = new PaymentResponseDTO(null, null)
                    paymentResponseDTO.amount(new Money(1.00))
                    paymentResponseDTO.rawResponse("rawResponse")
                    paymentResponseDTO.successful = false
                    paymentResponseDTO.paymentTransactionType(PaymentTransactionType.VOID)
                    paymentResponseDTO.responseMap("key","value")

                    return paymentResponseDTO
                }
            }
            public PaymentGatewayRollbackService getRollbackService() {
                return paymentGatewayRollbackService
            }

            public PaymentGatewayConfiguration getConfiguration() {
                return null
            }

            public PaymentGatewayTransactionService getTransactionService() {
                return null
            }

            public PaymentGatewayTransactionConfirmationService getTransactionConfirmationService() {
                return null
            }

            public PaymentGatewayReportingService getReportingService() {
                return null
            }

            public PaymentGatewayCreditCardService getCreditCardService() {
                return null
            }

            public PaymentGatewayCustomerService getCustomerService() {
                return null
            }

            public PaymentGatewaySubscriptionService getSubscriptionService() {
                return null
            }

            public PaymentGatewayFraudService getFraudService() {
                return null
            }

            public PaymentGatewayHostedService getHostedService() {
                return null
            }

            public PaymentGatewayWebResponseService getWebResponseService() {
                return null
            }

            public PaymentGatewayTransparentRedirectService getTransparentRedirectService() {
                return null
            }

            public TRCreditCardExtensionHandler getCreditCardExtensionHandler() {
                return null
            }

            public PaymentGatewayFieldExtensionHandler getFieldExtensionHandler() {
                return null
            }

            public CreditCardTypesExtensionHandler getCreditCardTypesExtensionHandler() {
                return null
            }
        }
        mockPaymentConfigurationServiceProvider.getGatewayConfigurationService(_) >> { cfg }
    }



    def "Test that Exception is thrown when a paymentConfigurationServiceProvider is not provided"() {
        RollbackHandler rollbackHandler = new ConfirmPaymentsRollbackHandler().with {
            paymentConfigurationServiceProvider = null
            it
        }


        when: "rollbackState is executed"
        rollbackHandler.rollbackState(activity, context, stateConfiguration)

        then: "RollbackFailureException is thrown"
        thrown(RollbackFailureException)
    }

    def "Test that Exception is thrown when an error occurs during transaction rollback"() {
        setup:"Place a PaymentTransaction into the seed as well as the StateConfiguration"
        PaymentTransaction tx1 = new PaymentTransactionImpl()
        tx1.id = 1
        tx1.amount = new Money(1.00)
        tx1.orderPayment = new OrderPaymentImpl()
        tx1.orderPayment.id = 2
        tx1.orderPayment.gatewayType = PaymentGatewayType.TEMPORARY
        tx1.type = PaymentTransactionType.AUTHORIZE
        paymentTransactions = new ArrayList()
        paymentTransactions.add(tx1)
        context.seedData.order.payments.add(tx1.orderPayment)
        stateConfiguration.put(ValidateAndConfirmPaymentActivity.CONFIRMED_TRANSACTIONS,paymentTransactions)
        mockOrderToPaymentRequestDTOService.translatePaymentTransaction(_, _) >> { new PaymentRequestDTO() }

        mockOrderService = Mock()
        mockOrderPaymentService = Mock()
        order = context.seedData.order
        RollbackHandler rollbackHandler = new ConfirmPaymentsRollbackHandler().with {
            paymentConfigurationServiceProvider = mockPaymentConfigurationServiceProvider
            transactionToPaymentRequestDTOService = mockOrderToPaymentRequestDTOService
            orderPaymentService = mockOrderPaymentService
            paymentGatewayCheckoutService = mockPaymentGatewayCheckoutService
            orderService = mockOrderService
            it
        }

        when: "rollbackState is executed"
        rollbackHandler.rollbackState(activity, context, stateConfiguration)

        then: "PaymentException is thrown during transaction logging"
        thrown(RollbackFailureException)
    }

    def "Test that Exception is thrown when a rollback Failure occurs when attemping to invalidate payments"() {
        setup: "Place a PaymentTransaction into the seed as well as the StateConfiguration"
        PaymentTransaction tx1 = new PaymentTransactionImpl()
        tx1.id = 1
        tx1.amount = new Money(1.00)
        tx1.orderPayment = new OrderPaymentImpl()
        tx1.orderPayment.id = 2
        tx1.orderPayment.gatewayType = PaymentGatewayType.TEMPORARY
        tx1.type = PaymentTransactionType.AUTHORIZE_AND_CAPTURE
        paymentTransactions = new ArrayList()
        paymentTransactions.add(tx1)
        context.seedData.order.payments.add(tx1.orderPayment)
        stateConfiguration.put(ValidateAndConfirmPaymentActivity.CONFIRMED_TRANSACTIONS,paymentTransactions)

        mockOrderPaymentService = Mock()
        mockOrderService = Mock()
        order = context.seedData.order
        RollbackHandler rollbackHandler = new ConfirmPaymentsRollbackHandler().with {
            paymentConfigurationServiceProvider = mockPaymentConfigurationServiceProvider
            transactionToPaymentRequestDTOService = mockOrderToPaymentRequestDTOService
            orderPaymentService = mockOrderPaymentService
            paymentGatewayCheckoutService = mockPaymentGatewayCheckoutService
            orderService = mockOrderService
            it
        }

        when: "rollbackState is executed"
        rollbackHandler.rollbackState(activity, context, stateConfiguration)

        then: "The Transaction is recorded and a RollbackFailureException is thrown during payment invalidation"
        1 * mockOrderPaymentService.createTransaction() >> { new PaymentTransactionImpl() }
        1 * mockOrderPaymentService.save(_)
        thrown(RollbackFailureException)
    }

    def "Test that Exception is thrown when the OrderService is unable to save invalidated payments"() {
        setup: "Place a PaymentTransaction into the seedData as well as into the StateConfiguration as well as set up the orderService to throw a PricingException"
        PaymentTransaction tx1 = new PaymentTransactionImpl()
        tx1.id = 1
        tx1.amount = new Money(1.00)
        tx1.orderPayment = new OrderPaymentImpl()
        tx1.orderPayment.id = 2
        tx1.orderPayment.gatewayType = PaymentGatewayType.TEMPORARY
        tx1.type = PaymentTransactionType.AUTHORIZE
        paymentTransactions = new ArrayList()
        paymentTransactions.add(tx1)
        context.seedData.order.payments.add(tx1.orderPayment)
        stateConfiguration.put(ValidateAndConfirmPaymentActivity.CONFIRMED_TRANSACTIONS,paymentTransactions)
        mockOrderPaymentService = Mock()
        mockOrderService = Mock()
        order = context.seedData.order
        mockOrderPaymentService.createTransaction() >> { new PaymentTransactionImpl() }
        mockOrderPaymentService.save(_) >> { args -> return args[0] }

        RollbackHandler rollbackHandler = new ConfirmPaymentsRollbackHandler().with {
            paymentConfigurationServiceProvider = mockPaymentConfigurationServiceProvider
            transactionToPaymentRequestDTOService = mockOrderToPaymentRequestDTOService
            orderPaymentService = mockOrderPaymentService
            paymentGatewayCheckoutService = mockPaymentGatewayCheckoutService
            orderService = mockOrderService
            it
        }

        when: "rollbackState is executed"
        rollbackHandler.rollbackState(activity, context, stateConfiguration)

        then: "RollbackFailureException is thrown during saving of payment invalidation"
        1 * mockOrderService.save(_, _) >> { throw new PricingException() }
        thrown(RollbackFailureException)
    }

    def "Test a successful run with valid data"() {
        setup: "Place a paymentTransaction into the seedData as well as the stateConfiguration"
        PaymentTransaction tx1 = new PaymentTransactionImpl()
        tx1.id = 1
        tx1.amount = new Money(1.00)
        tx1.orderPayment = new OrderPaymentImpl()
        tx1.orderPayment.id = 2
        tx1.orderPayment.gatewayType = PaymentGatewayType.TEMPORARY
        tx1.type = PaymentTransactionType.AUTHORIZE
        paymentTransactions = new ArrayList()
        paymentTransactions.add(tx1)
        context.seedData.order.payments.add(tx1.orderPayment)
        stateConfiguration.put(ValidateAndConfirmPaymentActivity.CONFIRMED_TRANSACTIONS,paymentTransactions)

        mockOrderPaymentService = Mock()
        mockOrderService = Mock()
        order = context.seedData.order
        mockOrderPaymentService.createTransaction() >> { new PaymentTransactionImpl() }
        RollbackHandler rollbackHandler = new ConfirmPaymentsRollbackHandler().with {
            paymentConfigurationServiceProvider = mockPaymentConfigurationServiceProvider
            transactionToPaymentRequestDTOService = mockOrderToPaymentRequestDTOService
            orderPaymentService = mockOrderPaymentService
            paymentGatewayCheckoutService = mockPaymentGatewayCheckoutService
            orderService = mockOrderService
            it
        }

        when: "rollbackState is executed"
        rollbackHandler.rollbackState(activity, context, stateConfiguration)

        then: "No exceptions are encountered and the orderService and orderPaymentService successfully saves the results"
        1 * mockPaymentGatewayCheckoutService.markPaymentAsInvalid(_)
        1 * mockOrderService.save(_,_)
        1 * mockOrderPaymentService.save(_) >> { args -> return args[0] }
        order.getPayments().size() == 1
    }
}
