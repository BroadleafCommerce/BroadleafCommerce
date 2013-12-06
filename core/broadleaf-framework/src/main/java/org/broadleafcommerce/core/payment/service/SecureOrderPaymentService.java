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
package org.broadleafcommerce.core.payment.service;

import org.broadleafcommerce.core.payment.domain.secure.Referenced;
import org.broadleafcommerce.core.payment.service.type.PaymentType;
import org.broadleafcommerce.core.workflow.WorkflowException;

public interface SecureOrderPaymentService {

    public Referenced findSecurePaymentInfo(String referenceNumber, PaymentType paymentType) throws WorkflowException;

    public Referenced save(Referenced securePayment);

    public Referenced create(PaymentType paymentType);

    public void remove(Referenced securePayment);

    public void findAndRemoveSecurePaymentInfo(String referenceNumber, PaymentType paymentType) throws WorkflowException;

}
