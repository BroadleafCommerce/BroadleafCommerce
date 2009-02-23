package org.broadleafcommerce.catalog.domain;

public interface ProductAttribute {

    public Long getId();

    public void setId(Long id);

    public String getValue();

    public void setValue(String value);

    public Boolean getSearchable();

    public void setSearchable(Boolean searchable);

    public Product getProduct();

    public void setProduct(Product product);

    public String getName();

    public void setName(String name);
}
