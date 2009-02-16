package org.broadleafcommerce.catalog.domain;

import java.io.Serializable;

public class BroadleafItemAttribute implements ItemAttribute, Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Product product;

    private Sku sku;

    private String name;

    private String value;

    private Boolean searchable;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getSearchable() {
        return searchable;
    }

    public void setSearchable(Boolean searchable) {
        this.searchable = searchable;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Sku getSku() {
        return sku;
    }

    public void setSku(Sku sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return value;
    }

}
