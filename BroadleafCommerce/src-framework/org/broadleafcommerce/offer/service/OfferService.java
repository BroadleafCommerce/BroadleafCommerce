package org.broadleafcommerce.offer.service;

import java.util.List;

import org.broadleafcommerce.offer.domain.Offer;
import org.broadleafcommerce.offer.domain.OfferCode;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.profile.domain.Customer;

public interface OfferService {

    public Offer lookupOfferByCode(String code);

    public boolean consumeOffer(Offer offer, Customer customer);

    public List<Offer> lookupValidOffersForSystem(String system);

    public OfferCode lookupCodeByOffer(Offer offer);

    //public List<OfferAudit> findAppliedOffers(List<Offer> candidateOffers, OrderItem orderItem);

    public void applyOffersToOrder(List<Offer> offers, Order order);
}
