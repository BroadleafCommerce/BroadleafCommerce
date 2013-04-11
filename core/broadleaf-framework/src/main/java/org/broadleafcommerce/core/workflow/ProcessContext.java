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

package org.broadleafcommerce.core.workflow;

import java.io.Serializable;

public interface ProcessContext extends Serializable {

    /**
     * Activly informs the workflow process to stop processing
     * no further activities will be executed
     *
     * @return whether or not the stop process call was successful
     */
    public boolean stopProcess();

    /**
     * Is the process stopped
     *
     * @return whether or not the process is stopped
     */
    public boolean isStopped();

    /**
     * Provide seed information to this ProcessContext, usually
     * provided at time of workflow kickoff by the containing
     * workflow processor.
     * 
     * @param seedObject - initial seed data for the workflow
     */
    public void setSeedData(Object seedObject);

}
