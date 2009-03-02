package org.broadleafcommerce.catalog.domain;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface Product {

    public Long getId();

    public void setId(Long id);

    public Map<String, ProductAttribute> getProductAttributes();

    public void setProductAttributes(Map<String, ProductAttribute> productAttributes);

    public String getName();

    public void setName(String name);

    public String getDescription();

    public void setDescription(String description);

    public String getLongDescription();

    public void setLongDescription(String longDescription);

    public Date getActiveStartDate();

    public void setActiveStartDate(Date activeStartDate);

    public Date getActiveEndDate();

    public void setActiveEndDate(Date activeEndDate);

    public List<Sku> getActiveSkus();

    public void setSkus(List<Sku> skus);

    public Map<String, String> getProductImages();

    public String getProductImage(String imageKey);

    public void setProductImages(Map<String, String> productImages);

    //    public List<ImageDescription> getProductAuxillaryImages();
    //
    //    public void setProductAuxillaryImages(List<ImageDescription> productAuxillaryImages);

    public Category getDefaultCategory();

    public void setDefaultCategory(Category defaultCategory);
}
