/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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
package org.broadleafcommerce.common.value;

import java.io.Serializable;

/**
 * Describes simple classes that can be assigned a name and value
 *
 * @author Jeff Fischer
 */
public interface ValueAssignable<T extends Serializable> extends Serializable {

    /**
     * The value
     *
     * @return The value
     */
    T getValue();

    /**
     * The value
     *
     * @param value The value
     */
    void setValue(T value);

    /**
     * The name
     *
     * @return The name
     */
    String getName();

    /**
     * The name
     *
     * @param name The name
     */
    void setName(String name);
}
