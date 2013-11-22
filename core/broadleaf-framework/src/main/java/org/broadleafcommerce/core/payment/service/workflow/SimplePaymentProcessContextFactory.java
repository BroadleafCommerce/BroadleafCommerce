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
package org.broadleafcommerce.core.payment.service.workflow;

import org.broadleafcommerce.core.workflow.DefaultProcessContextImpl;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.broadleafcommerce.core.workflow.ProcessContextFactory;
import org.broadleafcommerce.core.workflow.WorkflowException;

public class SimplePaymentProcessContextFactory implements ProcessContextFactory<PaymentSeed, PaymentSeed> {

    public ProcessContext<PaymentSeed> createContext(PaymentSeed seedData) throws WorkflowException {
        if(!(seedData instanceof PaymentSeed)){
            throw new WorkflowException("Seed data instance is incorrect. " +
                    "Required class is "+PaymentSeed.class.getName()+" " +
                    "but found class: "+seedData.getClass().getName());
        }

        ProcessContext<PaymentSeed> response = new DefaultProcessContextImpl<PaymentSeed>();
        response.setSeedData(seedData);

        return response;
    }

}
