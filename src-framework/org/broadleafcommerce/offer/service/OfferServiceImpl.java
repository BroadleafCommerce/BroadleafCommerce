package org.broadleafcommerce.offer.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.ComparatorUtils;
import org.apache.commons.collections.map.LRUMap;
import org.broadleafcommerce.offer.domain.CandidateItemOffer;
import org.broadleafcommerce.offer.domain.CandidateItemOfferImpl;
import org.broadleafcommerce.offer.domain.Offer;
import org.broadleafcommerce.offer.domain.OfferCode;
import org.broadleafcommerce.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.offer.service.type.OfferType;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.pricing.service.PricingService;
import org.broadleafcommerce.pricing.service.exception.PricingException;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.util.money.Money;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.springframework.stereotype.Service;

/**
 * The Class OfferServiceImpl.
 */
@Service("offerService")
public class OfferServiceImpl implements OfferService {

    private static final LRUMap expressionCache = new LRUMap(100);

    @Resource
    private PricingService pricingService;

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.offer.service.OfferService#consumeOffer(org.broadleafcommerce.offer.domain.Offer, org.broadleafcommerce.profile.domain.Customer)
     */
    @Override
    public boolean consumeOffer(Offer offer, Customer customer) {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.offer.service.OfferService#applyOffersToOrder(java.util.List, org.broadleafcommerce.order.domain.Order)
     */
    @SuppressWarnings("unchecked")
    public void applyOffersToOrder(List<Offer> offers, Order order) throws PricingException {
        List<Offer> qualifiedOrderOffers = new ArrayList<Offer>();
        List<CandidateItemOffer> qualifiedItemOffers = new ArrayList<CandidateItemOffer>();
        order.removeAllOffers();
        order.setCandidateOffers(new ArrayList<Offer>());
        order = pricingService.executePricing(order);
        List<Offer> offersWithValidDates = removeOutOfDateOffers(offers);
        if (offersWithValidDates != null && ! offersWithValidDates.isEmpty()) {

            //
            // . Pass One:
            //
            for (Offer offer : offersWithValidDates) {
                //
                // . Evaluate all offers and compute their discount amount as if they were the only offer on the order
                //
                if(offer.getType().equals(OfferType.ORDER)){
                    if (couldOfferApply(offer, order)) {
                        offer = calculateAtomOfferDiscount(offer, order.getSubTotal());
                        qualifiedOrderOffers.add(offer);
                    }
                } else if(offer.getType().equals(OfferType.ORDER_ITEM)){
                    for (OrderItem orderItem : order.getOrderItems()) {
                        if(couldOfferApply(offer, order, orderItem)) {
                            CandidateItemOffer candidateOffer = new CandidateItemOfferImpl(orderItem, offer);
                            //orderItem.addCandidateItemOffer(candidateOffer);
                            qualifiedItemOffers.add(candidateOffer);
                        }
                    }
                } else if(offer.getType().equals(OfferType.FULFILLMENT_GROUP)){
                    // TODO: Handle Offer calculation for offer type of fullfillment group
                }
            }
            //
            // . Create a sorted list sorted by priority asc then amount desc
            //
            Collections.sort(qualifiedOrderOffers, ComparatorUtils.reversedComparator(new BeanComparator("discountedPrice")));
            Collections.sort(qualifiedOrderOffers, new BeanComparator("priority"));

            Collections.sort(qualifiedItemOffers, ComparatorUtils.reversedComparator(new BeanComparator("discountedPrice")));
            Collections.sort(qualifiedItemOffers, new BeanComparator("priority"));

            //
            // . Add offers that could be used on the order to the order.candidateOffers and item.candidateOffers lists respectively
            //
            order.setCandidateOffers(qualifiedOrderOffers);

            // Iterate through the collection of CandiateItemOffers. Remember that each one is an offer that may apply to a
            // particular OrderItem.  Multiple CandidateItemOffers may contain a reference to the same OrderItem object.
            for (CandidateItemOffer itemOffer : qualifiedItemOffers) {
                OrderItem orderItem = itemOffer.getOrderItem();
                //- Determine the amount that should be discounted for each item
                //----- If the items sale price is better than the discounted price, don't apply
                if(itemOffer.getDiscountedPrice().greaterThan(orderItem.getSalePrice())){
                    // TODO: ----- If the offer requires other items, check to see if the items are still unmarked
                    if(requiresMultipleSkus(itemOffer)){
                        // TODO: apply offer to other skus
                    }
                    // ----- If the item itself has been marked by another discount then don't apply this offer unless the offer's applyDiscountToMarkedItems = true (edge case)
                    if(! orderItem.isMarkedForOffer() ||
                            (orderItem.isMarkedForOffer() && itemOffer.getOffer().isApplyDiscountToMarkedItems())){
                        //----- If the item already has a discount
                        if(orderItem.isMarkedForOffer()){
                            //  and this offer is stackable, apply on top of the existing offer
                            if(itemOffer.getOffer().isStackable()){
                                //----- Create corresponding item adjustments records and if (markItems == true) then mark the items used so that this offer is possible
                                applyItemOffer(orderItem,itemOffer);
                            }
                            // and this offer is not-stackable, don't apply
                        }else{
                            //----- Create corresponding item adjustments records and if (markItems == true) then mark the items used so that this offer is possible
                            applyItemOffer(orderItem,itemOffer);
                        }
                    }
                }
            }

            Money newOrderTotal = pricingService.executePricing(order).getSubTotal();

            // TODO: How and what do we do with order level offers?
            for (Offer offer : qualifiedOrderOffers) {
                //- Determine the amount that should be discounted for each item
                //----- If the order sale price is better than the discounted price, don't apply
                if(newOrderTotal.greaterThan(order.getSubTotal())){
                    // ----- If the order itself has been marked by another discount then don't apply this offer unless the offer's applyDiscountToMarkedItems = true (edge case)
                    if(! order.isMarkedForOffer() ||
                            (order.isMarkedForOffer() && offer.isApplyDiscountToMarkedItems())){
                        //----- If the order already has a discount
                        if(order.isMarkedForOffer()){
                            //  and this offer is stackable, apply on top of the existing offer
                            if(offer.isStackable()){
                                //----- Create corresponding item adjustments records and if (markItems == true) then mark the items used so that this offer is possible
                                applyOrderOffer(order,offer);
                            }
                            // and this offer is not-stackable, don't apply

                        }else{
                            //----- Create corresponding item adjustments records and if (markItems == true) then mark the items used so that this offer is possible
                            applyOrderOffer(order,offer);
                        }
                    }
                }
            }
        }
    }

    private boolean requiresMultipleSkus(CandidateItemOffer itemOffer){
        // TODO: Add determination code for offer requiring multiple skus
        // Assume offer does not for now
        return false;
    }

    private void applyItemOffer(OrderItem orderItem, CandidateItemOffer itemOffer){
        // TODO: Apply item offer
        //----- Create corresponding item adjustments records and if (markItems == true) then mark the items used so that this offer is possible
        orderItem.setMarkedForOffer(true);
    }

    private void applyOrderOffer(Order order, Offer offer){
        // TODO: Apply order offer
        order.setMarkedForOffer(true);
    }

    private Offer calculateAtomOfferDiscount(Offer offer, Money startingValue){
        if(offer.getDiscountType().equals(OfferDiscountType.AMOUNT_OFF)){
            offer.setDiscountPrice(startingValue.subtract(offer.getValue()));
        } else if(offer.getDiscountType().equals(OfferDiscountType.FIX_PRICE)){
            offer.setDiscountPrice(offer.getValue());
        } else if(offer.getDiscountType().equals(OfferDiscountType.PERCENT_OFF)){
            offer.setDiscountPrice(startingValue.multiply(Money.toAmount((offer.getValue().divide(new BigDecimal("100"))))));
        }
        return offer;
    }

    private List<Offer> removeOutOfDateOffers(List<Offer> offers){
        Date now = new Date();
        for (Offer offer : offers) {
            if(offer.getStartDate()!= null && offer.getStartDate().after(now)){
                offers.remove(offer);
            }else
                if(offer.getEndDate()!= null && offer.getEndDate().before(now)){
                    offers.remove(offer);
                }
        }
        return offers;
    }

    private boolean couldOfferApply(Offer offer, Order order) {
        return couldOfferApply(offer, order, null);
    }

    private boolean couldOfferApply(Offer offer, Order order, OrderItem item) {
        if (offer.getAppliesToRules() != null && offer.getAppliesToRules().length() != 0) {
            Serializable exp = (Serializable)expressionCache.get(offer.getAppliesToRules());
            if (exp == null) {
                ParserContext context = new ParserContext();
                context.addImport("OfferType", OfferType.class);
                exp = MVEL.compileExpression(offer.getAppliesToRules(), context);
            }
            expressionCache.put(offer.getAppliesToRules(), exp);

            HashMap<String, Object> vars = new HashMap<String, Object>();
            vars.put("order", order);
            vars.put("offer", offer);
            if (item != null) {
                vars.put("item", item);
            }
            Boolean expressionOutcome = (Boolean)MVEL.executeExpression(exp, vars);
            if (expressionOutcome != null && expressionOutcome) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.offer.service.OfferService#lookupCodeByOffer(org.broadleafcommerce.offer.domain.Offer)
     */
    @Override
    public OfferCode lookupCodeByOffer(Offer offer) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.offer.service.OfferService#lookupOfferByCode(java.lang.String)
     */
    @Override
    public Offer lookupOfferByCode(String code) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.offer.service.OfferService#lookupValidOffersForSystem(java.lang.String)
     */
    @Override
    public List<Offer> lookupValidOffersForSystem(String system) {
        // TODO Auto-generated method stub
        return null;
    }
}
