package org.broadleafcommerce.core.web.controller.catalog;

import java.io.Serializable;

import org.broadleafcommerce.core.catalog.domain.Product;

public class ReviewForm implements Serializable {

	private static final long serialVersionUID = 1L;

	protected Product product;
	protected Double rating;
	protected String reviewText;

	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public Double getRating() {
		return rating;
	}
	public void setRating(Double rating) {
		this.rating = rating;
	}
	public String getReviewText() {
		return reviewText;
	}
	public void setReviewText(String reviewText) {
		this.reviewText = reviewText;
	}
	
}
