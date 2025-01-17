/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.core.catalog.domain;

import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.ConfigurationItem;
import org.broadleafcommerce.common.presentation.ValidationConfiguration;

import javax.persistence.Column;

public class WeaveSkuMinOrderQuantity implements SkuMinOrderQuantity {

    @Column(name = "HAS_MIN_ORDER_QUANTITY")
    @AdminPresentation(friendlyName = "SkuImpl_hasMinOrderQuantity",
        tab = SkuAdminPresentation.TabName.Advanced, tabOrder = SkuAdminPresentation.TabOrder.Advanced,
        group = SkuAdminPresentation.GroupName.Advanced, order = 1000,
        defaultValue = "false")
    protected Boolean hasMinOrderQuantity = Boolean.FALSE;

    @Column(name = "MIN_ORDER_QUANTITY")
    @AdminPresentation(friendlyName = "SkuImpl_minOrderQuantity",
        tab = SkuAdminPresentation.TabName.Advanced, tabOrder = SkuAdminPresentation.TabOrder.Advanced,
        group = SkuAdminPresentation.GroupName.Advanced, order = 2000,
        validationConfigurations = {
            @ValidationConfiguration(validationImplementation = "blGreaterThanMinValueValidator",
                    configurationItems = { @ConfigurationItem(itemName = "minValue", itemValue = "0") }
            )
        },
        defaultValue = "1")
    protected Integer minOrderQuantity;


    @Override
    public boolean hasMinOrderQuantity() {
        return (hasMinOrderQuantity == null)? Boolean.FALSE : hasMinOrderQuantity;
    }

    @Override
    public void setHasMinOrderQuantity(Boolean hasMinOrderQuantity) {
        this.hasMinOrderQuantity = hasMinOrderQuantity;
    }

    @Override
    public Integer getMinOrderQuantity() {
        return (minOrderQuantity == null)? 1 : minOrderQuantity;
    }

    @Override
    public void setMinOrderQuantity(Integer minOrderQuantity) {
        this.minOrderQuantity = minOrderQuantity;
    }

}
