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

package org.broadleafcommerce.core.offer.domain;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.order.domain.OrderItem;

import java.io.Serializable;

/**
 * OrderItem level offer that has been qualified for an order,
 * but may still be ejected based on additional pricing
 * and stackability concerns once the order has been processed
 * through the promotion engine.
 */
public interface CandidateItemOffer extends Serializable {
    
    public Long getId();

    public void setId(Long id);

    public OrderItem getOrderItem();

    public void setOrderItem(OrderItem orderItem);
    
    public CandidateItemOffer clone();
    
    public void setOffer(Offer offer);

    public int getPriority();

    public Offer getOffer();
    
    public Money getDiscountedPrice();
    
    public void setDiscountedPrice(Money discountedPrice);
    
}
