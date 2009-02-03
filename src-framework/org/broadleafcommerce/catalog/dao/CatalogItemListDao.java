package org.broadleafcommerce.catalog.dao;

import org.broadleafcommerce.catalog.domain.CatalogItemList;

public interface CatalogItemListDao {

    public CatalogItemList readCatalogItemListById(Long catalogItemListId);
    
    public CatalogItemList maintainCatalogItemList(CatalogItemList catalogItemList);

}