package org.broadleafcommerce.search.service;

import java.util.List;

import org.broadleafcommerce.catalog.domain.Sku;

public interface SearchService {

	public void rebuildSkuIndex();

	public List<Sku> performSearch(String input);
}
