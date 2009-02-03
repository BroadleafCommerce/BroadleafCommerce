package org.broadleafcommerce.test.integration;

import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.catalog.dao.CatalogItemDaoJpa;
import org.broadleafcommerce.catalog.domain.CatalogItem;
import org.broadleafcommerce.test.dataprovider.CatalogItemDataProvider;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.Test;

public class CatalogItemDaoTest extends BaseTest {
    
    @Resource
    private CatalogItemDaoJpa catalogItemDao;

    @Test(groups={"createCatalogItem"},dataProvider="basicCatalogItem", dataProviderClass=CatalogItemDataProvider.class)
    @Rollback(false)
    public void testMaintainCatalogItem(CatalogItem catalogItem) {
        assert catalogItem.getId() == null;
        catalogItem = catalogItemDao.maintainCatalogItem(catalogItem);
        assert catalogItem.getId() != null;
    }
    
    @Test(dataProvider="basicCatalogItem", dataProviderClass=CatalogItemDataProvider.class)
    public void testReadCatalogItemsById(CatalogItem catalogItem) {
        catalogItem = catalogItemDao.maintainCatalogItem(catalogItem);
        CatalogItem result = catalogItemDao.readCatalogItemById(catalogItem.getId());
        assert catalogItem.equals(result);
    }
    
    @Test(dataProvider="basicCatalogItem", dataProviderClass=CatalogItemDataProvider.class)
    public void testReadCatalogItemsByName(CatalogItem catalogItem) {
        String name = catalogItem.getName();
        catalogItem = catalogItemDao.maintainCatalogItem(catalogItem);
        List<CatalogItem> result = catalogItemDao.readCatalogItemsByName(name);
        assert result.contains(catalogItem);
    }
    
}
