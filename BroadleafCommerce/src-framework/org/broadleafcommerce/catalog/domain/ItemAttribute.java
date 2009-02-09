package org.broadleafcommerce.catalog.domain;

import java.io.Serializable;

//@Entity
//@Table(name = "ITEM_ATTRIBUTE", uniqueConstraints = { @UniqueConstraint(columnNames = { "CATALOG_ITEM_ID", "NAME" }), @UniqueConstraint(columnNames = { "SELLABLE_ITEM_ID", "NAME" }) })
public class ItemAttribute implements Serializable {

    private static final long serialVersionUID = 1L;

//    @Id
//    @GeneratedValue
//    @Column(name = "ITEM_ATTRIBUTE_ID")
    private Long id;

//    @ManyToOne
//    @JoinColumn(name = "CATALOG_ITEM_ID")
    private CatalogItem catalogItem;

//    @ManyToOne
//    @JoinColumn(name = "SELLABLE_ITEM_ID")
    private SellableItem sellableItem;

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

    public CatalogItem getCatalogItem() {
        return catalogItem;
    }

    public void setCatalogItem(CatalogItem catalogItem) {
        this.catalogItem = catalogItem;
    }

    public SellableItem getSellableItem() {
        return sellableItem;
    }

    public void setSellableItem(SellableItem sellableItem) {
        this.sellableItem = sellableItem;
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
