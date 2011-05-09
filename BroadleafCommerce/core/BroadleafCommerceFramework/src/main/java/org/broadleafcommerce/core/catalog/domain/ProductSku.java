package org.broadleafcommerce.core.catalog.domain;

/**
 * A version of the Product entity that represents a one to one relationship
 * between Product and Sku. This is the most common case among retailers.
 * 
 * @author jfischer
 *
 */
public interface ProductSku extends Product {

	/**
	 * Retrieve the sku
	 * 
	 * @return sku
	 */
	public Sku getSku();

	/**
	 * Set the sku
	 * 
	 * @param sku
	 */
	public void setSku(Sku sku);

}