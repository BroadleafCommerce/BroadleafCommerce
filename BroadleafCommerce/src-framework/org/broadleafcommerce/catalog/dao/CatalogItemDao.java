package org.broadleafcommerce.catalog.dao;

import java.util.List;

import org.broadleafcommerce.catalog.domain.CatalogItem;

public interface CatalogItemDao {

    public CatalogItem readCatalogItemById(Long catalogItemId);
    
    public CatalogItem maintainCatalogItem(CatalogItem catalogItem);

    public List<CatalogItem> readCatalogItemsByName(String searchName);
}