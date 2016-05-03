/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.rating.dao;

import org.broadleafcommerce.core.rating.domain.RatingDetail;
import org.broadleafcommerce.core.rating.domain.RatingSummary;
import org.broadleafcommerce.core.rating.domain.ReviewDetail;
import org.broadleafcommerce.core.rating.service.type.RatingType;
import org.broadleafcommerce.profile.core.domain.Customer;

import java.util.Date;
import java.util.List;

public interface RatingSummaryDao {

    public RatingSummary createSummary();
    
    public RatingSummary createSummary(String itemId, RatingType type);
    
    public RatingDetail createDetail();
    
    public RatingDetail createDetail(RatingSummary ratingSummary, Double rating, Date submittedDate, Customer customer);
    
    RatingSummary readRatingSummary(String itemId, RatingType type);
    List<RatingSummary> readRatingSummaries(List<String> itemIds, RatingType type);
    RatingSummary saveRatingSummary(RatingSummary summary);
    void deleteRatingSummary(RatingSummary summary);

    RatingDetail readRating(Long customerId, Long ratingSummaryId);
    ReviewDetail readReview(Long customerId, Long ratingSummaryId);
}
