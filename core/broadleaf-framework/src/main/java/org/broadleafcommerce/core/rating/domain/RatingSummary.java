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

import org.broadleafcommerce.core.rating.service.type.RatingType;

import java.util.List;

public interface RatingSummary {
    
    public Long getId();
    
    public void setId(Long id);
    
    public RatingType getRatingType();
    
    public void setRatingType(RatingType ratingType);
    
    public String getItemId();
    
    public void setItemId(String itemId);
    
    public Integer getNumberOfRatings();
    
    public Integer getNumberOfReviews();
    
    public Double getAverageRating();
    
    public void resetAverageRating();

    public List<ReviewDetail> getReviews();
    
    public void setReviews(List<ReviewDetail> reviews);
    
    public List<RatingDetail> getRatings();
    
    public void setRatings(List<RatingDetail> ratings);

}
