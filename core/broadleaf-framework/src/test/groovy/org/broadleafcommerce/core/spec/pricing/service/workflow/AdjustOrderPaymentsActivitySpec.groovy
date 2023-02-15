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
/**
 * @author Austin Rooke (austinrooke)
 */
package org.broadleafcommerce.core.spec.pricing.service.workflow

import org.broadleafcommerce.common.money.Money
import org.broadleafcommerce.common.payment.PaymentTransactionType
import org.broadleafcommerce.common.payment.PaymentType
import org.broadleafcommerce.common.persistence.ArchiveStatus
import org.broadleafcommerce.core.payment.domain.OrderPaymentImpl
import org.broadleafcommerce.core.payment.domain.PaymentTransactionImpl
import org.broadleafcommerce.core.pricing.service.workflow.AdjustOrderPaymentsActivity


class AdjustOrderPaymentsActivitySpec extends BasePricingActivitySpec {

    /*
     * This activity will need the following:
     *      Order
     *          payments
     *              OrderPayment
     *                  transactions
     *                      PaymentTransaction
     *                          type = PaymentTransactionType.UNCONFIRMED
     *                  confirmed = false
     *                  type = PaymentType.CREDIT_CARD || PaymentType.THIRD_PARTY_ACCOUNT
     *              OrderPayment
     *                  transactions
     *                      PaymentTransaction
     *                          type = PaymentTransactionType.AUTHORIZE_AND_CAPTURE
     *                  amount = Money
     *              OrderPayment
     *                  archiveStatus
     *                      ArchiveStatus
     *                          archived = 'Y'
     *          total = Money
     */

    def setup() {
        context.seedData.payments = [
            new OrderPaymentImpl().with() {
                transactions = [new PaymentTransactionImpl().with() {
                    type = PaymentTransactionType.UNCONFIRMED
                    it
                }
                ]
                type = PaymentType.CREDIT_CARD
                order = context.seedData
                it
            },
            new OrderPaymentImpl().with() {
                transactions = [new PaymentTransactionImpl().with() {
                    type = PaymentTransactionType.AUTHORIZE_AND_CAPTURE
                    it
                }
                ]
                amount = new Money('5.00')
                order = context.seedData
                it
            },
            new OrderPaymentImpl().with() {
                archiveStatus = new ArchiveStatus().with() {
                    archived = 'Y'
                    it
                }
                order = context.seedData
                it
            }
        ]
        context.seedData.total = new Money('20.00')
    }

    def "Test AdjustOrderPaymentActivitySpec with valid data"() {
        activity = new AdjustOrderPaymentsActivity()

        when: "I execute AdjustOrderPaymentsActivity"
        context = activity.execute(context)

        then: "The Order's new total should be 20.00 and the first OrderPayment in the order should have its amount set to 15.00"
        context.seedData.total.amount == 20.00
        context.seedData.payments[0].amount.amount == 15.00

    }
}
