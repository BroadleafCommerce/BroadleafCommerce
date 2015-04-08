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

package org.broadleafcommerce.core.payment.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.payment.PaymentAdditionalFieldType;
import org.broadleafcommerce.common.payment.PaymentTransactionType;
import org.broadleafcommerce.common.persistence.Status;
import org.broadleafcommerce.profile.core.domain.AdditionalFields;
import org.broadleafcommerce.profile.core.domain.Customer;

/**
 * <p>Used to store individual transactions about a particular payment. While an {@link OrderPayment} holds data like what the
 * user might be paying with and the total amount they will be paying (like credit card and $10), a {@link PaymentTransaction}
 * is more about what happens with that particular payment. Thus, {@link PaymentTransaction}s do not make sense by
 * themselves and ONLY make sense in the context of an {@link OrderPayment}.</p>
 * 
 * <p>For instance, the user might say they want to pay $10 but rather than capture the payment at order checkout, there
 * might first be a transaction for {@link PaymentTransactionType#AUTHORIZE} and then when the item is shipped there is
 * another {@link PaymentTransaction} for {@link PaymentTransactionType#CAPTURE}.</p>
 * 
 * <p>In the above case, this also implies that a {@link PaymentTransaction} can have a <b>parent transaction</b> (retrieved
 * via {@link #getParentTransaction()}). The parent transaction will only be set in the following cases:</p>
 * 
 * <ul>
 *  <li>{@link PaymentTransactionType#CAPTURE} -> {@link PaymentTransactionType#AUTHORIZE}</li>
 *  <li>{@link PaymentTransactionType#REFUND} -> {@link PaymentTransactionType#CAPTURE} OR {@link PaymentTransactionType#SETTLED}</li>
 *  <li>{@link PaymentTransactionType#SETTLED} -> {@link PaymentTransactionType#CAPTURE}</li>
 *  <li>{@link PaymentTransactionType#VOID} -> {@link PaymentTransactionType#CAPTURE}</li>
 *  <li>{@link PaymentTransactionType#REVERSE_AUTH} -> {@link PaymentTransactionType#AUTHORIZE}</li>
 * </ul>
 * 
 * <p>For {@link PaymentTransactionType#UNCONFIRMED}, they will have children that will be either {@link PaymentTransactionType#AUTHORIZE}
 * or {@link PaymentTransactionType#AUTHORIZE_AND_CAPTURE}.</p> * 
 * @author Phillip Verheyden (phillipuniverse)
 */
public interface PaymentTransaction extends Serializable, Status, AdditionalFields {

    public Long getId();

    public void setId(Long id);

    /**
     * The overall payment that this transaction applies to. Note that if the relationship to an order payment is unset on
     * this particular transaction, then this will automatically attempt to obtain the {@link OrderPayment} from
     * {@link #getParentTransaction()}.
     */
    public OrderPayment getOrderPayment();

    /**
     * Sets the overall payment that this transaction applies to
     */
    public void setOrderPayment(OrderPayment payment);

    /**
     * Transactions can have a parent-child relationship for modifying transactions that can occur. Examples of this:
     * <ul>
     *  <li>{@link PaymentTransactionType#CAPTURE} -> {@link PaymentTransactionType#AUTHORIZE}</li>
     *  <li>{@link PaymentTransactionType#REFUND} -> {@link PaymentTransactionType#CAPTURE} OR {@link PaymentTransactionType#SETTLED}</li>
     *  <li>{@link PaymentTransactionType#SETTLED} -> {@link PaymentTransactionType#CAPTURE}</li>
     *  <li>{@link PaymentTransactionType#VOID} -> {@link PaymentTransactionType#CAPTURE}</li>
     *  <li>{@link PaymentTransactionType#REVERSE_AUTH} -> {@link PaymentTransactionType#AUTHORIZE}</li>
     * </ul>
     * 
     * <p>For {@link PaymentTransactionType#UNCONFIRMED}, they will have children that will be either {@link PaymentTransactionType#AUTHORIZE}
     * or {@link PaymentTransactionType#AUTHORIZE_AND_CAPTURE}.</p>
     * 
     * @return
     */
    public PaymentTransaction getParentTransaction();

    public void setParentTransaction(PaymentTransaction parentTransaction);

    /**
     * The type of 
     * @return
     */
    public PaymentTransactionType getType();

    public void setType(PaymentTransactionType type);

    /**
     * Gets the amount that this transaction is for
     */
    public Money getAmount();

    /**
     * Sets the amount of this transaction
     */
    public void setAmount(Money amount);

    /**
     * Gets the date that this transaction was made on
     */
    public Date getDate();

    /**
     * Sets the date that this transaction was made on
     */
    public void setDate(Date date);

    /**
     * Gets the {@link Customer} IP address that instigated this transaction. This is an optional field
     */
    public String getCustomerIpAddress();

    /**
     * Sets the {@link Customer} IP address that instigated the transaction. This is an optional field.
     */
    public void setCustomerIpAddress(String customerIpAddress);

    /**
     * Gets the string-representation of the serialized response from the gateway. This is usually the complete request
     * parameter map serialized in string form.
     */
    public String getRawResponse();

    /**
     * Sets the raw response that was returned from the gateway.
     */
    public void setRawResponse(String rawResponse);

    /**
     * Gets whether or not this transaction was successful. There are multiple reasons that a transaction could be
     * unsuccessful such as failed credit card processing or any other errors from the gateway.
     */
    public Boolean getSuccess();

    public void setSuccess(Boolean success);

    /**
     * @see {@link PaymentAdditionalFieldType}
     */
    public Map<String, String> getAdditionalFields();

    public void setAdditionalFields(Map<String, String> additionalFields);

}
