package org.broadleafcommerce.catalog.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

public interface Sku {

    public Long getId();

    public void setId(Long id);

    public BigDecimal getSalePrice();

    public void setSalePrice(BigDecimal salePrice);

    public BigDecimal getListPrice();

    public void setListPrice(BigDecimal listPrice);

    public String getName();

    public void setName(String name);

    public String getDescription();

    public void setDescription(String description);

    public String getLongDescription();

    public void setLongDescription(String longDescription);

    public boolean isTaxable();

    public void setTaxable(boolean taxable);

    public Date getActiveStartDate();

    public void setActiveStartDate(Date activeStartDate);

    public Date getActiveEndDate();

    public void setActiveEndDate(Date activeEndDate);

    public Map<String, String> getSkuImages();

    public String getSkuImage(String imageKey);

    public void setSkuImages(Map<String, String> skuImages);
}
