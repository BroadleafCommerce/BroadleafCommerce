/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.web.controller.catalog;

import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.rating.domain.ReviewDetail;
import org.broadleafcommerce.core.rating.service.RatingService;
import org.broadleafcommerce.core.rating.service.type.RatingType;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.ui.Model;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

public class BroadleafRatingsController {

    @Resource(name = "blRatingService")
    protected RatingService ratingService;
    @Resource(name = "blCatalogService")
    protected CatalogService catalogService;
    
    protected String formView = "catalog/partials/review";
    protected String successView = "catalog/partials/reviewSuccessful";
    
    
    public String viewReviewForm(HttpServletRequest request, Model model, ReviewForm form, String itemId) {
        Product product = catalogService.findProductById(Long.valueOf(itemId));
        form.setProduct(product);
        ReviewDetail reviewDetail = ratingService.readReviewByCustomerAndItem(CustomerState.getCustomer(), itemId);
        if (reviewDetail != null) {
            form.setReviewText(reviewDetail.getReviewText());
            form.setRating(reviewDetail.getRatingDetail().getRating());
        }
        model.addAttribute("reviewForm", form);
        return getFormView();
    }
    
    public String reviewItem(HttpServletRequest request, Model model, ReviewForm form, String itemId) {
        ratingService.reviewItem(itemId, RatingType.PRODUCT, CustomerState.getCustomer(), form.getRating(), form.getReviewText());
        model.addAttribute("reviewForm", form);
        return getSuccessView();
    }
    
    public String getFormView() {
        return formView;
    }
    
    public String getSuccessView() {
        return successView;
    }
    
}
