package org.broadleafcommerce.catalog.domain;

public interface CategoryCategory {

    public Long getId();

    public void setId(Long id);

    public Category getCategory();

    public void setCategory(Category category);

    public Category getSubCategory();

    public void setSubCategory(Category subCategory);

    public Integer getDisplayOrder();

    public void setDisplayOrder(Integer displayOrder);
}
