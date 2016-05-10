/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
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
