/*-
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.order.domain;

import org.broadleafcommerce.common.copy.MultiTenantCloneable;
import org.broadleafcommerce.common.money.Money;

import java.io.Serializable;

public interface BundleOrderItemFeePrice extends Serializable, MultiTenantCloneable<BundleOrderItemFeePrice> {

    public abstract Long getId();

    public abstract void setId(Long id);

    public abstract BundleOrderItem getBundleOrderItem();

    public abstract void setBundleOrderItem(BundleOrderItem bundleOrderItem);

    public abstract Money getAmount();

    public abstract void setAmount(Money amount);

    public abstract String getName();

    public abstract void setName(String name);

    public abstract Boolean isTaxable();

    public abstract void setTaxable(Boolean isTaxable);

    public abstract String getReportingCode();

    public abstract void setReportingCode(String reportingCode);

    public BundleOrderItemFeePrice clone();

}
