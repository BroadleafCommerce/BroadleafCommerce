/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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
package org.broadleafcommerce.core.payment.service.type;

import org.broadleafcommerce.common.BroadleafEnumerationType;
import java.io.Serializable;
import java.util.LinkedHashMap;

/**
 * Used to determine the status of an {@link org.broadleafcommerce.core.payment.domain.OrderPayment}
 * which is calculated based on the state of its containing
 * {@link org.broadleafcommerce.core.payment.domain.PaymentTransaction}s
 *
 * @author Elbert Bautista (elbertbautista)
 */
public class OrderPaymentStatus implements Serializable, BroadleafEnumerationType {

    private static final long serialVersionUID = 1L;

    private static final LinkedHashMap<String, OrderPaymentStatus> TYPES = new LinkedHashMap<String, OrderPaymentStatus>();

    /**
     * This is the default status for an Order Payment and is meant to encompass a state not
     * represented by the other payment statuses.
     */
    public static final OrderPaymentStatus UNDETERMINED = new OrderPaymentStatus("UNDETERMINED", "Undetermined");

    /**
     * This represents the state where there is only a {@link org.broadleafcommerce.common.payment.PaymentTransactionType#UNCONFIRMED}
     * transaction on the payment.
     */
    public static final OrderPaymentStatus UNCONFIRMED = new OrderPaymentStatus("UNCONFIRMED", "Unconfirmed Transaction");

    /**
     * This represents the state where there is a {@link org.broadleafcommerce.common.payment.PaymentTransactionType#PENDING}
     * transaction on the payment, but there is not yet a completed {@link org.broadleafcommerce.common.payment.PaymentTransactionType#AUTHORIZE}
     * or {@link org.broadleafcommerce.common.payment.PaymentTransactionType#AUTHORIZE_AND_CAPTURE} transaction.
     */
    public static final OrderPaymentStatus PENDING = new OrderPaymentStatus("PENDING", "Pending Charge");

    /**
     * This is equivalent to having a successful {@link org.broadleafcommerce.common.payment.PaymentTransactionType#AUTHORIZE}
     * transaction on the payment, but there are no transactions indicating that payment has been captured.
     */
    public static final OrderPaymentStatus AUTHORIZED = new OrderPaymentStatus("AUTHORIZED", "Authorized");

    /**
     * This is equivalent to having a successful {@link org.broadleafcommerce.common.payment.PaymentTransactionType#AUTHORIZE_AND_CAPTURE}
     * transaction on the payment OR all the partial {@link org.broadleafcommerce.common.payment.PaymentTransactionType#CAPTURE}
     * transaction amounts equal the original order payment transaction,
     * but there are no transactions indicating that payment has had any refunds issued against it.
     */
    public static final OrderPaymentStatus FULLY_CAPTURED = new OrderPaymentStatus("FULLY_CAPTURED", "Fully Captured");

    /**
     * This is equivalent to having a successful {@link org.broadleafcommerce.common.payment.PaymentTransactionType#AUTHORIZE_AND_CAPTURE}
     * OR one or more {@link org.broadleafcommerce.common.payment.PaymentTransactionType#CAPTURE} transactions which
     * may have zero or more refund transactions issued against it.
     */
    public static final OrderPaymentStatus PARTIALLY_COMPLETE = new OrderPaymentStatus("PARTIALLY_COMPLETE", "Partially Complete");

    /**
     * This represents a completed state for this order payment wherein no more action can be performed on the original transaction.
     * Specifically, if the transaction log contains a successful {@link org.broadleafcommerce.common.payment.PaymentTransactionType#REVERSE_AUTH},
     * {@link org.broadleafcommerce.common.payment.PaymentTransactionType#VOID}, {@link org.broadleafcommerce.common.payment.PaymentTransactionType#DETACHED_CREDIT}
     * or the total transaction amount is equal to all the refund transactions.
     */
    public static final OrderPaymentStatus COMPLETE = new OrderPaymentStatus("Complete", "Complete");

    public static OrderPaymentStatus getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;

    public OrderPaymentStatus() {
        //do nothing
    }

    public OrderPaymentStatus(final String type, final String friendlyType) {
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
        if (!TYPES.containsKey(type)) {
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
        OrderPaymentStatus other = (OrderPaymentStatus) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

}
