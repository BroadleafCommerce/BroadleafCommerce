package org.broadleafcommerce.catalog.domain;

import java.io.Serializable;

public class BroadleafProductListEntry implements ProductListEntry, Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long productId;

    private Long productListId;

    private Integer displayOrder;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getProductListId() {
        return productListId;
    }

    public void setProductListId(Long productListId) {
        this.productListId = productListId;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}
