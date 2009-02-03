package org.broadleafcommerce.catalog.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "CATALOG_ITEM_LIST_ASSOCIATION")
public class CatalogItemListAssociation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "ASSOCIATION_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "CATALOG_ITEM_ID")
    private CatalogItem catalogItem;

    @ManyToOne
    @JoinColumn(name = "CATALOG_ITEM_LIST_ID")
    private CatalogItemList catalogItemList;

    @Column(name = "RANK")
    private Integer rank;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CatalogItem getCatalogItem() {
        return catalogItem;
    }

    public void setCatalogItem(CatalogItem catalogItem) {
        this.catalogItem = catalogItem;
    }

    public CatalogItemList getCatalogItemList() {
        return catalogItemList;
    }

    public void setCatalogItemList(CatalogItemList catalogItemList) {
        this.catalogItemList = catalogItemList;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }
}
