package org.broadleafcommerce.search.service;

import java.util.List;

import org.broadleafcommerce.catalog.domain.SellableItem;

public interface SearchService {

	public void rebuildSellableItemIndex();

	public List<SellableItem> performSearch(String input);
}
