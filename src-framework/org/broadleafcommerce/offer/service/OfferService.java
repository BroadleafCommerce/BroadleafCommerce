package org.broadleafcommerce.offer.service;

import java.util.List;

import org.broadleafcommerce.offer.domain.Offer;
import org.broadleafcommerce.offer.domain.OfferCode;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.profile.domain.Customer;

/**
 * The Interface OfferService.
 */
public interface OfferService {

    /**
     * Lookup offer by code.
     * @param code the code
     * @return the offer
     */
    public Offer lookupOfferByCode(String code);

    /**
     * Consume offer. This method is used at order submit time to finalize the
     * offers that are used. Until this method is called all offers are not used
     * and may be used again if the order is not submitted and the offers not
     * consumed.
     * @param offer the offer
     * @param customer the customer
     * @return true, if successful
     */
    public boolean consumeOffer(Offer offer, Customer customer);

    /**
     * Lookup valid offers for system. Find the offers for a given system.
     * @param system the system
     * @return the list< offer>
     */
    public List<Offer> lookupValidOffersForSystem(String system);

    /**
     * Lookup code by offer.
     * @param offer the offer
     * @return the offer code
     */
    public OfferCode lookupCodeByOffer(Offer offer);

    /**
     * Apply offers to order.
     * @param offers the offers
     * @param order the order
     */
    public void applyOffersToOrder(List<Offer> offers, Order order);
}
