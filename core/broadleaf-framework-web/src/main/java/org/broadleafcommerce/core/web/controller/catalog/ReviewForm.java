package org.broadleafcommerce.core.web.controller.catalog;

import java.io.Serializable;

public class ReviewForm implements Serializable {

	private static final long serialVersionUID = 1L;

	protected Double rating;
	protected String reviewText;

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
