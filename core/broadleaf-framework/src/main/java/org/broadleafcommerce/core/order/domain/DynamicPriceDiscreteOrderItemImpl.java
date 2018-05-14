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
package org.broadleafcommerce.core.order.domain;

import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

/**
 * 
 * @author jfischer
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_DYN_DISCRETE_ORDER_ITEM")
@AdminPresentationClass(friendlyName = "DynamicPriceDiscreteOrderItemImpl_dynamicPriceOrderItem")
public class DynamicPriceDiscreteOrderItemImpl extends DiscreteOrderItemImpl implements DynamicPriceDiscreteOrderItem {

    private static final long serialVersionUID = 1L;

    @Override
    public void setSku(Sku sku) {
        this.sku = sku;
        this.name = sku.getName();
    }

    @Override
    public boolean updateSaleAndRetailPrices() {
        return false;
    }

}
