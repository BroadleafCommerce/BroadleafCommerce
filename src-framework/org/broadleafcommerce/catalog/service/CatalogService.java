package org.broadleafcommerce.catalog.service;

import java.util.List;

import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.catalog.domain.Sku;

public interface CatalogService {

    public Product saveProduct(Product product);

    public Product findProductById(Long productId);

    public List<Product> findProductsByName(String searchName);

    public Category saveCategory(Category category);

    public Category findCategoryById(Long categoryId);

    public Category findCategoryByUrlKey(String urlKey);

    public List<Category> findAllCategories();

    public List<Category> findAllSubCategories(Category category);

    public List<Product> findProductsForCategory(Category category);

    public Sku saveSku(Sku sku);

    public List<Sku> findSkusForProductId(Long productId);

    public List<Sku> findAllSkus();

    public List<Sku> findSkusByIds(List<Long> ids);

    public Sku readSkuById(Long skuId);
}
