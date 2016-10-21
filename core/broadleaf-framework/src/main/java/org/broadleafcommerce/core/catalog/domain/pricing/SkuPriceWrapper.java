package org.broadleafcommerce.core.catalog.domain.pricing;

import org.broadleafcommerce.core.catalog.domain.Sku;

import java.io.Serializable;

/**
 * @author Jon Fleschler (jfleschler)
 */
public class SkuPriceWrapper implements Serializable {

    private static final long serialVersionUID = 1L;

    protected Sku targetSku;

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
