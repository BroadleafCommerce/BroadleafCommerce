package org.broadleafcommerce.core.web.controller.catalog;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.rating.domain.ReviewDetail;
import org.broadleafcommerce.core.rating.service.RatingService;
import org.broadleafcommerce.core.rating.service.type.RatingSortType;
import org.broadleafcommerce.core.rating.service.type.RatingType;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.ui.Model;

public class BroadleafRatingsController {

	@Resource(name = "blRatingService")
	protected RatingService ratingService;
	@Resource(name = "blCatalogService")
	protected CatalogService catalogService;
	
	protected String reviewsView = "catalog/partials/reviews";
	protected String reviewsRedirect = "redirect:/ratings/reviews";
	protected String formView = "catalog/partials/review";
	
	public String readReviews(HttpServletRequest request, Model model, String itemId, RatingType type, int start, int finish, RatingSortType sortBy) {
		List<ReviewDetail> reviews = ratingService.readReviews(itemId, type, start, finish, sortBy);
		model.addAttribute("reviews", reviews);
		return getReviewsView();
	}
	
	public String viewReviewForm(HttpServletRequest request, Model model, ReviewForm form, String itemId) {
		Product product = catalogService.findProductById(Long.valueOf(itemId));
		form.setProduct(product);
		model.addAttribute("reviewForm", form);
		return getFormView();
	}
	
	public String reviewItem(HttpServletRequest request, String itemId, RatingType type, Double rating, String reviewText) {
		ratingService.reviewItem(itemId, type, CustomerState.getCustomer(), rating, reviewText);
		return reviewsRedirect;
	}
	
	public String rateItem(HttpServletRequest request, String itemId, RatingType type, Double rating) {
		//TODO
		ratingService.rateItem(itemId, type, CustomerState.getCustomer(), rating);
		return "";
	}
	
	public String voteReviewAsHelpful(HttpServletRequest request, Model model, Long reviewId, String helpful) {
		boolean isHelpful = StringUtils.equalsIgnoreCase("YES", helpful);
		ratingService.markReviewHelpful(reviewId, CustomerState.getCustomer(), isHelpful);
		return "";
	}
	
	public String getReviewsView() {
		return reviewsView;
	}
	
	public String getFormView() {
		return formView;
	}
	
}
