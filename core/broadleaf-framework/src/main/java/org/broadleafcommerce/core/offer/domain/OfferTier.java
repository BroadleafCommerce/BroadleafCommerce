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
package org.broadleafcommerce.core.offer.domain;


import java.math.BigDecimal;


/**
 * Represents a tier and amount combination for an offer.   For example, an offer might allow a
 * 10% off if a user purchases 1 -5 but then allow 15% off if they purchase more than 5.    
 * @author bpolster
 *
 */
public interface OfferTier extends Comparable<OfferTier>{

    /**
     * Returns the unique id of the offer tier.
     * @return
     */
    Long getId();

    /**
     * Sets the id of the offer tier.
     * @param id
     */
    void setId(Long id);

    /**
     * Returns the amount of the offer.   The amount could be a percentage, fixed price, or amount-off
     * depending on the parent {@link Offer#getDiscountType()}
     * @return
     */
    BigDecimal getAmount();

    /**
     * Sets the amount of the tier.
     * @param amount
     */
    void setAmount(BigDecimal amount);

    /**
     * The minimum number needed to qualify for this tier.
     * @return
     */
    Long getMinQuantity();


    /**
     * Sets the minimum number need to qualify for this tier.
     * @param minQuantity
     */
    void setMinQuantity(Long minQuantity);

    /**
     * Returns the associated offer.
     * @return
     */
    Offer getOffer();

    /**
     * Sets the associated offer.
     * @param offer
     */
    void setOffer(Offer offer);

}
