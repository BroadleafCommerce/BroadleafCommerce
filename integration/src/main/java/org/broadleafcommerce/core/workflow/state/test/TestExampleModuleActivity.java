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

import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ModuleActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;


/**
 * Pass-through activity to test that a workflow with a {@link ModuleActivity} marker interface in it performs correctly
 *
 * @author Phillip Verheyden (phillipuniverse)
 */
public class TestExampleModuleActivity extends BaseActivity<ProcessContext> implements ModuleActivity {

    @Override
    public ProcessContext execute(ProcessContext context) throws Exception {
        return context;
    }

    @Override
    public String getModuleName() {
        return "integration";
    }

}
