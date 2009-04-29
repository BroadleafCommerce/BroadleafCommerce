package org.broadleafcommerce.offer.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.broadleafcommerce.order.service.type.FulfillmentGroupType;
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
    private static final StringBuffer functions = new StringBuffer();

    @Resource
    private PricingService pricingService;

    static {
        InputStream is = OfferServiceImpl.class.getResourceAsStream("/org/broadleafcommerce/offer/service/mvelFunctions.mvel");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                functions.append(line);
            }
            functions.append(" ");
        } catch(Exception e){
            throw new RuntimeException(e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e){}
            }
        }
    }

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
                            orderItem.addCandidateItemOffer(candidateOffer);
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
            Collections.sort(qualifiedOrderOffers, ComparatorUtils.reversedComparator(new BeanComparator("discountPrice")));
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
                if(itemOffer.getDiscountedPrice().lessThan(orderItem.getSalePrice())){
                    // ----- If the item itself has been marked by another discount then don't apply this offer unless the offer's applyDiscountToMarkedItems = true (edge case)
                    if(! orderItem.isAllQuantityMarkedForOffer() || itemOffer.getOffer().isApplyDiscountToMarkedItems()) {
                        //----- If the item already has a discount
                        if(orderItem.getAppliedItemOffers() != null && orderItem.getAppliedItemOffers().size() > 0){
                            //  and this offer is stackable, apply on top of the existing offer
                            if(itemOffer.getOffer().isStackable()){
                                //----- Create corresponding item adjustments records and if (markItems == true) then mark the items used so that this offer is possible
                                applyItemOffer(itemOffer, order);
                            }
                            // and this offer is not-stackable, don't apply
                        } else {
                            //----- Create corresponding item adjustments records and if (markItems == true) then mark the items used so that this offer is possible
                            applyItemOffer(itemOffer, order);
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

    private void applyItemOffer(CandidateItemOffer itemOffer, Order order){
        //----- Create corresponding item adjustments records and if (markItems == true) then mark the items used so that this offer is possible
        String expression = itemOffer.getOffer().getAppliesToCustomerRules();
        if (expression != null && expression.indexOf("orderContainsPlusMark") >= 0) { //We know that they evaluated multiple items
            HashMap<String, Object> vars = new HashMap<String, Object>();
            vars.put("currentItem", itemOffer.getOrderItem());
            vars.put("order", order);
            vars.put("offer", itemOffer.getOffer());
            vars.put("doMark", Boolean.TRUE); //This will allow the orderContainsPlusMark function to mark the items
            Boolean result = (Boolean)executeExpression(expression, vars);
            if (result) {
                itemOffer.getOrderItem().addAppliedItemOffer(itemOffer.getOffer());  //This is how we can tell if an item has been discounted
            }
        } else {
            itemOffer.getOrderItem().addAppliedItemOffer(itemOffer.getOffer());  //This is how we can tell if an item has been discounted
        }
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
            } else if(offer.getEndDate()!= null && offer.getEndDate().before(now)){
                offers.remove(offer);
            }
        }
        return offers;
    }

    private boolean couldOfferApply(Offer offer, Order order) {
        return couldOfferApply(offer, order, null);
    }

    private boolean couldOfferApply(Offer offer, Order order, OrderItem currentItem) {
        boolean appliesToItem = false;
        boolean appliesToCustomer = false;

        if (offer.getAppliesToItemRules() != null && offer.getAppliesToItemRules().length() != 0) {

            HashMap<String, Object> vars = new HashMap<String, Object>();
            vars.put("doMark", Boolean.FALSE); //We never want to mark offers when we are checking if they could apply.
            vars.put("order", order);
            vars.put("offer", offer);
            if (currentItem != null) {
                vars.put("currentItem", currentItem);
            }
            Boolean expressionOutcome = (Boolean)executeExpression(offer.getAppliesToItemRules(), vars);
            if (expressionOutcome != null && expressionOutcome) {
                appliesToItem = true;
            }
        } else {
            appliesToItem = true;
        }

        if (offer.getAppliesToCustomerRules() != null && offer.getAppliesToCustomerRules().length() != 0) {

            HashMap<String, Object> vars = new HashMap<String, Object>();
            vars.put("customer", order.getCustomer());
            Boolean expressionOutcome = (Boolean)executeExpression(offer.getAppliesToCustomerRules(), vars);
            if (expressionOutcome != null && expressionOutcome) {
                appliesToCustomer = true;
            }
        } else {
            appliesToCustomer = true;
        }

        return appliesToItem && appliesToCustomer;
    }

    private Object executeExpression(String expression, Map<String, Object> vars) {
        Serializable exp = (Serializable)expressionCache.get(expression);
        if (exp == null) {
            ParserContext context = new ParserContext();
            context.addImport("OfferType", OfferType.class);
            context.addImport("FulfillmentGroupType", FulfillmentGroupType.class);
            StringBuffer completeExpression = new StringBuffer(functions.toString());
            completeExpression.append(" ").append(expression);
            exp = MVEL.compileExpression(completeExpression.toString(), context);
        }
        expressionCache.put(expression, exp);

        return MVEL.executeExpression(exp, vars);

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
