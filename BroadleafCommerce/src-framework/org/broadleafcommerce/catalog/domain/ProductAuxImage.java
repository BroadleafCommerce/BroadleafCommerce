package org.broadleafcommerce.catalog.domain;

public interface ProductAuxImage {

    public Long getId();

    public void setId(Long id);

    public String getUrl();

    public void setUrl(String url);

    public Product getProduct();

    public void setProduct(Product product);

    public Integer getDisplayOrder();

    public void setDisplayOrder(Integer displayOrder);

    public String getDescription();

    public void setDescription(String description);
}
