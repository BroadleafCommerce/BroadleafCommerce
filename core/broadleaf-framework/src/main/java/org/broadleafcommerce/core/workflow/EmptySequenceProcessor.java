/*
 * Copyright 2008-2013 the original author or authors.
 *
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
 */

package org.broadleafcommerce.core.workflow;

import org.broadleafcommerce.core.order.service.OrderService;


/**
 * Convenience class for creating an empty workflow. Useful when a user wants to remove workflow behavior from Broadleaf.
 * For instance, a user might want to subclass {@link OrderService} and provide their own implementation of addItem, but
 * wants to invoke the super implementation of this method to obtain all functionality <i>except</i> executing the workflow
 * since they want to take charge of the entire process themselves.
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
public class EmptySequenceProcessor extends SequenceProcessor {

    @Override
    protected ProcessContext createContext(Object seedData) {
        return null;
    }

}
