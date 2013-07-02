/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.order.service.workflow;

import org.broadleafcommerce.core.workflow.DefaultProcessContextImpl;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.broadleafcommerce.core.workflow.ProcessContextFactory;
import org.broadleafcommerce.core.workflow.WorkflowException;

/**
 * Provides a method that creates the seed ProcessContext object for a cart
 * operation. The same seed object is used for add item, update item, and remove
 * item cart operations.
 * 
 * @author apazzolini
 */
public class CartOperationProcessContextFactory implements ProcessContextFactory<CartOperationRequest, CartOperationRequest> {

    /**
     * Creates the necessary context for cart operations
     */
    public ProcessContext<CartOperationRequest> createContext(CartOperationRequest seedData) throws WorkflowException {
        if (!(seedData instanceof CartOperationRequest)){
            throw new WorkflowException("Seed data instance is incorrect. " +
                    "Required class is " + CartOperationRequest.class.getName() + " " +
                    "but found class: " + seedData.getClass().getName());
        }
        
        ProcessContext<CartOperationRequest> context = new DefaultProcessContextImpl<CartOperationRequest>();
        context.setSeedData((CartOperationRequest) seedData);
        return context;
    }

}
