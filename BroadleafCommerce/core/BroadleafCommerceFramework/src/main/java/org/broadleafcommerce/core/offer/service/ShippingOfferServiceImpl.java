package org.broadleafcommerce.core.offer.service;

import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.springframework.stereotype.Service;

@Service("blShippingOfferService")
public class ShippingOfferServiceImpl implements ShippingOfferService {
	
	@Resource(name="blOfferService")
	protected OfferService offerService;

	public void reviewOffers(Order order) throws PricingException {
		List<Offer> offers = offerService.buildOfferListForOrder(order);
        offerService.applyFulfillmentGroupOffersToOrder(offers, order);
	}

}
