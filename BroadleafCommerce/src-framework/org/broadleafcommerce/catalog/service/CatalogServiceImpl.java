package org.broadleafcommerce.catalog.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.broadleafcommerce.catalog.dao.CatalogItemDao;
import org.broadleafcommerce.catalog.dao.CategoryDao;
import org.broadleafcommerce.catalog.dao.SellableItemDao;
import org.broadleafcommerce.catalog.domain.CatalogItem;
import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.catalog.domain.SellableItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("catalogService")
public class CatalogServiceImpl implements CatalogService {

    @Resource
    private CatalogItemDao catalogItemDao;

    @Resource
    private CategoryDao categoryDao;

    @Resource
    private SellableItemDao sellableItemDao;

    @Override
    public CatalogItem readCatalogItemById(Long catalogItemId) {
        return catalogItemDao.readCatalogItemById(catalogItemId);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public List<CatalogItem> readCatalogItemsByName(String searchName) {
        return catalogItemDao.readCatalogItemsByName(searchName);
    }

    @Transactional(propagation=Propagation.REQUIRED)
    public CatalogItem saveCatalogItem(CatalogItem catalogItem) {
        return catalogItemDao.maintainCatalogItem(catalogItem);
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
	public List<SellableItem> readAllSellableItems() {
		return sellableItemDao.readAllSellableItems();
	}

	@Override
	public SellableItem readSellableItemById(Long sellableItemId) {
		return sellableItemDao.readSellableItemById(sellableItemId);
	}

	@Override
	public List<SellableItem> readSellableItemsForCatalogItemId(
			Long catalogItemId) {
		return sellableItemDao.readSellableItemsByCategoryItemId(catalogItemId);
	}

	@Override
    @Transactional(propagation=Propagation.REQUIRED)
	public SellableItem saveSellableItem(SellableItem sellableItem) {
		return sellableItemDao.maintainSellableItem(sellableItem);
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
	public List<SellableItem> readSellableItemsByIds(List<Long> ids) {
		return sellableItemDao.readSellableItemById(ids);
	}

}
