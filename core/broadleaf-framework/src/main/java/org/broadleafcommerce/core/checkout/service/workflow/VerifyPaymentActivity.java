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

package org.broadleafcommerce.core.checkout.service.workflow;

import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;


/**
 * Write a good comment here when it's done, dawg.
 *
 * @author Phillip Verheyden (phillipuniverse)
 */
public class VerifyPaymentActivity extends BaseActivity<ProcessContext<CheckoutSeed>> {


    @Override
    public ProcessContext<CheckoutSeed> execute(ProcessContext<CheckoutSeed> context) throws Exception {
        //TODO: This should be relatively simple and just loop through the order payments on the order, add up the total
        // and throw an exception if they don't add up.
        //
        // There should also likely be something that says whether the payment was successful or not and this should check
        // that as well. Currently there isn't really a concept for that
        return context;
    }

}
