/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
package org.broadleafcommerce.core.workflow;


/**
 * <p>
 * Marker interface that all modules should use when adding activities to Broadleaf workflows. This is used for logging to
 * the user on startup that a module has modified a particular workflow and the final ordering of the configured workflow.
 * This logging is necessary for users that might be unaware of all of the activities that different modules could be
 * injecting into their workflows, since it's possible they they might want to change the ordering of their particular
 * activities as well.</p>
 * 
 * <p>When writing a module activity, the ordering should be configured in the 100 range (3100, 3200, etc) so that users
 * who use your module can configure custom activities in-between framework <b>and</b> module activities.</p>
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
public interface ModuleActivity {

    /**
     * The name of the module that this activity came from (for instance: 'inventory')
     * @return
     */
    public String getModuleName();

}
