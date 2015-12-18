/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.common.payment;

import org.broadleafcommerce.common.BroadleafEnumerationType;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The PaymentTransactionType is used to represent the types of operations/transactions that could occur against a single payment.
 * In the Broadleaf core framework, these types appear on the org.broadleafcommerce.core.payment.domain.PaymentTransaction.
 *
 * @see {@link #AUTHORIZE}
 * @see {@link #CAPTURE}
 * @see {@link #AUTHORIZE_AND_CAPTURE}
 * @see {@link #SETTLED}
 * @see {@link #REFUND}
 * @see {@link #DETACHED_CREDIT}
 * @see {@link #VOID}
 * @see {@link #REVERSE_AUTH}
 * @see {@link #UNCONFIRMED}
 * @see {@link #PENDING}
 *
 *  The following is a depiction of the possible state flows for an Order Payment and the
 *  hierarchical relationship of all its transactions:
 *
 * +-------------+
 * | UNCONFIRMED |
 * +-+-----------+
 *   |
 *   | +--------------------+
 *   +-+ PENDING (Optional) |
 *     +-+----------------+-+
 *       |                |
 *       | +-----------+  |                +-----------------------+
 *       +-+ AUTHORIZE |  +----------------+ AUTHORIZE_AND_CAPTURE |
 *         +-+---------+                   +-+---------------------+
 *           |                             |
 *           | +-------------------+       | +------+
 *           +-+ REVERSE_AUTHORIZE |       +-+ VOID |
 *           | +-------------------+       | +------+
 *           |                             |
 *           | +---------+                 | +--------------------+
 *           +-+ CAPTURE |                 +-+ SETTLED (Optional) |
 *             +-+-------+                   +-+------------------+
 *               |                             |
 *               | +------+                    | +--------+
 *               +-+ VOID |                    +-+ REFUND |
 *               | +------+                      +--------+
 *               |
 *               | +--------------------+
 *               +-+ SETTLED (Optional) |
 *                 +-+------------------+
 *                   |
 *                   | +--------+
 *                   +-+ REFUND |
 *                     +--------+
 *
 * +-------------+
 * | UNCONFIRMED |
 * +-+-----------+
 *   |
 *   | +-----------------+
 *   +-+ DETACHED_CREDIT |
 *     +-+---------------+
 *
 * @author Jerry Ocanas (jocanas)
 * @author Phillip Verheyden (phillipuniverse)
 * @author Elbert Bautista (elbertbautista)
 */
public class PaymentTransactionType implements Serializable, BroadleafEnumerationType {

    private static final long serialVersionUID = 1L;

    private static final Map<String, PaymentTransactionType> TYPES = new LinkedHashMap<String, PaymentTransactionType>();

    /**
     * Funds have been authorized for capture. This might appear as a 'pending' transaction on a customer's credit
     * card statement
     */
    public static final PaymentTransactionType AUTHORIZE = new PaymentTransactionType("AUTHORIZE", "Authorize");
    
    /**
     * Funds have been charged/submitted/debited from the customer and payment is complete. Can <b>ONLY</b> occur after an
     * amount has ben {@link #AUTHORIZE}d.
     */
    public static final PaymentTransactionType CAPTURE = new PaymentTransactionType("CAPTURE", "Capture");
    
    /**
     * <p>Funds have been captured/authorized all at once. While this might be the simplest to
     * implement from an order management perspective, the recommended approach is to {@link #AUTHORIZE} and then {@link #CAPTURE}
     * in separate transactions and at separate times. For instance, an {@link AUTHORIZE} would happen once the {@link Order}
     * has completed checkout but then a {@link CAPTURE} would happen once the {@link Order} has shipped.</p>
     *
     * <p>NOTE: Many Gateways like to refer to this as also a SALE transaction.</p>
     * 
     * <p>This should be treated the exact same as a {@link #CAPTURE}.</p>
     */
    public static final PaymentTransactionType AUTHORIZE_AND_CAPTURE = new PaymentTransactionType("AUTHORIZE_AND_CAPTURE", "Authorize and Capture");
   
    /**
     * Can <b>ONLY</b> occur after a payment has been {@link #CAPTURE}d. This represents a payment that has been balanced by
     * the payment provider. This represents more finality than a {@link #CAPTURE}. Some payment providers might not explicitly
     * expose the details of settled transactions which are usually done in batches at the end of the day.
     */
    public static final PaymentTransactionType SETTLED = new PaymentTransactionType("SETTLED", "Settled");
    
    /**
     * <p>Funds have been refunded/credited. This can <b>ONLY</b> occur after funds have been {@link #CAPTURE}d or
     * {@link #SETTLED}. This should only be used when money goes back to a customer. This assumes that
     * there will be a parent {@link #AUTHORIZE_AND_CAPTURE}, {@link #CAPTURE}, or {@link #SETTLED} transaction
     * that this can be tied back to.</p>
     *
     * <p>NOTE: This can also be referred to as a "follow-on credit"</p>
     */
    public static final PaymentTransactionType REFUND = new PaymentTransactionType("REFUND", "Refund");

    /**
     * <p>Some payment processors allow you to issue credit to a customer that is not tied
     * to an initial {@link #AUTHORIZE} or {@link #AUTHORIZE_AND_CAPTURE} transaction.
     * Most payment gateways disable this feature by default because it is against
     * card association (e.g. Visa, MasterCard) rules. However, there may be legitimate instances
     * where you had a sale transaction but are not able to issue a refund (e.g. closed account of original payment etc...)
     * Please contact your payment gateway provider to see how to enable this feature.</p>
     *
     * <p>NOTE: This can also be referred to as a "blind credit" or "stand-alone credit"</p>
     */
    public static final PaymentTransactionType DETACHED_CREDIT = new PaymentTransactionType("DETACHED_CREDIT", "Detached Credit");
    
    /**
     * <p>Void can happen after a CAPTURE but before it has been SETTLED. Payment transactions are usually settled in batches
     * at the end of the day.</p>
     */
    public static final PaymentTransactionType VOID = new PaymentTransactionType("VOID", "Void");
    
    /**
     * The reverse of {@link #AUTHORIZE}. This can <b>ONLY</b> occur <b>AFTER</b> funds have been
     * {@link #AUTHORIZE}d but <b>BEFORE</b> funds have been {@link #CAPTURE}d.
     */
    public static final PaymentTransactionType REVERSE_AUTH = new PaymentTransactionType("REVERSE_AUTH", "Reverse Auth");

    /**
     * <p>This applies to payment types like "PayPal Express Checkout" and Credit Card tokens/nonce
     * where a transaction must be confirmed at a later stage.
     * A payment is considered "confirmed" if the gateway has actually processed a transaction against this user's card/account.
     * There might be instances where payments have not been confirmed at the moment it has been added to the order.
     *
     * For example, there might be a scenario where it is desirable to show a 'review confirmation' page to the user before actually
     * hitting 'submit' and completing the checkout workflow (this is also the desired case
     * with gift cards and account credits).</p>
     * 
     * <p>It is important to note that all "UNCONFIRMED" transactions will be confirmed in the checkout workflow via the
     * {@link ValidateAndConfirmPaymentActivity}. That means that any unconfirmed CREDIT_CARD transactions will be
     * "Authorized" or "Authorized and Captured" at time of checkout. If the Order Payment is of any other type, then the activity
     * will attempt to call the gateways implementation of:
     * {@link org.broadleafcommerce.common.payment.service.PaymentGatewayTransactionConfirmationService#confirmTransaction(org.broadleafcommerce.common.payment.dto.PaymentRequestDTO)}</p>
     */
    public static final PaymentTransactionType UNCONFIRMED = new PaymentTransactionType("UNCONFIRMED", "Not Confirmed");

    /**
     * <p>Some implementations may wish to defer any Authorization or Authorize and Capture transactions outside
     * the scope of the checkout workflow. For example, some may wish to take all orders up front (possibly
     * just doing AVS and CVV checks during checkout) and opt to process the users card offline or asynchronously
     * through some other external mechanism or process. In this scenario, you may create an Order Payment with
     * a transaction that "marks" it with the intention of being processed later. This allows the
     * {@link ValidateAndConfirmPaymentActivity} to correctly compare the equality of all the successful payments on the order
     * against the order total.</p>
     *
     * <p>NOTE: This differs from {@link #UNCONFIRMED} because at the time of checkout,
     * the checkout workflow will try to AUTH or SALE any UNCONFIRMED transactions on all the payments.</p>
     */
    public static final PaymentTransactionType PENDING = new PaymentTransactionType("PENDING", "Pending Authorize or Authorize and Capture");


    public static PaymentTransactionType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;

    public PaymentTransactionType() {
        // do nothing
    }

    public PaymentTransactionType(String type, String friendlyType) {
        this.friendlyType = friendlyType;
        setType(type);
    }

   @Override
     public String getType() {
        return type;
    }

   @Override
     public String getFriendlyType() {
        return friendlyType;
    }

    private void setType(final String type) {
        this.type = type;
        if (!TYPES.containsKey(type)){
            TYPES.put(type, this);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!getClass().isAssignableFrom(obj.getClass()))
            return false;
        PaymentTransactionType other = (PaymentTransactionType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
}
