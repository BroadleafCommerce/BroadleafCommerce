package org.broadleafcommerce.catalog.service;

import java.util.List;

import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.catalog.domain.Sku;

public interface CatalogService {

    public Product saveProduct(Product product);

    public Product readProductById(Long productId);

    public List<Product> readProductsByName(String searchName);

    public Category saveCategory(Category category);

    public Category readCategoryById(Long categoryId);

    public Category readCategoryByUrlKey(String urlKey);

    public List<Category> readAllCategories();

    public List<Category> readAllSubCategories(Category category);

    public Sku saveSku(Sku sku);

    public List<Sku> readSkusForProductId(Long productId);

    public List<Sku> readAllSkus();

    public List<Sku> readSkusByIds(List<Long> ids);

    public Sku readSkuById(Long skuId);


}
