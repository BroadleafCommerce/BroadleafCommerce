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
package org.broadleafcommerce.common.util;

/**
 * This Tuple class can be used when you want to return two elements from a function in a type safe way.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class Tuple<A, B> {
    
    protected final A one;
    protected final B two;
    
    public Tuple(A one, B two) {
        this.one = one;
        this.two = two;
    }
    
    public A getFirst() {
        return one;
    }
    
    public B getSecond() {
        return two;
    }

}
