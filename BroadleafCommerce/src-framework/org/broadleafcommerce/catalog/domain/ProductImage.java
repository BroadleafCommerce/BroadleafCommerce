package org.broadleafcommerce.catalog.domain;

public interface ProductImage {

    public Long getId();

    public void setId(Long id);

    public String getName();

    public void setName(String name);

    public String getUrl();

    public void setUrl(String url);

    public Product getProduct();

    public void setProduct(Product product);
}
