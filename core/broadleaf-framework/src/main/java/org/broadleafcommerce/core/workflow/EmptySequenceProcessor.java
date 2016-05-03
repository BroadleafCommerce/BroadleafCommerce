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

import org.broadleafcommerce.core.order.service.OrderService;


/**
 * Convenience class for creating an empty workflow. Useful when a user wants to remove workflow behavior from Broadleaf.
 * For instance, a user might want to subclass {@link OrderService} and provide their own implementation of addItem, but
 * wants to invoke the super implementation of this method to obtain all functionality <i>except</i> executing the workflow
 * since they want to take charge of the entire process themselves.
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
public class EmptySequenceProcessor extends SequenceProcessor {

    @Override
    protected ProcessContext createContext(Object seedData) {
        return null;
    }

}
