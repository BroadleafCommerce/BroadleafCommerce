/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.workflow;

import org.springframework.beans.factory.BeanNameAware;

public interface Activity extends BeanNameAware{
    /**
     * Called by the encompassing processor to activate
     * the execution of the Activity
     * 
     * @param context - process context for this workflow
     * @return resulting process context
     * @throws Exception
     */
    public ProcessContext execute(ProcessContext context) throws Exception;
    
    
    /**
     * Get the fine-grained error handler wired up for this Activity
     * @return
     */
    public ErrorHandler getErrorHandler();
    
    public String getBeanName();

}
