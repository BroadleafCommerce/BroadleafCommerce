package org.broadleafcommerce.catalog.domain;

import java.io.Serializable;

//@Entity
//@Table(name = "ITEM_ATTRIBUTE", uniqueConstraints = { @UniqueConstraint(columnNames = { "PRODUCT_ID", "NAME" }), @UniqueConstraint(columnNames = { "SKU_ID", "NAME" }) })
public class ItemAttribute implements Serializable {

    private static final long serialVersionUID = 1L;

//    @Id
//    @GeneratedValue
//    @Column(name = "ITEM_ATTRIBUTE_ID")
    private Long id;

//    @ManyToOne
//    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

//    @ManyToOne
//    @JoinColumn(name = "SKU_ID")
    private Sku sku;

//    @Column(name = "NAME")
    private String name;

//    @Column(name = "VALUE")
    private String value;

//    @Column(name = "SEARCHABLE")
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
