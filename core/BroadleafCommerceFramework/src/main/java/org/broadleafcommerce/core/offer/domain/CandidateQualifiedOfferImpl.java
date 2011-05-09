package org.broadleafcommerce.core.offer.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Transient;

import org.broadleafcommerce.core.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.money.Money;

public abstract class CandidateQualifiedOfferImpl implements CandidateQualifiedOffer {

	private static final long serialVersionUID = 1L;
	
	@Transient
    protected HashMap<OfferItemCriteria, List<OrderItem>> candidateQualifiersMap = new HashMap<OfferItemCriteria, List<OrderItem>>();
	
	@Transient
    protected List<OrderItem> candidateTargets = new ArrayList<OrderItem>();
	
	public HashMap<OfferItemCriteria, List<OrderItem>> getCandidateQualifiersMap() {
		return candidateQualifiersMap;
	}

	public void setCandidateQualifiersMap(HashMap<OfferItemCriteria, List<OrderItem>> candidateItemsMap) {
		this.candidateQualifiersMap = candidateItemsMap;
	}
	
	public List<OrderItem> getCandidateTargets() {
		return candidateTargets;
	}

	public void setCandidateTargets(List<OrderItem> candidateTargets) {
		this.candidateTargets = candidateTargets;
	}

	public Money calculateSavingsForOrderItem(OrderItem orderItem, int qtyToReceiveSavings) {
		Money savings = new Money(0);
		Money salesPrice = orderItem.getPriceBeforeAdjustments(getOffer().getApplyDiscountToSalePrice());
		if (getOffer().getDiscountType().equals(OfferDiscountType.AMOUNT_OFF)) {
			//Price reduction by a fixed amount
			savings = savings.add(new Money(getOffer().getValue()).multiply(qtyToReceiveSavings));
		} else if (getOffer().getDiscountType().equals(OfferDiscountType.PERCENT_OFF)) {
			//Price reduction by a percent off
			BigDecimal savingsPercent = getOffer().getValue().divide(new BigDecimal(100));
			savings = savings.add(salesPrice.multiply(savingsPercent).multiply(qtyToReceiveSavings));
		} else {
			//Different price (presumably less than the normal price)
			savings = savings.add(salesPrice.multiply(qtyToReceiveSavings).subtract(new Money(getOffer().getValue()).multiply(qtyToReceiveSavings)));
		}
		return savings;
	}
}
