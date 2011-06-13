package org.broadleafcommerce.core.order.service.manipulation;

import org.broadleafcommerce.core.order.domain.BundleOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.DynamicPriceDiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.GiftWrapOrderItem;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;

public interface OrderItemVisitor {
	
	public void visit(OrderItem orderItem) throws PricingException;
	public void visit(BundleOrderItem bundleOrderItem) throws PricingException;
	public void visit(DiscreteOrderItem discreteOrderItem) throws PricingException;
	public void visit(DynamicPriceDiscreteOrderItem dynamicPriceDiscreteOrderItem) throws PricingException;
	public void visit(GiftWrapOrderItem giftWrapOrderItem) throws PricingException;
	
}
