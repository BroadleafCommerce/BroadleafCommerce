/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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
package org.broadleafcommerce.core.rating.domain;

import org.broadleafcommerce.core.rating.service.type.ReviewStatusType;
import org.broadleafcommerce.profile.core.domain.Customer;

import java.util.Date;
import java.util.List;

public interface ReviewDetail {

    Long getId();
    Customer getCustomer();
    String getReviewText();
    void setReviewText(String reviewText);
    Date getReviewSubmittedDate();
    Integer helpfulCount();
    Integer notHelpfulCount();
    ReviewStatusType getStatus();
    RatingSummary getRatingSummary();
    RatingDetail getRatingDetail();
    List<ReviewFeedback> getReviewFeedback();

}
