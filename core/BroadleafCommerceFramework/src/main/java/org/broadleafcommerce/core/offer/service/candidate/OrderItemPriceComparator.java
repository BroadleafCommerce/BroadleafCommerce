package org.broadleafcommerce.core.offer.service.candidate;

import java.util.Comparator;

import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.money.Money;

public class OrderItemPriceComparator implements Comparator<OrderItem> {
	
	private Offer offer;
	
	public OrderItemPriceComparator(Offer offer) {
		this.offer = offer;
	}

	public int compare(OrderItem c1, OrderItem c2) {
		
		Money price = c1.getPriceBeforeAdjustments(offer.getApplyDiscountToSalePrice());
		Money price2 = c2.getPriceBeforeAdjustments(offer.getApplyDiscountToSalePrice());
		
		// highest amount first
		return price2.compareTo(price);
	}

}
