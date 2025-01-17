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

import javax.persistence.Column;

public class WeaveProductSkuUsage implements ProductSkuUsage{

    @Column(name = "USE_DEFAULT_SKU_IN_INVENTORY")
    @AdminPresentation(friendlyName = "ProductImpl_useDefaultSkuInInventory",
            group = ProductAdminPresentation.GroupName.Miscellaneous, order = 7000,
            tooltip = "ProductImpl_useDefaultSkuInInventory_Tooltip",
            defaultValue = "false")
    protected Boolean useDefaultSkuInInventory = false;

    @Override
    public Boolean getUseDefaultSkuInInventory() {
        return useDefaultSkuInInventory != null && useDefaultSkuInInventory;
    }

    @Override
    public void setUseDefaultSkuInInventory(Boolean useDefaultSkuInInventory) {
        this.useDefaultSkuInInventory = useDefaultSkuInInventory != null && useDefaultSkuInInventory;;
    }

}
