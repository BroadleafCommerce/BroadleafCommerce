package org.broadleafcommerce.offer.service;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.offer.domain.Offer;
import org.broadleafcommerce.offer.domain.OfferAudit;
import org.broadleafcommerce.offer.domain.OfferCode;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.broadleafcommerce.type.OfferDiscountType;
import org.broadleafcommerce.util.DateUtil;
import org.springframework.stereotype.Service;

@Service("offerService")
public class OfferServiceImpl implements OfferService {

	@Resource
	private EntityConfiguration entityConfiguration;
	
	@Override
	public boolean consumeOffer(Offer offer, Customer customer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<OfferAudit> findAppliedOffers(List<Offer> candidateOffers,
			OrderItem orderItem) {
		List<OfferAudit> foundOffers = new ArrayList<OfferAudit>();
		
		for (Offer candOffer : candidateOffers) {
			if(candOffer.isStackable()){  				// If the offer is stackable, add it to applied
				foundOffers.add(createOfferAuditFromOffer(candOffer,orderItem));
			}else										// Start logic to compare offer to applied offers
			if(foundOffers.size() > 0){ 				// If we have applied offers already
				for (OfferAudit appOfferAudit : foundOffers) { 	// iterate through the applied offers
					Offer appOffer = appOfferAudit.getOffer();
					if(!appOffer.isStackable()){	   	// Only examine non-stackable offers					
						if(!(appOffer.getPriority() == candOffer.getPriority())){ // Test equal priority
							if(isCandOfferHasPriorityOverAppOffer(appOffer,candOffer)){ // Test greater priority
								foundOffers.remove(appOfferAudit);
								foundOffers.add(createOfferAuditFromOffer(candOffer,orderItem));
							}
						}else {							// Priorities are equal
							if(isCandOfferGreaterThanAppOffer(candOffer, appOffer, orderItem.getRetailPrice())){
								foundOffers.remove(appOfferAudit);
								foundOffers.add(createOfferAuditFromOffer(candOffer,orderItem));
							}
						}
						
					}
				}				
			}else{ 									// No applied offers yet
				foundOffers.add(createOfferAuditFromOffer(candOffer,orderItem));			// Add one
			}
		}
		return foundOffers;

	}

	@Override
	public OfferCode lookupCodeByOffer(Offer offer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Offer lookupOfferByCode(String code) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Offer> lookupValidOffersForSystem(String system) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private boolean isCandOfferHasPriorityOverAppOffer(Offer appliedOffer,Offer candidateOffer){
		if(candidateOffer.getPriority() > appliedOffer.getPriority()){
			return true;
		}else{
			return false;
		}
		
	}

	private boolean isCandOfferGreaterThanAppOffer(Offer candidateOffer, Offer appliedOffer, BigDecimal price){
		BigDecimal candAmount = getOfferAmount(candidateOffer, price);
		BigDecimal appAmount = getOfferAmount(appliedOffer, price);
		return candAmount.compareTo(appAmount) > 0;
	}
	
	private BigDecimal getOfferAmount(Offer offer, BigDecimal price){
		if(offer.getDiscountType() == OfferDiscountType.AMOUNT_OFF ){
			return price.subtract(offer.getValue());
		}			
		if(offer.getDiscountType() == OfferDiscountType.FIX_PRICE){
			return offer.getValue();
		}

		if(offer.getDiscountType() == OfferDiscountType.PERCENT_OFF){
			return price.multiply(offer.getValue().divide(new BigDecimal("100")));
		}
		return price;
		
	}
	
	private OfferAudit createOfferAuditFromOffer(Offer offer,OrderItem orderItem){
		OfferAudit oa = (OfferAudit)entityConfiguration.createEntityInstance("offerAudit");
		oa.setOffer(offer);
		oa.setRedeemedDate(new Date(DateUtil.getNow().getTime()));
		oa.setRelatedId(orderItem.getId());
		oa.setRelatedPrice(getOfferAmount(offer, orderItem.getRetailPrice()));
		oa.setRelatedRetailPrice(orderItem.getRetailPrice());
		oa.setRelatedSalePrice(orderItem.getSalePrice());
		oa.setOfferCodeId(lookupCodeByOffer(offer).getId());
		return oa;
	}
	

}
