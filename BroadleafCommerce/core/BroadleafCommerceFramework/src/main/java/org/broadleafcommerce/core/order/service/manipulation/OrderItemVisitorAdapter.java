package org.broadleafcommerce.core.order.service.manipulation;

import org.broadleafcommerce.core.order.domain.BundleOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.DynamicPriceDiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.GiftWrapOrderItem;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;

public class OrderItemVisitorAdapter implements OrderItemVisitor {

	public void visit(BundleOrderItem bundleOrderItem) throws PricingException {
		//do nothing
	}

	public void visit(DiscreteOrderItem discreteOrderItem) throws PricingException {
		//do nothing
	}

	public void visit(DynamicPriceDiscreteOrderItem dynamicPriceDiscreteOrderItem) throws PricingException {
		//do nothing
	}

	public void visit(GiftWrapOrderItem giftWrapOrderItem) throws PricingException {
		//do nothing
	}

	public void visit(OrderItem orderItem) throws PricingException {
		//do nothing
	}

}
