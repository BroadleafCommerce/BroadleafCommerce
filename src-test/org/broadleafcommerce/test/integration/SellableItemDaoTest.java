package org.broadleafcommerce.test.integration;

import javax.annotation.Resource;

import org.broadleafcommerce.catalog.dao.CatalogItemDaoJpa;
import org.broadleafcommerce.catalog.dao.SellableItemDaoJpa;
import org.broadleafcommerce.catalog.domain.CatalogItem;
import org.broadleafcommerce.catalog.domain.SellableItem;
import org.broadleafcommerce.test.dataprovider.SellableItemDaoDataProvider;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.Test;

public class SellableItemDaoTest extends BaseTest {

	private Long sellableItemId;
	
	@Resource
	private SellableItemDaoJpa sellableItemDao;
	
	@Resource
	private CatalogItemDaoJpa catalogItemDao;

	@Test(groups = {"createSellableItem"},dataProvider="basicSellableItem", dataProviderClass=SellableItemDaoDataProvider.class, dependsOnGroups={"readUser1","createOrder","createCatalogItem"})
	@Rollback(false)
	public void createSellableItem(SellableItem sellableItem){
		CatalogItem catalogItem = (catalogItemDao.readCatalogItemsByName("setOfAggieDominoes")).get(0);
		sellableItem.setCatalogItem(catalogItem);
		sellableItem.setPrice(10.0);
		assert sellableItem.getId() == null;		
		sellableItem = sellableItemDao.maintainSellableItem(sellableItem);
		assert sellableItem.getId() != null;
		sellableItemId = sellableItem.getId();
	}
	
	@Test(groups = {"readFirstSellableItem"}, dependsOnGroups={"createSellableItem"})
	public void readFirstSellableItem(){
		SellableItem si = sellableItemDao.readFirstSellableItem();
		assert si != null;
		assert si.getId() != null;
	}
	
	@Test(groups = {"readSellableItemById"}, dependsOnGroups={"createSellableItem"})
	public void readSellableItemById(){
		SellableItem item = sellableItemDao.readSellableItemById(sellableItemId);
		assert item != null;
		assert item.getId() == sellableItemId;
	}
	
}
