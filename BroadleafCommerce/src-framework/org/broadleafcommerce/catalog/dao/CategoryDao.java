package org.broadleafcommerce.catalog.dao;


import java.util.List;

import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.catalog.domain.Product;

public interface CategoryDao {

    public Category readCategoryById(Long categoryId);

    public Category readCategoryByName(String categoryId);

    public Category save(Category category);

    public List<Category> readAllCategories();

    public List<Product> readAllProducts();

    public List<Category> readAllSubCategories(Category category);
}