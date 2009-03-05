package org.broadleafcommerce.catalog.domain;

public interface ProductAuxillaryImage {

    public Long getId();

    public void setId(Long id);

    public Product getProduct();

    public void setProduct(Product product);

    public Integer getDisplayOrder();

    public void setDisplayOrder(Integer displayOrder);

    public String getUrl();

    public void setUrl(String url);

    public String getDescription();

    public void setDescription(String description);
}
