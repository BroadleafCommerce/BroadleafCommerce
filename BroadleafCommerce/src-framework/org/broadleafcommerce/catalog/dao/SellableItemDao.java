package org.broadleafcommerce.catalog.dao;

import java.util.List;

import org.broadleafcommerce.catalog.domain.SellableItem;

public interface SellableItemDao {

	public SellableItem readSellableItemById(Long sellableItemId);

	public SellableItem maintainSellableItem(SellableItem sellableItem);

	public SellableItem readFirstSellableItem();

	public List<SellableItem> readAllSellableItems();

	public List<SellableItem> readSellableItemById(List<Long> ids);

	public List<SellableItem> readSellableItemsByCategoryItemId(Long catalogItemId);
}
