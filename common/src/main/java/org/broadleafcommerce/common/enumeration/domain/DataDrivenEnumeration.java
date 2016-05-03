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
package org.broadleafcommerce.common.enumeration.domain;

import org.broadleafcommerce.common.copy.MultiTenantCloneable;

import java.io.Serializable;
import java.util.List;

public interface DataDrivenEnumeration extends Serializable, MultiTenantCloneable<DataDrivenEnumeration> {
    
    public Long getId();

    public void setId(Long id);

    public String getKey();

    public void setKey(String key);

    public Boolean getModifiable();

    public void setModifiable(Boolean modifiable);

    /**
     * Gets list of values associated with this enumeration.
     */
    public List<DataDrivenEnumerationValue> getEnumValues();

    /**
     * Sets list of values associated with this enumeration. 
     */
    public void setEnumValues(List<DataDrivenEnumerationValue> enumValues);

    /**
     * Incorrectly named, kept purely for API consistency
     * @deprecated use {@link #getEnumValues()} instead
     */
    @Deprecated
    public List<DataDrivenEnumerationValue> getOrderItems();

    /**
     * Incorrectly named, kept purely for API consistency
     * @deprecated use {@link #setEnumValues()} instead
     */
    @Deprecated
    public void setOrderItems(List<DataDrivenEnumerationValue> orderItems);

}
