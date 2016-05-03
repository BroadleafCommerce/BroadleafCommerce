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

import java.io.Serializable;

public interface ProcessContext<T> extends Serializable {

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
    public void setSeedData(T seedObject);

    /**
     * Returns the seed information
     * @return
     */
    public T getSeedData();

}
