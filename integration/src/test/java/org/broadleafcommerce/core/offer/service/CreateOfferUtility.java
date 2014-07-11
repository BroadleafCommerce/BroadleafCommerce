/*
 * #%L
 * BroadleafCommerce Integration
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
package org.broadleafcommerce.core.offer.service;

import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.core.offer.dao.OfferCodeDao;
import org.broadleafcommerce.core.offer.dao.OfferDao;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferCode;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteria;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteriaImpl;
import org.broadleafcommerce.core.offer.service.type.OfferDeliveryType;
import org.broadleafcommerce.core.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.core.offer.service.type.OfferItemRestrictionRuleType;
import org.broadleafcommerce.core.offer.service.type.OfferType;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collections;

@SuppressWarnings("deprecation")
public class CreateOfferUtility {

    private OfferDao offerDao;
    private OfferCodeDao offerCodeDao;
    private OfferService offerService;

    public CreateOfferUtility(OfferDao offerDao, OfferCodeDao offerCodeDao, OfferService offerService) {
        this.offerDao = offerDao;
        this.offerCodeDao = offerCodeDao;
        this.offerService = offerService;
    }

    public OfferCode createOfferCode(String offerName, OfferType offerType, OfferDiscountType discountType, double value, String customerRule, String orderRule, boolean stackable, boolean combinable, int priority) {
        return createOfferCode("NONAME", offerName, offerType, discountType, value, customerRule, orderRule, stackable, combinable, priority);
    }

    public OfferCode createOfferCode(String offerCodeName, String offerName, OfferType offerType, OfferDiscountType discountType, double value, String customerRule, String orderRule, boolean stackable, boolean combinable, int priority) {
        OfferCode offerCode = offerCodeDao.create();
        Offer offer = createOffer(offerName, offerType, discountType, value, customerRule, orderRule, stackable, combinable, priority);
        offerCode.setOffer(offer);
        offerCode.setOfferCode(offerCodeName);
        offerCode = offerService.saveOfferCode(offerCode);
        return offerCode;
    }

    public Offer createOffer(String offerName, OfferType offerType, OfferDiscountType discountType, double value, String customerRule, String orderRule, boolean stackable, boolean combinable, int priority) {
        Offer offer = offerDao.create();
        offer.setName(offerName);
        offer.setStartDate(SystemTime.asDate());
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        offer.setStartDate(calendar.getTime());
        calendar.add(Calendar.DATE, 2);
        offer.setEndDate(calendar.getTime());
        offer.setType(offerType);
        offer.setDiscountType(discountType);
        offer.setValue(BigDecimal.valueOf(value));
        offer.setDeliveryType(OfferDeliveryType.CODE);
        offer.setStackable(stackable);
        if (stackable) {
            offer.setOfferItemQualifierRuleType(OfferItemRestrictionRuleType.QUALIFIER_TARGET);
            offer.setOfferItemTargetRuleType(OfferItemRestrictionRuleType.QUALIFIER_TARGET);
        }
        
        OfferItemCriteria oic = new OfferItemCriteriaImpl();
        oic.setQuantity(1);
        oic.setMatchRule(orderRule);
        offer.setTargetItemCriteria(Collections.singleton(oic));
        
        offer.setAppliesToCustomerRules(customerRule);
        offer.setCombinableWithOtherOffers(combinable);
        offer.setPriority(priority);
        offer = offerService.save(offer);
        offer.setMaxUses(50);
        return offer;
    }

    public Offer updateOfferCodeMaxCustomerUses(OfferCode code, Long maxUses) {
        code.getOffer().setMaxUsesPerCustomer(maxUses);
        return offerService.save(code.getOffer());
    }
}
