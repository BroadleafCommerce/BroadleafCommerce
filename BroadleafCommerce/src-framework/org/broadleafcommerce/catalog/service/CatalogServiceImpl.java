package org.broadleafcommerce.catalog.service;

import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.catalog.dao.CategoryDao;
import org.broadleafcommerce.catalog.dao.ProductDao;
import org.broadleafcommerce.catalog.dao.SkuDao;
import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.catalog.domain.Sku;
import org.springframework.stereotype.Service;

@Service("catalogServiceImpl")
public class CatalogServiceImpl implements CatalogService {

    @Resource
    private CategoryDao categoryDao;

    @Resource
    private ProductDao productDao;

    @Resource
    private SkuDao skuDao;

    public Product findProductById(Long productId) {
        return productDao.readProductById(productId);
    }

    public List<Product> findProductsByName(String searchName) {
        return productDao.readProductsByName(searchName);
    }

    public List<Product> findActiveProductsByCategory(Category category) {
        return productDao.readActiveProductsByCategory(category.getId());
    }

    public Product saveProduct(Product product) {
        return productDao.maintainProduct(product);
    }

    @Override
    public Category findCategoryById(Long categoryId) {
        return categoryDao.readCategoryById(categoryId);
    }

    @Override
    public Category saveCategory(Category category) {
        return categoryDao.maintainCategory(category);
    }

    @Override
    public List<Category> findAllCategories() {
        return categoryDao.readAllCategories();
    }

    @Override
    public List<Sku> findAllSkus() {
        return skuDao.readAllSkus();
    }

    @Override
    public Sku readSkuById(Long skuId) {
        return skuDao.readSkuById(skuId);
    }

    @Override
    public Sku saveSku(Sku sku) {
        return skuDao.maintainSku(sku);
    }

    @Override
    public List<Sku> findSkusByIds(List<Long> ids) {
        return skuDao.readSkusById(ids);
    }

    public void setProductDao(ProductDao productDao) {
        this.productDao = productDao;
    }

    public void setSkuDao(SkuDao skuDao) {
        this.skuDao = skuDao;
    }

    public List<Product> findProductsForCategory(Category category) {
        // TODO Auto-generated method stub
        return null;
    }

    public void setCategoryDao(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }
}
