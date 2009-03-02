package org.broadleafcommerce.catalog.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;

import org.hibernate.annotations.CollectionOfElements;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_SKU")
public class SkuImpl implements Sku, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "SKU_ID")
    private Long id;

    @Column(name = "SALE_PRICE")
    private BigDecimal salePrice;

    @Column(name = "LIST_PRICE")
    private BigDecimal listPrice;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "LONG_DESCRIPTION")
    private String longDescription;

    @Column(name = "TAXABLE_FLAG")
    private boolean taxable;

    @Column(name = "ACTIVE_START_DATE")
    private Date activeStartDate;

    @Column(name = "ACTIVE_END_DATE")
    private Date activeEndDate;

    @CollectionOfElements
    @JoinTable(name = "SKU_IMAGE", joinColumns = @JoinColumn(name = "SKU_ID"))
    @org.hibernate.annotations.MapKey(columns = { @Column(name = "NAME", length = 5) })
    @Column(name = "URL")
    private Map<String, String> skuImages;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Map<String, String> getSkuImages() {
        return skuImages;
    }

    public String getSkuImage(String imageKey) {
        return skuImages.get(imageKey);
    }

    public void setSkuImages(Map<String, String> skuImages) {
        this.skuImages = skuImages;
    }
}
