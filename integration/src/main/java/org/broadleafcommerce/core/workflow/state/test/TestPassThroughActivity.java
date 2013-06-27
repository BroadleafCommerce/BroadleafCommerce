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

package org.broadleafcommerce.core.workflow.state.test;

import org.broadleafcommerce.core.checkout.service.workflow.CheckoutContext;
import org.broadleafcommerce.core.workflow.BaseActivity;


/**
 * A Do-nothing activity used to test proper merge ordering in workflows
 *
 * @author Phillip Verheyden (phillipuniverse)
 */
public class TestPassThroughActivity extends BaseActivity<CheckoutContext> {

    @Override
    public CheckoutContext execute(CheckoutContext context) throws Exception {
        // TODO Auto-generated method stub
        return context;
    }

}
