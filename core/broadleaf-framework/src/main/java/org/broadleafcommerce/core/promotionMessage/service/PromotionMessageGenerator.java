/*
 * #%L
 * broadleaf-enterprise
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.promotionMessage.service;


import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.promotionMessage.domain.PromotionMessage;
import org.broadleafcommerce.core.promotionMessage.dto.PromotionMessageDTO;

import java.util.List;
import java.util.Map;

/**
 * A {@link PromotionMessageGenerator} understands how to gather applicable {@link PromotionMessage}s
 *  for the given {@link Product} or {@link OrderItem}.
 * 
 * @author Chris Kittrell (ckittrell)
 */
public interface PromotionMessageGenerator {
    
    Map<String, List<PromotionMessageDTO>> generatePromotionMessages(Product product);

    List<String> generatePromotionMessages(OrderItem orderItem);

}
