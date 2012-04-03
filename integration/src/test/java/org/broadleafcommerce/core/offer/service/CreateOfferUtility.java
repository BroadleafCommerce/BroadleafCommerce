package org.broadleafcommerce.core.offer.service;

import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.core.offer.dao.OfferCodeDao;
import org.broadleafcommerce.core.offer.dao.OfferDao;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferCode;
import org.broadleafcommerce.core.offer.service.type.OfferDeliveryType;
import org.broadleafcommerce.core.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.core.offer.service.type.OfferType;

import java.math.BigDecimal;
import java.util.Calendar;

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
        offer.setAppliesToOrderRules(orderRule);
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