package org.broadleafcommerce.catalog.dao;

import java.util.List;

import org.broadleafcommerce.catalog.domain.Product;

public interface ProductDao {

    public Product readProductById(Long productId);

    public Product maintainProduct(Product product);

    public List<Product> readProductsByName(String searchName);

    public List<Product> readActiveProductsByCategory(Long categoryId);
}