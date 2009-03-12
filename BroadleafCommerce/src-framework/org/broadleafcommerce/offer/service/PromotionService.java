package org.broadleafcommerce.offer.service;

import org.broadleafcommerce.offer.domain.Promotion;
import org.broadleafcommerce.order.domain.Order;

public interface PromotionService {

	Order applyPromotionToOrder(Order order, Promotion promotion);
}
