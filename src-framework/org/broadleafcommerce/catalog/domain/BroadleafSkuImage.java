package org.broadleafcommerce.catalog.domain;

import java.io.Serializable;

public class BroadleafSkuImage implements SkuImage, Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private String url;

    private Sku sku;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Sku getSku() {
        return sku;
    }

    public void setSku(Sku sku) {
        this.sku = sku;
    }
}
