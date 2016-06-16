/*
 * #%L
 * BroadleafCommerce Integration
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
package org.broadleafcommerce.core.offer.service;

import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.core.offer.dao.OfferCodeDao;
import org.broadleafcommerce.core.offer.dao.OfferDao;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferCode;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteria;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteriaImpl;
import org.broadleafcommerce.core.offer.domain.OfferQualifyingCriteriaXref;
import org.broadleafcommerce.core.offer.domain.OfferQualifyingCriteriaXrefImpl;
import org.broadleafcommerce.core.offer.domain.OfferTargetCriteriaXref;
import org.broadleafcommerce.core.offer.domain.OfferTargetCriteriaXrefImpl;
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

    public OfferCode createOfferCode(String offerName, OfferType offerType, OfferDiscountType discountType, double value,
            String orderRule, boolean stackable, boolean combinable, int priority) {
        return createOfferCode("NONAME", offerName, offerType, discountType, value, orderRule, stackable, combinable, priority, null);
    }

    public OfferCode createOfferCode(String offerCodeName, String offerName, OfferType offerType, OfferDiscountType discountType, double value, String orderRule, boolean stackable, boolean combinable, int priority, String qualifierRule) {
        OfferCode offerCode = offerCodeDao.create();
        Offer offer = createOffer(offerName, offerType, discountType, value, orderRule, stackable, combinable, priority, qualifierRule);
        offerCode.setOffer(offer);
        offerCode.setOfferCode(offerCodeName);
        offerCode = offerService.saveOfferCode(offerCode);
        return offerCode;
    }

    public Offer createOffer(String offerName, OfferType offerType, OfferDiscountType discountType, double value,
            String orderRule, boolean stackable, boolean combinable, int priority, String qualifierRule) {
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

        if (stackable) {
            offer.setOfferItemTargetRuleType(OfferItemRestrictionRuleType.QUALIFIER_TARGET);
        }

        OfferItemCriteria oic = new OfferItemCriteriaImpl();
        oic.setQuantity(1);
        oic.setMatchRule(orderRule);

        OfferTargetCriteriaXref targetXref = new OfferTargetCriteriaXrefImpl();
        targetXref.setOffer(offer);
        targetXref.setOfferItemCriteria(oic);

        offer.setTargetItemCriteriaXref(Collections.singleton(targetXref));
        offer.setCombinableWithOtherOffers(combinable);

        if (qualifierRule != null) {
            OfferItemCriteria qoic = new OfferItemCriteriaImpl();
            qoic.setQuantity(1);
            qoic.setMatchRule(qualifierRule);

            OfferQualifyingCriteriaXref qualifyingXref = new OfferQualifyingCriteriaXrefImpl();
            qualifyingXref.setOffer(offer);
            qualifyingXref.setOfferItemCriteria(qoic);

            offer.setQualifyingItemCriteriaXref(Collections.singleton(qualifyingXref));

            offer.setOfferItemQualifierRuleType(OfferItemRestrictionRuleType.QUALIFIER_TARGET);
        }
        offer.setPriority(priority);
        offer = offerService.save(offer);
        offer.setMaxUsesPerOrder(50);
        return offer;
    }

    public Offer updateOfferCodeMaxCustomerUses(OfferCode code, Long maxUses) {
        code.getOffer().setMaxUsesPerCustomer(maxUses);
        return offerService.save(code.getOffer());
    }
}
