/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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
package org.broadleafcommerce.core.spec.checkout.service.workflow

import org.broadleafcommerce.common.money.Money
import org.broadleafcommerce.common.payment.PaymentGatewayType
import org.broadleafcommerce.common.payment.PaymentTransactionType
import org.broadleafcommerce.common.payment.PaymentType
import org.broadleafcommerce.common.payment.dto.PaymentResponseDTO
import org.broadleafcommerce.core.checkout.service.exception.CheckoutException
import org.broadleafcommerce.core.checkout.service.strategy.OrderPaymentConfirmationStrategy
import org.broadleafcommerce.core.checkout.service.workflow.ValidateAndConfirmPaymentActivity
import org.broadleafcommerce.core.payment.domain.OrderPayment
import org.broadleafcommerce.core.payment.domain.OrderPaymentImpl
import org.broadleafcommerce.core.payment.domain.PaymentTransaction
import org.broadleafcommerce.core.payment.domain.PaymentTransactionImpl
import org.broadleafcommerce.core.payment.service.DefaultPaymentGatewayCheckoutService
import org.broadleafcommerce.core.payment.service.OrderPaymentService
import org.broadleafcommerce.core.payment.service.OrderPaymentStatusService
import org.broadleafcommerce.core.payment.service.OrderPaymentStatusServiceImpl
import org.broadleafcommerce.core.workflow.state.ActivityStateManagerImpl
import org.broadleafcommerce.core.workflow.state.RollbackStateLocal

/**
 * @author Elbert Bautista (elbertbautista)
 */
class ValidateAndConfirmPaymentActivitySpec extends BaseCheckoutActivitySpec {

    OrderPaymentStatusService statusService = new OrderPaymentStatusServiceImpl()

    OrderPayment confirmedCC = new OrderPaymentImpl()
    PaymentTransaction confirmedCCTransaction = new PaymentTransactionImpl()

    OrderPayment unconfirmedTP = new OrderPaymentImpl()
    PaymentTransaction unconfirmedTPTransaction = new PaymentTransactionImpl()

    OrderPayment unconfirmedCC = new OrderPaymentImpl()
    PaymentTransaction unconfirmedCCTransaction = new PaymentTransactionImpl()

    def setup() {
        def rollbackStateLocal = new RollbackStateLocal()
        rollbackStateLocal.setThreadId("SPOCK_THREAD")
        rollbackStateLocal.setWorkflowId("TEST")
        RollbackStateLocal.setRollbackStateLocal(rollbackStateLocal)

        new ActivityStateManagerImpl().init()

        confirmedCC.amount = new Money(10)
        confirmedCC.type = PaymentType.CREDIT_CARD
        confirmedCC.order = context.seedData.order

        confirmedCCTransaction.type = PaymentTransactionType.AUTHORIZE_AND_CAPTURE
        confirmedCCTransaction.amount = new Money(10)
        confirmedCCTransaction.success = true
        confirmedCCTransaction.orderPayment = confirmedCC

        confirmedCC.transactions << confirmedCCTransaction

        unconfirmedTP.amount = new Money(12)
        unconfirmedTP.type = PaymentType.THIRD_PARTY_ACCOUNT
        unconfirmedTP.order = context.seedData.order

        unconfirmedTPTransaction.type = PaymentTransactionType.UNCONFIRMED
        unconfirmedTPTransaction.amount = new Money(12)
        unconfirmedTPTransaction.success = true
        unconfirmedTPTransaction.orderPayment = unconfirmedTP

        unconfirmedTP.transactions << unconfirmedTPTransaction

        unconfirmedCC.amount = new Money(15)
        unconfirmedCC.type = PaymentType.CREDIT_CARD
        unconfirmedCC.order = context.seedData.order

        unconfirmedCCTransaction.type = PaymentTransactionType.UNCONFIRMED
        unconfirmedCCTransaction.amount = new Money(15)
        unconfirmedCCTransaction.success = true
        unconfirmedCCTransaction.orderPayment = unconfirmedCC

        unconfirmedCC.transactions << unconfirmedCCTransaction
    }

    def reset() {
        activity = new ValidateAndConfirmPaymentActivity().with {
            orderPaymentStatusService = statusService;
            it
        }
        context.seedData.order.payments = new ArrayList<OrderPayment>()
        context.seedData.order.total = null
    }

    def "Test Activity State Manager"() {
        setup: "I have one confirmed order payment on the order"
        reset()
        context.seedData.order.payments << confirmedCC
        context.seedData.order.total = new Money(10)

        when: "I execute the ValidateAndConfirmPaymentActivity"
        context = activity.execute(context)

        then: "There should be 1 state container for the Activity State Manager rollback thread"
        def containers = ActivityStateManagerImpl.stateManager.stateMap.get("SPOCK_THREAD_TEST")
        containers.size() == 1
    }

    def "Test validate payment sums against order total"() {
        setup: "I have one confirmed order payment on the order but the order total does not match"
        reset()
        context.seedData.order.payments << confirmedCC
        context.seedData.order.total = new Money(20)

        when: "I execute the ValidateAndConfirmPaymentActivity"
        context = activity.execute(context)

        then: "An IllegalArgumentException should be thrown stating that the sums don't add up"
        IllegalArgumentException ex = thrown()
        ex.message == "There are not enough payments to pay for the total order. The sum of " +
                "the payments is " + confirmedCCTransaction.amount.amount.toPlainString() + " and the order total is " + context.seedData.order.total.amount.toPlainString()

    }

    def "Test SUCCESSFULLY confirming all unconfirmed THIRD_PARTY_ACCOUNT transactions on the order"() {
        setup: "I have one unconfirmed THIRD_PARTY_ACCOUNT order payment transaction on the order"
        reset()
        context.seedData.order.payments << unconfirmedTP
        context.seedData.order.total = new Money(12)

        //Initiate Mocks
        PaymentResponseDTO responseDTO = new PaymentResponseDTO(PaymentType.THIRD_PARTY_ACCOUNT, PaymentGatewayType.PASSTHROUGH)
                .amount(new Money(12))
                .rawResponse("TEST")
                .successful(true)
                .paymentTransactionType(PaymentTransactionType.AUTHORIZE_AND_CAPTURE)

        OrderPaymentConfirmationStrategy mockStrategy = Mock()
        mockStrategy.confirmTransaction(*_) >> responseDTO

        OrderPaymentService mockOrderPaymentService = Mock()
        mockOrderPaymentService.createTransaction() >> new PaymentTransactionImpl()
        mockOrderPaymentService.save(_ as OrderPayment) >> {OrderPayment payment -> payment}
        mockOrderPaymentService.save(_ as PaymentTransaction) >> {PaymentTransaction transaction -> transaction}

        activity = new ValidateAndConfirmPaymentActivity().with {
            orderPaymentStatusService = statusService;
            orderPaymentConfirmationStrategy = mockStrategy
            orderPaymentService = mockOrderPaymentService
            it
        }

        when: "I execute the ValidateAndConfirmPaymentActivity"
        context = activity.execute(context)

        then: "The order should contain an OrderPayment with two THIRD_PARTY_ACCOUNT Transactions"
        context.seedData.order.payments.get(0).transactions.get(0).type == PaymentTransactionType.UNCONFIRMED
        context.seedData.order.payments.get(0).transactions.get(1).type == PaymentTransactionType.AUTHORIZE_AND_CAPTURE
        context.seedData.order.payments.get(0).transactions.get(1).success
        context.seedData.order.payments.get(0).transactions.get(1).parentTransaction == context.seedData.order.payments.get(0).transactions.get(0)
    }

    def "Test UNSUCCESSFULLY confirming all unconfirmed THIRD_PARTY_ACCOUNT transactions on the order"() {
        setup: "I have one unconfirmed THIRD_PARTY_ACCOUNT order payment transaction on the order"
        reset()
        context.seedData.order.payments << unconfirmedTP
        context.seedData.order.total = new Money(12)

        //Initiate Mocks
        PaymentResponseDTO responseDTO = new PaymentResponseDTO(PaymentType.THIRD_PARTY_ACCOUNT, PaymentGatewayType.PASSTHROUGH)
                .amount(new Money(12))
                .rawResponse("TEST")
                .successful(false)
                .paymentTransactionType(PaymentTransactionType.AUTHORIZE_AND_CAPTURE)

        OrderPaymentConfirmationStrategy mockStrategy = Mock()
        mockStrategy.confirmTransaction(*_) >> responseDTO

        OrderPaymentService mockOrderPaymentService = Mock()
        
        PaymentTransaction tx = new PaymentTransactionImpl().with {
            orderPayment = unconfirmedTP
            it
        }
        
        mockOrderPaymentService.createTransaction() >> {
            tx
        }
        mockOrderPaymentService.save(_ as OrderPayment) >> {OrderPayment payment -> payment}
        mockOrderPaymentService.save(_ as PaymentTransaction) >> {PaymentTransaction transaction -> transaction}
        mockOrderPaymentService.readPaymentById(_) >> {Long id -> unconfirmedTP }
        mockOrderPaymentService.readTransactionById(_) >> { Long id -> tx }
        
        DefaultPaymentGatewayCheckoutService mockCheckoutService = Stub()
        mockCheckoutService.orderPaymentService = mockOrderPaymentService

        activity = new ValidateAndConfirmPaymentActivity().with {
            orderPaymentStatusService = statusService;
            orderPaymentConfirmationStrategy = mockStrategy
            orderPaymentService = mockOrderPaymentService
            paymentGatewayCheckoutService = mockCheckoutService
            it
        }

        when: "I execute the ValidateAndConfirmPaymentActivity"
        context = activity.execute(context)

        then: "A CheckoutException should be thrown and the order should contain an OrderPayment with two THIRD_PARTY_ACCOUNT Transactions (one unsuccessful)"
        CheckoutException ex = thrown()
        ex.message == "Attempting to confirm/authorize an UNCONFIRMED transaction on the order was unsuccessful."
        context.seedData.order.payments.get(0).transactions.get(0).type == PaymentTransactionType.UNCONFIRMED
        context.seedData.order.payments.get(0).transactions.get(1).type == PaymentTransactionType.AUTHORIZE_AND_CAPTURE
        !context.seedData.order.payments.get(0).transactions.get(1).success
        context.seedData.order.payments.get(0).transactions.get(1).parentTransaction == context.seedData.order.payments.get(0).transactions.get(0)
    }

    def "Test SUCCESSFULLY confirming all unconfirmed CREDIT_CARD transactions on the order"() {
        setup: "I have one unconfirmed CREDIT_CARD order payment transaction on the order"
        reset()
        context.seedData.order.payments << unconfirmedCC
        context.seedData.order.total = new Money(15)

        //Initiate Mocks
        PaymentResponseDTO responseDTO = new PaymentResponseDTO(PaymentType.CREDIT_CARD, PaymentGatewayType.PASSTHROUGH)
                .amount(new Money(15))
                .rawResponse("TEST")
                .successful(true)
                .paymentTransactionType(PaymentTransactionType.AUTHORIZE_AND_CAPTURE)

        OrderPaymentConfirmationStrategy mockStrategy = Mock()
        mockStrategy.confirmTransaction(*_) >> responseDTO

        OrderPaymentService mockOrderPaymentService = Mock()
        mockOrderPaymentService.createTransaction() >> new PaymentTransactionImpl()
        mockOrderPaymentService.save(_ as OrderPayment) >> {OrderPayment payment -> payment}
        mockOrderPaymentService.save(_ as PaymentTransaction) >> {PaymentTransaction transaction -> transaction}

        activity = new ValidateAndConfirmPaymentActivity().with {
            orderPaymentStatusService = statusService;
            orderPaymentConfirmationStrategy = mockStrategy
            orderPaymentService = mockOrderPaymentService
            it
        }

        when: "I execute the ValidateAndConfirmPaymentActivity"
        context = activity.execute(context)

        then: "The order should contain an OrderPayment with two CREDIT_CARD Transactions"
        context.seedData.order.payments.get(0).transactions.get(0).type == PaymentTransactionType.UNCONFIRMED
        context.seedData.order.payments.get(0).transactions.get(1).type == PaymentTransactionType.AUTHORIZE_AND_CAPTURE
        context.seedData.order.payments.get(0).transactions.get(1).success
        context.seedData.order.payments.get(0).transactions.get(1).parentTransaction == context.seedData.order.payments.get(0).transactions.get(0)

    }

}
