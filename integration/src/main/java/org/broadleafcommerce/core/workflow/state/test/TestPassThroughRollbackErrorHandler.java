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

package org.broadleafcommerce.core.workflow.state.test;

import org.broadleafcommerce.core.workflow.ErrorHandler;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.broadleafcommerce.core.workflow.WorkflowException;

/**
 * Add an ErrorHandler that does nothing and does not stop the workflow
 *
 * @author Jeff Fischer
 */
public class TestPassThroughRollbackErrorHandler implements ErrorHandler {

    @Override
    public void handleError(ProcessContext context, Throwable th) throws WorkflowException {
        //do nothing
        //could get programmatic access to the ActivityStateManager for explicit rollbacks here
    }

    @Override
    public void setBeanName(String name) {
        //do nothing
    }
}
