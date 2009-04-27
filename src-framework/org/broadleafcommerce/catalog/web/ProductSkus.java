package org.broadleafcommerce.catalog.web;

import java.util.List;

import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.catalog.domain.Sku;

public class ProductSkus {

	private Product product;
	private List<Sku> skus;

	public ProductSkus(Product product, List<Sku> skus) {
		super();
		this.product = product;
		this.skus = skus;
	}

	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public List<Sku> getSkus() {
		return skus;
	}
	public void setSkus(List<Sku> skus) {
		this.skus = skus;
	}
}
