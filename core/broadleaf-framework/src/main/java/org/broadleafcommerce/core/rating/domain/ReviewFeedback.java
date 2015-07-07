/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.core.rating.domain;

import org.broadleafcommerce.profile.core.domain.Customer;

public interface ReviewFeedback {

    public Long getId();
    public Customer getCustomer();
    public ReviewDetail getReviewDetail();
    public Boolean getIsHelpful();
    public void setIsHelpful(Boolean isHelpful);
    public void setId(Long id);
    public void setCustomer(Customer customer);
    public void setReviewDetail(ReviewDetail reviewDetail);

}
