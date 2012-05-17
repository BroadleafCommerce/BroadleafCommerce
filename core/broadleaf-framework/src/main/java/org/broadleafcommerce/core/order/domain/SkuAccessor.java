package org.broadleafcommerce.core.order.domain;

import org.broadleafcommerce.core.catalog.domain.Sku;

/**
 * Interface indicating that an item contains a getSku method.
 *
 * Intended for use by subclasses of OrderItem that contain a sku.
 *
 * Created by bpolster.
 */
public interface SkuAccessor {
    Sku getSku();
}
