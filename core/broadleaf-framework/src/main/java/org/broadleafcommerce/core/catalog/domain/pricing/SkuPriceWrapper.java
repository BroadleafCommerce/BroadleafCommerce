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
package org.broadleafcommerce.core.catalog.domain.pricing;

import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuImpl;

import java.io.Serializable;

/**
 * @author Jon Fleschler (jfleschler)
 */
public class SkuPriceWrapper implements Serializable {

    private static final long serialVersionUID = 1L;

    protected Sku targetSku;

    public SkuPriceWrapper() {
        this.targetSku = new SkuImpl();
    }

    public SkuPriceWrapper(Sku sku) {
        this.targetSku = sku;
    }

    public Sku getTargetSku() {
        return targetSku;
    }

    public void setTargetSku(Sku targetSku) {
        this.targetSku = targetSku;
    }
}
