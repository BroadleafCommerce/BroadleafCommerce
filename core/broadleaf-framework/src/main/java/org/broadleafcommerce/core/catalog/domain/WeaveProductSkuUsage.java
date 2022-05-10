/*-
 * #%L
 * BroadleafCommerce Product
 * %%
 * Copyright (C) 2009 - 2021 Broadleaf Commerce
 * %%
 * NOTICE:  All information contained herein is, and remains
 * the property of Broadleaf Commerce, LLC
 * The intellectual and technical concepts contained
 * herein are proprietary to Broadleaf Commerce, LLC
 * and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Broadleaf Commerce, LLC.
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
