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

import org.broadleafcommerce.core.payment.domain.Referenced;
import org.broadleafcommerce.core.payment.service.type.PaymentInfoType;
import org.broadleafcommerce.core.workflow.WorkflowException;

public interface SecurePaymentInfoService {

    public Referenced findSecurePaymentInfo(String referenceNumber, PaymentInfoType paymentInfoType) throws WorkflowException;

    public Referenced save(Referenced securePaymentInfo);

    public Referenced create(PaymentInfoType paymentInfoType);

    public void remove(Referenced securePaymentInfo);

    public void findAndRemoveSecurePaymentInfo(String referenceNumber, PaymentInfoType paymentInfoType) throws WorkflowException;

}
