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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("catalogService")
public class CatalogServiceImpl implements CatalogService {

    @Resource
    private ProductDao productDao;

    @Resource
    private CategoryDao categoryDao;

    @Resource
    private SkuDao skuDao;

    @Override
    public Product readProductById(Long productId) {
        return productDao.readProductById(productId);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public List<Product> readProductsByName(String searchName) {
        return productDao.readProductsByName(searchName);
    }

    @Transactional(propagation=Propagation.REQUIRED)
    public Product saveProduct(Product product) {
        return productDao.maintainProduct(product);
    }

    @Override
    public Category readCategoryById(Long categoryId) {
        return categoryDao.readCategoryById(categoryId);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public Category saveCategory(Category category) {
        return categoryDao.maintainCategory(category);
    }

    @Override
    public List<Category> readAllCategories() {
        return categoryDao.readAllCategories();
    }

	@Override
	public List<Category> readAllSubCategories(Category category) {
		return categoryDao.readAllSubCategories(category);
	}

	@Override
	public Category readCategoryByUrlKey(String urlKey) {
		Map<String, Category> catMap = buildUrlKeyCategoryMap();
		return catMap.get(urlKey);
	}

	@Override
	public List<Sku> readAllSkus() {
		return skuDao.readAllSkus();
	}

	@Override
	public Sku readSkuById(Long skuId) {
		return skuDao.readSkuById(skuId);
	}

	@Override
	public List<Sku> readSkusForProductId(
			Long productId) {
		return skuDao.readSkusByProductId(productId);
	}

	@Override
    @Transactional(propagation=Propagation.REQUIRED)
	public Sku saveSku(Sku sku) {
		return skuDao.maintainSku(sku);
	}

	private Map<String, Category> buildUrlKeyCategoryMap(){
		Map<String, Category> catMap = new HashMap<String, Category>();
		List<Category> categories = categoryDao.readAllCategories();
		for (Iterator<Category> itr = categories.iterator(); itr.hasNext();){
			Category cat = itr.next();
			catMap.put(cat.getUrlKey(), cat);
		}

		return catMap;
	}

	@Override
	public List<Sku> readSkusByIds(List<Long> ids) {
		return skuDao.readSkusById(ids);
	}

}
