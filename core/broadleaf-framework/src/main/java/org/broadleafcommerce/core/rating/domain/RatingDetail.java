/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.rating.domain;

import org.broadleafcommerce.profile.core.domain.Customer;

import java.util.Date;

public interface RatingDetail {

    public Long getId();
    
    public void setId(Long id);
    
    public Double getRating();
    
    public void setRating(Double newRating);
    
    public Customer getCustomer();
    
    public void setCustomer(Customer customer);
    
    public Date getRatingSubmittedDate();
    
    public void setRatingSubmittedDate(Date ratingSubmittedDate);
    
    public RatingSummary getRatingSummary();
    
    public void setRatingSummary(RatingSummary ratingSummary);

}
