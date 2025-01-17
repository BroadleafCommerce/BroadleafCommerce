/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.core.rating.service;

import org.broadleafcommerce.core.rating.domain.RatingSummary;
import org.broadleafcommerce.core.rating.domain.ReviewDetail;
import org.broadleafcommerce.core.rating.service.type.RatingSortType;
import org.broadleafcommerce.core.rating.service.type.RatingType;
import org.broadleafcommerce.profile.core.domain.Customer;

import java.util.List;
import java.util.Map;

public interface RatingService {

    RatingSummary saveRatingSummary(RatingSummary rating);

    void deleteRatingSummary(RatingSummary rating);

    RatingSummary readRatingSummary(String itemId, RatingType type);

    Map<String, RatingSummary> readRatingSummaries(List<String> itemIds, RatingType type);

    void rateItem(String itemId, RatingType type, Customer customer, Double rating);

    List<ReviewDetail> readReviews(String itemId, RatingType type, int start, int finish, RatingSortType sortBy);

    void reviewItem(String itemId, RatingType type, Customer customer, Double rating, String reviewText);

    void markReviewHelpful(Long reviewId, Customer customer, Boolean helpful);

    /**
     * Reads a ReviewDetail by the given customer and the itemId
     *
     * @param itemId
     * @param customer
     * @return review, or null if review is not found
     */
    ReviewDetail readReviewByCustomerAndItem(Customer customer, String itemId);

}
