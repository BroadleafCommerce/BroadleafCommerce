package org.broadleafcommerce.catalog.service;

import java.util.List;

import org.broadleafcommerce.catalog.domain.CatalogItem;
import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.catalog.domain.SellableItem;

public interface CatalogService {

    public CatalogItem saveCatalogItem(CatalogItem catalogItem);

    public CatalogItem readCatalogItemById(Long catalogItemId);

    public List<CatalogItem> readCatalogItemsByName(String searchName);

    public Category saveCategory(Category category);

    public Category readCategoryById(Long categoryId);

    public Category readCategoryByUrlKey(String urlKey);

    public List<Category> readAllCategories();

    public List<Category> readAllSubCategories(Category category);

    public SellableItem saveSellableItem(SellableItem sellableItem);

    public List<SellableItem> readSellableItemsForCatalogItemId(Long catalogItemId);

    public List<SellableItem> readAllSellableItems();

    public List<SellableItem> readSellableItemsByIds(List<Long> ids);

    public SellableItem readSellableItemById(Long sellableItemId);


}
