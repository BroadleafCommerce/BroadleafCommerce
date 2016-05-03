/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.spec.payment.service

import org.broadleafcommerce.common.currency.domain.BroadleafCurrency
import org.broadleafcommerce.common.currency.domain.BroadleafCurrencyImpl
import org.broadleafcommerce.common.money.Money
import org.broadleafcommerce.common.payment.PaymentTransactionType
import org.broadleafcommerce.core.order.domain.Order
import org.broadleafcommerce.core.order.domain.OrderImpl
import org.broadleafcommerce.core.payment.domain.OrderPayment
import org.broadleafcommerce.core.payment.domain.OrderPaymentImpl
import org.broadleafcommerce.core.payment.domain.PaymentTransaction
import org.broadleafcommerce.core.payment.domain.PaymentTransactionImpl
import org.broadleafcommerce.core.payment.service.OrderPaymentStatusService
import org.broadleafcommerce.core.payment.service.OrderPaymentStatusServiceImpl
import org.broadleafcommerce.core.payment.service.type.OrderPaymentStatus
import spock.lang.Specification

/**
 * @author Elbert Bautista (elbertbautista)
 */
class OrderPaymentStatusServiceSpec extends Specification {

    OrderPaymentStatusService statusService
    Order order
    OrderPayment payment

    PaymentTransaction unconfirmedTX
    PaymentTransaction pendingTX
    PaymentTransaction authTX
    PaymentTransaction reverseAuthTX
    PaymentTransaction saleTX
    PaymentTransaction capture1TX
    PaymentTransaction capture2TX
    PaymentTransaction voidSaleTX
    PaymentTransaction voidCapture1Tx
    PaymentTransaction voidCapture2Tx
    PaymentTransaction refundSaleTx
    PaymentTransaction refundCapture1TX
    PaymentTransaction refundCapture2TX
    PaymentTransaction settledTX
    PaymentTransaction detachedTX

    def setup() {
        statusService = new OrderPaymentStatusServiceImpl()

        BroadleafCurrency usd = new BroadleafCurrencyImpl()
        usd.currencyCode = "USD";
        order = new OrderImpl()
        order.currency = usd
        payment = new OrderPaymentImpl()
        payment.order = order
        payment.amount = new Money(10)

        //Set up global transactions
        unconfirmedTX = new PaymentTransactionImpl()
        unconfirmedTX.type = PaymentTransactionType.UNCONFIRMED
        unconfirmedTX.amount = new Money(10)
        unconfirmedTX.orderPayment = payment

        pendingTX = new PaymentTransactionImpl()
        pendingTX.type = PaymentTransactionType.PENDING
        pendingTX.amount = new Money(10)
        pendingTX.orderPayment = payment

        authTX = new PaymentTransactionImpl()
        authTX.type = PaymentTransactionType.AUTHORIZE
        authTX.amount = new Money(10)
        authTX.orderPayment = payment

        saleTX = new PaymentTransactionImpl()
        saleTX.type = PaymentTransactionType.AUTHORIZE_AND_CAPTURE
        saleTX.amount = new Money(10)
        saleTX.orderPayment = payment

        reverseAuthTX = new PaymentTransactionImpl()
        reverseAuthTX.type = PaymentTransactionType.REVERSE_AUTH
        reverseAuthTX.amount = new Money(10)
        reverseAuthTX.orderPayment = payment

        capture1TX = new PaymentTransactionImpl()
        capture1TX.type = PaymentTransactionType.CAPTURE
        capture1TX.amount = new Money(5)
        capture1TX.orderPayment = payment

        capture2TX = new PaymentTransactionImpl()
        capture2TX.type = PaymentTransactionType.CAPTURE
        capture2TX.amount = new Money(5)
        capture2TX.orderPayment = payment

        voidSaleTX = new PaymentTransactionImpl()
        voidSaleTX.type = PaymentTransactionType.VOID
        voidSaleTX.amount = new Money(10)
        voidSaleTX.orderPayment = payment

        voidCapture1Tx = new PaymentTransactionImpl()
        voidCapture1Tx.type = PaymentTransactionType.VOID
        voidCapture1Tx.amount = new Money(5)
        voidCapture1Tx.orderPayment = payment

        voidCapture2Tx = new PaymentTransactionImpl()
        voidCapture2Tx.type = PaymentTransactionType.VOID
        voidCapture2Tx.amount = new Money(5)
        voidCapture2Tx.orderPayment = payment

        refundSaleTx = new PaymentTransactionImpl()
        refundSaleTx.type = PaymentTransactionType.REFUND
        refundSaleTx.amount = new Money(10)
        refundSaleTx.orderPayment = payment

        refundCapture1TX = new PaymentTransactionImpl()
        refundCapture1TX.type = PaymentTransactionType.REFUND
        refundCapture1TX.amount = new Money(5)
        refundCapture1TX.orderPayment = payment

        refundCapture2TX = new PaymentTransactionImpl()
        refundCapture2TX.type = PaymentTransactionType.REFUND
        refundCapture2TX.amount = new Money(5)
        refundCapture2TX.orderPayment = payment

        settledTX = new PaymentTransactionImpl()
        settledTX.type = PaymentTransactionType.SETTLED
        settledTX.amount = new Money(10)
        settledTX.orderPayment = payment

        detachedTX = new PaymentTransactionImpl()
        detachedTX.type = PaymentTransactionType.DETACHED_CREDIT
        detachedTX.amount = new Money(10)
        detachedTX.orderPayment = payment
    }

    def reset() {
        payment.getTransactions().clear()
    }

    def "Test OrderPaymentStatus.COMPLETE with a REVERSE_AUTH"() {
        setup: "I have an Order Payment with a REVERSE_AUTH transaction"
        reset()
        payment.transactions << unconfirmedTX
        payment.transactions << authTX
        payment.transactions << reverseAuthTX

        when: "I execute the order payment status service"
        OrderPaymentStatus status = statusService.determineOrderPaymentStatus(payment);

        then: "The OrderPaymentStatus should be COMPLETE"
        status == OrderPaymentStatus.COMPLETE
    }

    def "Test OrderPaymentStatus.COMPLETE with a DETACHED_CREDIT"() {
        setup: "I have an Order Payment with a DETACHED_CREDIT transaction"
        reset()
        payment.transactions << detachedTX

        when: "I execute the order payment status service"
        OrderPaymentStatus status = statusService.determineOrderPaymentStatus(payment);

        then: "The OrderPaymentStatus should be COMPLETE"
        status == OrderPaymentStatus.COMPLETE
    }

    def "Test OrderPaymentStatus.COMPLETE with a full VOID on a SALE"() {
        setup: "I have an Order Payment with a full VOID transaction"
        reset()
        payment.transactions << saleTX
        payment.transactions << voidSaleTX

        when: "I execute the order payment status service"
        OrderPaymentStatus status = statusService.determineOrderPaymentStatus(payment);

        then: "The OrderPaymentStatus should be COMPLETE"
        status == OrderPaymentStatus.COMPLETE
    }

    def "Test OrderPaymentStatus.COMPLETE with a full REFUND on a SALE"() {
        setup: "I have an Order Payment with a full REFUND transaction"
        reset()
        payment.transactions << saleTX
        payment.transactions << settledTX
        payment.transactions << refundSaleTx

        when: "I execute the order payment status service"
        OrderPaymentStatus status = statusService.determineOrderPaymentStatus(payment);

        then: "The OrderPaymentStatus should be COMPLETE"
        status == OrderPaymentStatus.COMPLETE
    }

    def "Test OrderPaymentStatus.COMPLETE with a full VOID on partial captures"() {
        setup: "I have an Order Payment with a full VOID on partial captures"
        reset()
        payment.transactions << authTX
        payment.transactions << capture1TX
        payment.transactions << voidCapture1Tx
        payment.transactions << capture2TX
        payment.transactions << voidCapture2Tx

        when: "I execute the order payment status service"
        OrderPaymentStatus status = statusService.determineOrderPaymentStatus(payment);

        then: "The OrderPaymentStatus should be COMPLETE"
        status == OrderPaymentStatus.COMPLETE
    }

    def "Test OrderPaymentStatus.COMPLETE with a full REFUND on partial captures"() {
        setup: "I have an Order Payment with a full REFUND on partial captures"
        reset()
        payment.transactions << authTX
        payment.transactions << capture1TX
        payment.transactions << refundCapture1TX
        payment.transactions << capture2TX
        payment.transactions << refundCapture2TX

        when: "I execute the order payment status service"
        OrderPaymentStatus status = statusService.determineOrderPaymentStatus(payment);

        then: "The OrderPaymentStatus should be COMPLETE"
        status == OrderPaymentStatus.COMPLETE
    }

    def "Test OrderPaymentStatus.PARTIALLY_COMPLETE with a partial VOID on partial captures"() {
        setup: "I have an Order Payment with a partial VOID on partial captures"
        reset()
        payment.transactions << authTX
        payment.transactions << capture1TX
        payment.transactions << voidCapture1Tx
        payment.transactions << capture2TX

        when: "I execute the order payment status service"
        OrderPaymentStatus status = statusService.determineOrderPaymentStatus(payment);

        then: "The OrderPaymentStatus should be PARTIALLY_COMPLETE"
        status == OrderPaymentStatus.PARTIALLY_COMPLETE
    }

    def "Test OrderPaymentStatus.PARTIALLY_COMPLETE on multiple captures"() {
        setup: "I have an Order Payment with a partial refund"
        reset()
        payment.transactions << authTX
        payment.transactions << capture1TX
        payment.transactions << refundCapture1TX
        payment.transactions << capture2TX

        when: "I execute the order payment status service"
        OrderPaymentStatus status = statusService.determineOrderPaymentStatus(payment);

        then: "The OrderPaymentStatus should be PARTIALLY_COMPLETE"
        status == OrderPaymentStatus.PARTIALLY_COMPLETE
    }

    def "Test OrderPaymentStatus.PARTIALLY_COMPLETE on a single capture and refund"() {
        setup: "I have an Order Payment with a single capture and refund"
        reset()
        payment.transactions << authTX
        payment.transactions << capture1TX
        payment.transactions << refundCapture1TX

        when: "I execute the order payment status service"
        OrderPaymentStatus status = statusService.determineOrderPaymentStatus(payment);

        then: "The OrderPaymentStatus should be PARTIALLY_COMPLETE"
        status == OrderPaymentStatus.PARTIALLY_COMPLETE
    }

    def "Test OrderPaymentStatus.PARTIALLY_COMPLETE on an AUTH"() {
        setup: "I have an Order Payment with a partial capture"
        reset()
        payment.transactions << unconfirmedTX
        payment.transactions << authTX
        payment.transactions << capture1TX

        when: "I execute the order payment status service"
        OrderPaymentStatus status = statusService.determineOrderPaymentStatus(payment);

        then: "The OrderPaymentStatus should be PARTIALLY_COMPLETE"
        status == OrderPaymentStatus.PARTIALLY_COMPLETE
    }

    def "Test OrderPaymentStatus.FULLY_CAPTURED on a SALE"() {
        setup: "I have an Order Payment with a sale transaction"
        reset()
        payment.transactions << unconfirmedTX
        payment.transactions << saleTX

        when: "I execute the order payment status service"
        OrderPaymentStatus status = statusService.determineOrderPaymentStatus(payment);

        then: "The OrderPaymentStatus should be FULLY_CAPTURED"
        status == OrderPaymentStatus.FULLY_CAPTURED
    }

    def "Test OrderPaymentStatus.FULLY_CAPTURED on multiple captures"() {
        setup: "I have an Order Payment with multiple captures"
        reset()
        payment.transactions << unconfirmedTX
        payment.transactions << authTX
        payment.transactions << capture1TX
        payment.transactions << capture2TX

        when: "I execute the order payment status service"
        OrderPaymentStatus status = statusService.determineOrderPaymentStatus(payment);

        then: "The OrderPaymentStatus should be FULLY_CAPTURED"
        status == OrderPaymentStatus.FULLY_CAPTURED
    }

    def "Test OrderPaymentStatus.AUTHORIZED on an AUTH"() {
        setup: "I have an Order Payment with an AUTH transaction"
        reset()
        payment.transactions << unconfirmedTX
        payment.transactions << authTX

        when: "I execute the order payment status service"
        OrderPaymentStatus status = statusService.determineOrderPaymentStatus(payment);

        then: "The OrderPaymentStatus should be AUTHORIZED"
        status == OrderPaymentStatus.AUTHORIZED
    }

    def "Test OrderPaymentStatus.AUTHORIZED on a PENDING then AUTH"() {
        setup: "I have an Order Payment with a PENDING then AUTH transaction"
        reset()
        payment.transactions << unconfirmedTX
        payment.transactions << pendingTX
        payment.transactions << authTX

        when: "I execute the order payment status service"
        OrderPaymentStatus status = statusService.determineOrderPaymentStatus(payment);

        then: "The OrderPaymentStatus should be AUTHORIZED"
        status == OrderPaymentStatus.AUTHORIZED
    }

    def "Test OrderPaymentStatus.PENDING on an UNCONFIRMED"() {
        setup: "I have an Order Payment with a PENDING transaction"
        reset()
        payment.transactions << unconfirmedTX
        payment.transactions << pendingTX

        when: "I execute the order payment status service"
        OrderPaymentStatus status = statusService.determineOrderPaymentStatus(payment);

        then: "The OrderPaymentStatus should be PENDING"
        status == OrderPaymentStatus.PENDING
    }

    def "Test OrderPaymentStatus.UNCONFIRMED on an UNCONFIRMED"() {
        setup: "I have an Order Payment with a UNCONFIRMED transaction"
        reset()
        payment.transactions << unconfirmedTX

        when: "I execute the order payment status service"
        OrderPaymentStatus status = statusService.determineOrderPaymentStatus(payment);

        then: "The OrderPaymentStatus should be UNCONFIRMED"
        status == OrderPaymentStatus.UNCONFIRMED
    }

    def "Test OrderPaymentStatus.UNDETERMINED on an Invalid Transaction set"() {
        setup: "I have an Order Payment with invalid transactions"
        reset()
        payment.transactions << unconfirmedTX
        payment.transactions << refundCapture1TX

        when: "I execute the order payment status service"
        OrderPaymentStatus status = statusService.determineOrderPaymentStatus(payment);

        then: "The OrderPaymentStatus should be UNDETERMINED"
        status == OrderPaymentStatus.UNDETERMINED
    }
}
