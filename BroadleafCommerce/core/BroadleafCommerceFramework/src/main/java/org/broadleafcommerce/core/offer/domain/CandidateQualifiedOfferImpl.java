package org.broadleafcommerce.core.offer.domain;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Transient;

import org.broadleafcommerce.core.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.money.Money;

public abstract class CandidateQualifiedOfferImpl implements CandidateQualifiedOffer {

	private static final long serialVersionUID = 1L;
	
	@Transient
    private HashMap<OfferItemCriteria, List<OrderItem>> candidateQualifiersMap = new HashMap<OfferItemCriteria, List<OrderItem>>();
	private Money potentialSavings; 
	
	public HashMap<OfferItemCriteria, List<OrderItem>> getCandidateQualifiersMap() {
		return candidateQualifiersMap;
	}

	public void setCandidateQualifiersMap(HashMap<OfferItemCriteria, List<OrderItem>> candidateItemsMap) {
		this.candidateQualifiersMap = candidateItemsMap;
	}
	
	public Money getPotentialSavings() {
		if (potentialSavings == null) {
			potentialSavings = calculatePotentialSavings();
		}
		return potentialSavings;
	}

	/**
	 * This method determines how much the customer might save using this promotion for the
	 * purpose of sorting promotions with the same priority.  
	 *  
	 * If two promotions have the same priority, the one with the highest potential savings
	 * will be used as the tie-breaker to determine the order to apply promotions.
	 * 
	 * This method makes a good approximation of the promotion value as determining the exact value
	 * would require all permutations of promotions to be run resulting in a costly 
	 * operation.
	 * 
	 * @return
	 */
	protected Money calculatePotentialSavings() {
		Money savings = new Money(0);
		int maxUses = calculateMaximumNumberOfUses();
		int appliedCount = 0;
		
		for (OfferItemCriteria itemCriteria : candidateQualifiersMap.keySet()) {
			List<OrderItem> targetItems = candidateQualifiersMap.get(itemCriteria);
			for(OrderItem chgItem : targetItems) {
				int qtyToReceiveSavings = Math.min(chgItem.getQuantity(), maxUses);
				savings = calculateSavingsForOrderItem(chgItem, qtyToReceiveSavings);

				appliedCount = appliedCount + qtyToReceiveSavings;
				if (appliedCount >= maxUses) {
					return savings;
				}
			}
		}
		return savings;
	}

	public Money calculateSavingsForOrderItem(OrderItem chgItem, int qtyToReceiveSavings) {
		Money savings = new Money(0);
		Money salesPrice = chgItem.getPriceBeforeAdjustments(getOffer().getApplyDiscountToSalePrice());
		if (getOffer().getDiscountType().equals(OfferDiscountType.AMOUNT_OFF)) {
			//Price reduction by a fixed amount
			savings = savings.add(new Money(getOffer().getValue()).multiply(qtyToReceiveSavings));
		} else if (getOffer().getDiscountType().equals(OfferDiscountType.PERCENT_OFF)){
			//Price reduction by a percent off
			BigDecimal savingsPercent = getOffer().getValue().divide(new BigDecimal(100));
			savings = savings.add(salesPrice.multiply(savingsPercent).multiply(qtyToReceiveSavings));
		} else {
			//Different price (presumably less than the normal price)
			savings = savings.add(salesPrice.multiply(qtyToReceiveSavings).subtract(new Money(getOffer().getValue()).multiply(qtyToReceiveSavings)));
		}
		return savings;
	}
	
	/**
	 * Determines the maximum number of times this promotion can be used based on the
	 * ItemCriteria and promotion's maxQty setting.
	 */
	private int calculateMaximumNumberOfUses() {		
		int maxMatchesFound = 9999; // set arbitrarily high / algorithm will adjust down	
		
		for (OfferItemCriteria itemCriteria : getOffer().getQualifyingItemCriteria())  {
			int numberOfUsesForThisItemCriteria = calculateMaxUsesForItemCriteria(itemCriteria, getOffer());
			maxMatchesFound = Math.min(maxMatchesFound, numberOfUsesForThisItemCriteria);
		}
		return Math.min(maxMatchesFound, getOffer().getMaxUses());
	}
	
	private int calculateMaxUsesForItemCriteria(OfferItemCriteria itemCriteria, Offer promotion) {
		int numberOfQualifiers = 0;
		
		List<OrderItem> potentialTargets = getCandidateQualifiersMap().get(itemCriteria);
		if (potentialTargets != null) {
			for(OrderItem potentialTarget : potentialTargets) {
				numberOfQualifiers = numberOfQualifiers + potentialTarget.getQuantityAvailableToBeUsedAsQualifier(promotion);
			}
		}
		
		int numberOfUsesForThisItemCriteria = numberOfQualifiers / itemCriteria.getRequiresQuantity();
		return numberOfUsesForThisItemCriteria;
	}
}
