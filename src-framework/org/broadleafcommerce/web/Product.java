package org.broadleafcommerce.web;

import java.util.List;

import org.broadleafcommerce.catalog.domain.CatalogItem;
import org.broadleafcommerce.catalog.domain.SellableItem;

public class Product {

	private CatalogItem catalogItem;
	private List<SellableItem> sellableItems;

	public Product(CatalogItem catalogItem, List<SellableItem> sellableItems) {
		super();
		this.catalogItem = catalogItem;
		this.sellableItems = sellableItems;
	}

	public CatalogItem getCatalogItem() {
		return catalogItem;
	}
	public void setCatalogItem(CatalogItem catalogItem) {
		this.catalogItem = catalogItem;
	}
	public List<SellableItem> getSellableItems() {
		return sellableItems;
	}
	public void setSellableItems(List<SellableItem> sellableItems) {
		this.sellableItems = sellableItems;
	}
}
