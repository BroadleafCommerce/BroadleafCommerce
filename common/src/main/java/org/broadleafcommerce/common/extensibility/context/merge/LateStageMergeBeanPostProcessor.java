/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.extensibility.context.merge;

import org.springframework.core.Ordered;

/**
 * Use this merge processor for merging duties that should take place later in the Spring startup lifecycle.
 * This would include items that should be merged after the initialization of the persistence layer, like beans
 * that rely on EntityManager injection in some way. This is the most commonly used merge processor. Less
 * commonly used is the {@link EarlyStageMergeBeanPostProcessor}. See {@link AbstractMergeBeanPostProcessor} for
 * usage information.
 *
 * @see AbstractMergeBeanPostProcessor
 * @author Jeff Fischer
 */
public class LateStageMergeBeanPostProcessor extends AbstractMergeBeanPostProcessor implements Ordered {

    protected int order = Integer.MAX_VALUE;

    /**
     * The regular ordering for this post processor in relation to other post processors. The default
     * value is Integer.MAX_VALUE.
     */
    @Override
    public int getOrder() {
        return order;
    }

    /**
     * The regular ordering for this post processor in relation to other post processors. The default
     * value is Integer.MAX_VALUE.
     *
     * @param order the regular ordering
     */
    public void setOrder(int order) {
        this.order = order;
    }

}
