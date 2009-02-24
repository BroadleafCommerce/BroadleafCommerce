package org.broadleafcommerce.catalog.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.broadleafcommerce.catalog.dao.CategoryDao;
import org.broadleafcommerce.catalog.dao.ProductDao;
import org.broadleafcommerce.catalog.dao.SkuDao;
import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.catalog.domain.Sku;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("catalogService")
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

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Product> findProductsByName(String searchName) {
        return productDao.readProductsByName(searchName);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Product saveProduct(Product product) {
        return productDao.maintainProduct(product);
    }

    @Override
    public Category findCategoryById(Long categoryId) {
        return categoryDao.readCategoryById(categoryId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Category saveCategory(Category category) {
        return categoryDao.maintainCategory(category);
    }

    @Override
    public List<Category> findAllCategories() {
        return categoryDao.readAllCategories();
    }

    @Override
    public List<Category> findAllSubCategories(Category category) {
        return categoryDao.readAllSubCategories(category);
    }

    @Override
    public Category findCategoryByUrlKey(String urlKey) {
        Map<String, Category> catMap = buildUrlKeyCategoryMap();
        return catMap.get(urlKey);
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
    public List<Sku> findSkusForProductId(Long productId) {
        return skuDao.readSkusByProductId(productId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Sku saveSku(Sku sku) {
        return skuDao.maintainSku(sku);
    }

    private Map<String, Category> buildUrlKeyCategoryMap() {
        Map<String, Category> catMap = new HashMap<String, Category>();
        List<Category> categories = categoryDao.readAllCategories();
        for (Iterator<Category> itr = categories.iterator(); itr.hasNext();) {
            Category cat = itr.next();
            catMap.put(cat.getUrlKey(), cat);
        }

        return catMap;
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
