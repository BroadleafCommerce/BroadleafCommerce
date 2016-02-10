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

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.persistence.Status;
import org.broadleafcommerce.core.offer.service.type.OfferDeliveryType;
import org.broadleafcommerce.core.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.core.offer.service.type.OfferItemRestrictionRuleType;
import org.broadleafcommerce.core.offer.service.type.OfferType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This interface can be used with an Offer implementation to indicate that an offer supports minimum required targets.
 * 
 * If set, the offer will not be applied unless the order contains at least the getMinimumRequired() number
 * of eligible targets.
 * 
 * For example, if this was set to 10 and an offer's target criteria was "item in category hot-sauces", then,
 * if the user added 9 hot-sauces to the cart, it would not apply.
 * 
 * If they added 10 or more, it would apply.  
 * 
 * This is different than using the quantity on the OrderItemCriteria.    If the OrderItemCriteria.quantity was
 * set to 10, the discount would be applied only for quantities in increments of 
 * 10 (so yes for 10, 20, etc. but not for 11, 12, etc.)
 * 
 * @author bpolster
 *
 */
public interface MinimumTargetsRequired {
    /**
     * The minimum number of targets required
     * @return
     */
    Integer getMinimumTargetsRequired();
}
