package org.broadleafcommerce.catalog.domain;

import java.io.Serializable;

public class BroadleafProductAuxImage implements ProductAuxImage, Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String url;

    private Product product;

    private Integer displayOrder;

    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
