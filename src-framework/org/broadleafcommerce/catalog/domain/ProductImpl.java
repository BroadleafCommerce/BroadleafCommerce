package org.broadleafcommerce.catalog.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.OrderBy;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_PRODUCT")
public class ProductImpl implements Product, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    // TODO : figure out maps
    // This is a One-To-Many which OWNS!!! the collection
    // Notice that I don't have a "mappedBy" member on the @OneToMany annotation
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, targetEntity = ProductAttributeImpl.class)
    @MapKey(name = "name")
    @JoinTable(name = "PRODUCT_PRODUCT_ATTRIBUTES", joinColumns = @JoinColumn(name = "PRODUCT_ID"), inverseJoinColumns = @JoinColumn(name = "PRODUCT_ATTRIBUTE_ID"))
    private Map<String, ProductAttribute> productAttributes;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "LONG_DESCRIPTION")
    private String longDescription;

    @Column(name = "ACTIVE_START_DATE")
    private Date activeStartDate;

    @Column(name = "ACTIVE_END_DATE")
    private Date activeEndDate;

    @OneToMany(mappedBy = "product", targetEntity = SkuImpl.class)
    private List<Sku> skus;

    @CollectionOfElements
    @JoinTable(name = "PRODUCT_IMAGE", joinColumns = @JoinColumn(name = "PRODUCT_ID"))
    @org.hibernate.annotations.MapKey(columns = { @Column(name = "NAME", length = 5) })
    @Column(name = "URL")
    private Map<String, String> productImages;

    // This is a One-To-Many which OWNS!!! the collection
    // Notice that I don't have a "mappedBy" member on the @OneToMany annotation
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, targetEntity = ImageDescriptionImpl.class)
    @OrderBy(clause = "SEQUENCE")
    @JoinTable(name = "PRODUCT_AUXILLARY_IMAGES", joinColumns = @JoinColumn(name = "PRODUCT_ID"), inverseJoinColumns = @JoinColumn(name = "IMAGE_DESCRIPTION_ID"))
    private List<ImageDescription> productAuxillaryImages;

    @OneToOne(targetEntity = CategoryImpl.class)
    @JoinColumn(name = "DEFAULT_CATEGORY_ID")
    private Category defaultCategory;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Map<String, ProductAttribute> getProductAttributes() {
        return productAttributes;
    }

    public void setProductAttributes(Map<String, ProductAttribute> productAttributes) {
        this.productAttributes = productAttributes;
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

    public List<Sku> getSkus() {
        return skus;
    }

    public void setSkus(List<Sku> skus) {
        this.skus = skus;
    }

    public Map<String, String> getProductImages() {
        return productImages;
    }

    public void setProductImages(Map<String, String> productImages) {
        this.productImages = productImages;
    }

    public List<ImageDescription> getProductAuxillaryImages() {
        return productAuxillaryImages;
    }

    public void setProductAuxillaryImages(List<ImageDescription> productAuxillaryImages) {
        this.productAuxillaryImages = productAuxillaryImages;
    }

    public Category getDefaultCategory() {
        return defaultCategory;
    }

    public void setDefaultCategory(Category defaultCategory) {
        this.defaultCategory = defaultCategory;
    }
}
