/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
/**
 * @author Austin Rooke (austinrooke)
 */
package org.broadleafcommerce.core.spec.order.service.workflow.add

import org.broadleafcommerce.core.order.service.workflow.CartOperationRequest
import org.broadleafcommerce.core.workflow.BaseActivity
import org.broadleafcommerce.core.workflow.DefaultProcessContextImpl
import org.broadleafcommerce.core.workflow.ProcessContext

import spock.lang.Specification


class BaseAddItemActivitySpec extends Specification {

    BaseActivity<ProcessContext<CartOperationRequest>> activity
    ProcessContext<CartOperationRequest> context
    def setup() {
        context = new DefaultProcessContextImpl<CartOperationRequest>().with() {
            //Create valid CartOperationRequest
            seedData = new CartOperationRequest()
            it
        }
    }
}
