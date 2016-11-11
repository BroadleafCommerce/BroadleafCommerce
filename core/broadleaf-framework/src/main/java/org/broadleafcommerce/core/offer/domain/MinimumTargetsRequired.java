/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
package org.broadleafcommerce.core.offer.domain;

/**
 * This interface can be used with an Offer implementation to indicate that an offer supports minimum required targets.
 * 
 * If set, the offer will not be applied unless the order contains at least the getMinimumRequired() number
 * of eligible targets.
 * 
 * For example, if this was set to 10 and an offer's target criteria was "item in category hot-sauces", then,
 * if the user added 9 hot-sauces to the cart, it would not apply.
 * 
 * If they added 10 or more, it would apply.  
 * 
 * This is different than using the quantity on the OrderItemCriteria.    If the OrderItemCriteria.quantity was
 * set to 10, the discount would be applied only for quantities in increments of 
 * 10 (so yes for 10, 20, etc. but not for 11, 12, etc.)
 * 
 * @author bpolster
 *
 */
public interface MinimumTargetsRequired {
    /**
     * The minimum number of targets required
     * @return
     */
    Integer getMinimumTargetsRequired();
}
