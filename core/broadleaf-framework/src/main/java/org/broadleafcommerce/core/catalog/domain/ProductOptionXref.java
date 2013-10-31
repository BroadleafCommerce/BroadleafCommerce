package org.broadleafcommerce.core.catalog.domain;

import java.io.Serializable;

/**
 * @author Jeff Fischer
 */
public interface ProductOptionXref extends Serializable {

    Long getId();

    void setId(Long id);

    Product getProduct();

    void setProduct(Product product);

    ProductOption getProductOption();

    void setProductOption(ProductOption productOption);

}
