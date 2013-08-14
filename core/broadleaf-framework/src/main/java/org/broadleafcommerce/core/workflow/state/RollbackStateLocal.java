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

package org.broadleafcommerce.core.workflow.state;

import org.broadleafcommerce.common.classloader.release.ThreadLocalManager;

/**
 * Handles the identification of the outermost workflow and the current thread so that the StateManager can
 * operate on the appropriate RollbackHandlers.
 *
 * @author Jeff Fischer
 */
public class RollbackStateLocal {

    private static final ThreadLocal<RollbackStateLocal> THREAD_LOCAL = ThreadLocalManager.createThreadLocal(RollbackStateLocal.class, false);

    public static RollbackStateLocal getRollbackStateLocal() {
        return THREAD_LOCAL.get();
    }

    public static void setRollbackStateLocal(RollbackStateLocal rollbackStateLocal) {
        THREAD_LOCAL.set(rollbackStateLocal);
    }

    private String threadId;
    private String workflowId;

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }
}
