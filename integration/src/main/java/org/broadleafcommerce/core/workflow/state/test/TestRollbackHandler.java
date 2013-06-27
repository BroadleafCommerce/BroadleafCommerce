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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.workflow.Activity;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.broadleafcommerce.core.workflow.state.RollbackFailureException;
import org.broadleafcommerce.core.workflow.state.RollbackHandler;
import org.broadleafcommerce.core.workflow.state.RollbackStateLocal;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Demonstrate a call to a RollbackHandler
 *
 * @author Jeff Fischer
 */
public class TestRollbackHandler implements RollbackHandler {

    private static final Log LOG = LogFactory.getLog(TestRollbackHandler.class);

    @Override
    @Transactional("blTransactionManager")
    public void rollbackState(Activity<? extends ProcessContext> activity,
            ProcessContext processContext, Map<String, Object> stateConfiguration) throws RollbackFailureException {
        LOG.warn("******************* TestRollbackHandler Engaged *********************");
        LOG.warn("******************* Activity: " + activity.getBeanName() + " *********************");
        RollbackStateLocal rollbackStateLocal = RollbackStateLocal.getRollbackStateLocal();
        LOG.warn("******************* Workflow: " + rollbackStateLocal.getWorkflowId() + " *********************");
        LOG.warn("******************* Thread: " + rollbackStateLocal.getThreadId() + " *********************");
    }
}
