package org.broadleafcommerce.catalog.domain;

public interface SkuImage {

    public Long getId();

    public void setId(Long id);

    public String getName();

    public void setName(String name);

    public String getUrl();

    public void setUrl(String url);

    public Sku getSku();

    public void setSku(Sku sku);
}
