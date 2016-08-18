/*
 * #%L
 * broadleaf-enterprise
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt).
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * 
 * NOTICE:  All information contained herein is, and remains
 * the property of Broadleaf Commerce, LLC
 * The intellectual and technical concepts contained
 * herein are proprietary to Broadleaf Commerce, LLC
 * and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Broadleaf Commerce, LLC.
 * #L%
 */
package org.broadleafcommerce.core.promotionMessage.service;


import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.promotionMessage.domain.PromotionMessage;
import org.broadleafcommerce.core.promotionMessage.dto.PromotionMessageDTO;

import java.util.List;
import java.util.Map;

/**
 * A {@link PromotionMessageGenerator} understands how to gather applicable {@link PromotionMessage}s
 *  for the given {@link Product}.
 * 
 * @author Chris Kittrell (ckittrell)
 */
public interface PromotionMessageGenerator {
    
    public Map<String, List<PromotionMessageDTO>> generatePromotionMessages(Product product);

}
