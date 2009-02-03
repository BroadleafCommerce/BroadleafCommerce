package org.broadleafcommerce.catalog.dao;


import java.util.List;

import org.broadleafcommerce.catalog.domain.Category;

public interface CategoryDao {

    public Category readCategoryById(Long categoryId);

    public Category maintainCategory(Category category);

    public List<Category> readAllCategories();

    public List<Category> readAllSubCategories(Category category);
}