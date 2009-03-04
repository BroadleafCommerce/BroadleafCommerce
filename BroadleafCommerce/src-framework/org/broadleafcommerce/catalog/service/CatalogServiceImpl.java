package org.broadleafcommerce.catalog.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.broadleafcommerce.catalog.dao.CategoryDao;
import org.broadleafcommerce.catalog.dao.ProductDao;
import org.broadleafcommerce.catalog.dao.SkuDao;
import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.catalog.domain.Sku;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("catalogService")
public class CatalogServiceImpl implements CatalogService {

    @Resource
    private CategoryDao categoryDao;

    @Resource
    private ProductDao productDao;

    @Resource
    private SkuDao skuDao;

    private Map<String,Category> cachedUrlProductKeyMap;

    @PostConstruct
    public void init() {
        refreshCategoryUrlKeyMap();
    }

    public Product findProductById(Long productId) {
        return productDao.readProductById(productId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Product> findProductsByName(String searchName) {
        return productDao.readProductsByName(searchName);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Product> findActiveProductsByCategory(Category category) {
        return productDao.readActiveProductsByCategory(category.getId());
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
    public Map<String,Category> getCategoryUrlKeyMap() {
        return cachedUrlProductKeyMap;
    }

    public void refreshCategoryUrlKeyMap() {
        Map<String,Category> tmpMap = new HashMap<String, Category>();
        List<Category> categories = findAllCategories();
        for (Iterator<Category> itr = categories.iterator(); itr.hasNext();) {
            Category cat = itr.next();
            tmpMap.put(cat.getUrlKey(), cat);
        }
        cachedUrlProductKeyMap= tmpMap;
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
    @Transactional(propagation = Propagation.REQUIRED)
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
