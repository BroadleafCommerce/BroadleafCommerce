package org.broadleafcommerce.catalog.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BroadleafProduct implements Product, Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Map<String, ItemAttribute> itemAttributes;

    private String description;

    private String name;

    private List<Sku> skus;

    private Set<ProductImage> productImages;

    private List<ProductAuxImage> productAuxImages;

    private Map<String, String> productImageMap;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Map<String, ItemAttribute> getItemAttributes() {
        return itemAttributes;
    }

    public void setItemAttributes(Map<String, ItemAttribute> itemAttributes) {
        this.itemAttributes = itemAttributes;
    }

    public List<Sku> getSkus() {
        return skus;
    }

    public void setSkus(List<Sku> skus) {
        this.skus = skus;
    }

    public Set<ProductImage> getProductImages() {
        return productImages;
    }

    public void setProductImages(Set<ProductImage> productImages) {
        this.productImages = productImages;
    }

    public String getProductImage(String key) {
        if (productImageMap == null) {
            productImageMap = new HashMap<String, String>();
            Set<ProductImage> images = getProductImages();
            if (images != null) {
                for (ProductImage pi : images) {
                    productImageMap.put(pi.getName(), pi.getUrl());
                }
            }
        }
        return productImageMap.get(key);
    }

    public List<ProductAuxImage> getProductAuxImages() {
        return productAuxImages;
    }

    public void setProductAuxImages(List<ProductAuxImage> productAuxImages) {
        this.productAuxImages = productAuxImages;
    }
}
