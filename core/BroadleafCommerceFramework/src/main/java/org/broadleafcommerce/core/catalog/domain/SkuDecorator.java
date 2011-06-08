package org.broadleafcommerce.core.catalog.domain;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.broadleafcommerce.core.media.domain.Media;
import org.broadleafcommerce.money.Money;

public class SkuDecorator implements Sku {

	private static final long serialVersionUID = 1L;
	
	protected SkuImpl sku;
	
	public SkuDecorator(SkuImpl sku) {
		this.sku = sku;
	}
	
	public Long getId() {
		return sku.getId();
	}

	public void setId(Long id) {
		throw new RuntimeException("Not Supported");
	}

	public Money getSalePrice() {
		return sku.salePrice == null ? null : new Money(sku.salePrice);
	}

	public String toString() {
		return sku.toString();
	}

	public void setSalePrice(Money salePrice) {
		throw new RuntimeException("Not Supported");
	}

	public Money getRetailPrice() {
		return sku.retailPrice == null ? null : new Money(sku.retailPrice);
	}

	public void setRetailPrice(Money retailPrice) {
		throw new RuntimeException("Not Supported");
	}

	public Money getListPrice() {
		return sku.getListPrice();
	}

	public void setListPrice(Money listPrice) {
		throw new RuntimeException("Not Supported");
	}

	public String getName() {
		return sku.getName();
	}

	public void setName(String name) {
		throw new RuntimeException("Not Supported");
	}

	public String getDescription() {
		return sku.getDescription();
	}

	public void setDescription(String description) {
		throw new RuntimeException("Not Supported");
	}

	public String getLongDescription() {
		return sku.getLongDescription();
	}

	public void setLongDescription(String longDescription) {
		throw new RuntimeException("Not Supported");
	}

	public Boolean isTaxable() {
		return sku.isTaxable();
	}

	public Boolean getTaxable() {
		return sku.getTaxable();
	}

	public void setTaxable(Boolean taxable) {
		throw new RuntimeException("Not Supported");
	}

	public Boolean isDiscountable() {
		return sku.isDiscountable();
	}

	public Boolean getDiscountable() {
		return sku.getDiscountable();
	}

	public void setDiscountable(Boolean discountable) {
		throw new RuntimeException("Not Supported");
	}

	public Boolean isAvailable() {
		return sku.isAvailable();
	}

	public Boolean getAvailable() {
		return sku.getAvailable();
	}

	public void setAvailable(Boolean available) {
		throw new RuntimeException("Not Supported");
	}

	public Date getActiveStartDate() {
		return sku.getActiveStartDate();
	}

	public void setActiveStartDate(Date activeStartDate) {
		throw new RuntimeException("Not Supported");
	}

	public Date getActiveEndDate() {
		return sku.getActiveEndDate();
	}

	public void setActiveEndDate(Date activeEndDate) {
		throw new RuntimeException("Not Supported");
	}

	public boolean isActive() {
		return sku.isActive();
	}

	public boolean isActive(Product product, Category category) {
		return sku.isActive(product, category);
	}

	public Map<String, String> getSkuImages() {
		return sku.getSkuImages();
	}

	public String getSkuImage(String imageKey) {
		return sku.getSkuImage(imageKey);
	}

	public void setSkuImages(Map<String, String> skuImages) {
		throw new RuntimeException("Not Supported");
	}

	public Map<String, Media> getSkuMedia() {
		return sku.getSkuMedia();
	}

	public void setSkuMedia(Map<String, Media> skuMedia) {
		throw new RuntimeException("Not Supported");
	}

	public List<Product> getAllParentProducts() {
		return sku.getAllParentProducts();
	}

	public void setAllParentProducts(List<Product> allParentProducts) {
		throw new RuntimeException("Not Supported");
	}

	public boolean equals(Object obj) {
		return sku.equals(obj);
	}

	public int hashCode() {
		return sku.hashCode();
	}

	public SkuImpl getDelegate() {
		return sku;
	}
	
	public void reset() {
		sku = null;
	}
}
