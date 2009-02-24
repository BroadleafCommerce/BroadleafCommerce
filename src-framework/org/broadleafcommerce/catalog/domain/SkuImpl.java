package org.broadleafcommerce.catalog.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_SKU")
// @DiscriminatorColumn(name="TYPE")
public class SkuImpl implements Sku, Serializable {

    private static final long serialVersionUID = 1L;

    // TODO SKU_ID
    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    // private Set<Sku> childSkus;

    // TODO return money class from getter SALE_PRICE
    @Column(name = "SALE_PRICE")
    private BigDecimal salePrice;

    // TODO return money class from getter LIST_PRICE
    @Column(name = "LIST_PRICE")
    private BigDecimal listPrice;

    // TODO
    // private Map<String, ItemAttribute> itemAttributes;

    // TODO return money class from getter NAME
    @Column(name = "NAME")
    private String name;

    // TODO DESCRIPTION
    @Column(name = "DESCRIPTION")
    private String description;

    // TODO LONG_DESCRIPTION
    @Column(name = "LONG_DESCRIPTION")
    private String longDescription;

    // TODO TAXABLE_FLAG
    @Column(name = "TAXABLE_FLAG")
    private boolean taxable;

    // TODO ACTIVE_START_DATE
    @Column(name = "ACTIVE_START_DATE")
    private Date activeStartDate;

    // TODO ACTIVE_END_DATE
    @Column(name = "ACTIVE_END_DATE")
    private Date activeEndDate;

    // TODO fix map
    //    private Set<SkuImage> skuImages;

    //    private Map<String, String> skuImageMap;

    @ManyToOne(targetEntity = ProductImpl.class)
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // TODO fix
    // public Set<Sku> getChildSkus() {
    // return childSkus;
    // }
    //
    // public void setChildSkus(Set<Sku> childSkus) {
    // this.childSkus = childSkus;
    // }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public BigDecimal getListPrice() {
        return listPrice;
    }

    public void setListPrice(BigDecimal listPrice) {
        this.listPrice = listPrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public boolean isTaxable() {
        return taxable;
    }

    public void setTaxable(boolean taxable) {
        this.taxable = taxable;
    }

    public Date getActiveStartDate() {
        return activeStartDate;
    }

    public void setActiveStartDate(Date activeStartDate) {
        this.activeStartDate = activeStartDate;
    }

    public Date getActiveEndDate() {
        return activeEndDate;
    }

    public void setActiveEndDate(Date activeEndDate) {
        this.activeEndDate = activeEndDate;
    }

    // public String getSkuImage(String key) {
    // if (skuImageMap == null) {
    // skuImageMap = new HashMap<String, String>();
    // Set<SkuImage> images = getSkuImages();
    // if (images != null) {
    // for (SkuImage s : images) {
    // skuImageMap.put(s.getName(), s.getUrl());
    // }
    // }
    // }
    // return skuImageMap.get(key);
    // }
}
