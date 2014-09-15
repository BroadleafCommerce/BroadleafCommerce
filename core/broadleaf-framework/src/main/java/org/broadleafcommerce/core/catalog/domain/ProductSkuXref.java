package org.broadleafcommerce.core.catalog.domain;

import java.io.Serializable;

/**
 * @author Jeff Fischer
 */
public interface ProductSkuXref extends Serializable {

    Sku getSku();

    void setSku(Sku sku);

    Product getProduct();

    void setProduct(Product product);

    Long getId();

    void setId(Long id);
}
