package org.broadleafcommerce.catalog.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.broadleafcommerce.common.domain.Auditable;

public class BroadleafSku implements Sku, Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    //    private Set<Sku> childSkus;

    private double price;

    private Auditable auditable;

    private Product product;

    private Map<String, ItemAttribute> itemAttributes;

    private String name;

    private Set<SkuImage> skuImages;

    private Map<String, String> skuImageMap;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    // TODO fix
    //    public Set<Sku> getChildSkus() {
    //        return childSkus;
    //    }
    //
    //    public void setChildSkus(Set<Sku> childSkus) {
    //        this.childSkus = childSkus;
    //    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Auditable getAuditable() {
        return auditable;
    }

    public void setAuditable(Auditable auditable) {
        this.auditable = auditable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, ItemAttribute> getItemAttributes() {
        return itemAttributes;
    }

    public void setItemAttributes(Map<String, ItemAttribute> itemAttributes) {
        this.itemAttributes = itemAttributes;
    }

    public Set<SkuImage> getSkuImages() {
        return skuImages;
    }

    public void setSkuImages(Set<SkuImage> skuImages) {
        this.skuImages = skuImages;
    }

    public String getSkuImage(String key) {
        if (skuImageMap == null) {
            skuImageMap = new HashMap<String, String>();
            Set<SkuImage> images = getSkuImages();
            if (images != null) {
                for (SkuImage s : images) {
                    skuImageMap.put(s.getName(), s.getUrl());
                }
            }
        }
        return skuImageMap.get(key);
    }
}
