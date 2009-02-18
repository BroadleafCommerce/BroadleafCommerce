package org.broadleafcommerce.catalog.domain;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Product {

    public String getName();

    public void setName(String name);

    public String getDescription();

    public void setDescription(String description);

    public Long getId();

    public void setId(Long id);

    public Map<String, ItemAttribute> getItemAttributes();

    public void setItemAttributes(Map<String, ItemAttribute> itemAttributes);

    public List<Sku> getSkus();

    public void setSkus(List<Sku> skus);

    public Set<ProductImage> getProductImages();

    public void setProductImages(Set<ProductImage> productImages);

    public String getProductImage(String key);

    public List<ProductAuxImage> getProductAuxImages();

    public void setProductAuxImages(List<ProductAuxImage> productAuxImages);
}
