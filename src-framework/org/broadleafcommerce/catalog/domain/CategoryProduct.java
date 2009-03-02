package org.broadleafcommerce.catalog.domain;

public interface CategoryProduct {

    public Long getId();

    public void setId(Long id);

    public Category getCategory();

    public void setCategory(Category category);

    public Product getProduct();

    public void setProduct(Product product);

    public Integer getDisplayOrder();

    public void setDisplayOrder(Integer displayOrder);
}
