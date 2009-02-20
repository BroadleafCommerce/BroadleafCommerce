package org.broadleafcommerce.promotion.service;

import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.promotion.domain.Promotion;

public interface PromotionService {

	Order applyPromotionToOrder(Order order, Promotion promotion);
}
