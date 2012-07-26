package org.broadleafcommerce.core.catalog.domain;

public class RelatedProductDTO {
	private Long categoryId;
	private Long productId;
	private RelatedProductTypeEnum type = RelatedProductTypeEnum.FEATURED;
	private boolean cumulativeResults=true;
	private Integer quantity = null;
	
	/**
	 * Returns the categoryId for which the system should find related products.
	 */
	public Long getCategoryId() {
		return categoryId;
	}

	/**
	 * Sets the categoryId for which the system should find related products.   May be ignored 
	 * by implementations if an productId is also specified.   
	 * @param categoryId
	 */
	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}
	
	/**
	 * Returns the productId for which the system should find related products.
	 */
	public Long getProductId() {
		return productId;
	}
	
	/**
	 * Sets the productId for which the system should find related products.   
	 * @param productId
	 */	
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	
	/**
	 * Returns the type of relationship that is desired.
	 * @see RelatedProductTypeEnum
	 *  
	 * @return
	 */
	public RelatedProductTypeEnum getType() {
		return type;
	}
	
	/**
	 * Sets the type of relationship that is desired.
	 * 
	 * @see RelatedProductTypeEnum
	 * @param type
	 */
	public void setType(RelatedProductTypeEnum type) {
		this.type = type;
	}
	
	/**
	 * Returns whether cumulative results are desired.   Defaults to true.  
	 * 
	 * @return
	 */
	public boolean isCumulativeResults() {
		return cumulativeResults;
	}
	
	/**
	 * Sets whether cumulative results are desired. 
	 * 
	 * @param cumulativeResults
	 */
	public void setCumulativeResults(boolean cumulativeResults) {
		this.cumulativeResults = cumulativeResults;
	}

	/**
	 * The number of results to return.    The system will not look for additional results after 
	 * the quantity has been met.    Null indicates to return all results.
	 * @return
	 */
	public Integer getQuantity() {
		return quantity;
	}

	/**
	 * The number of results to return.  Null indicates to return all results.
	 * 
	 * @param maxQuantity
	 */
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
}
