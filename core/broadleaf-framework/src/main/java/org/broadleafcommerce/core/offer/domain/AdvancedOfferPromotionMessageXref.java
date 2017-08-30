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
package org.broadleafcommerce.core.offer.domain;

import org.broadleafcommerce.common.copy.MultiTenantCloneable;
import org.broadleafcommerce.core.promotionMessage.domain.PromotionMessage;

import java.io.Serializable;

/**
 * @author Chris Kittrell (ckittrell)
 */
public interface AdvancedOfferPromotionMessageXref extends Serializable, MultiTenantCloneable<AdvancedOfferPromotionMessageXref> {

    /**
     * Id of this AdvancedOfferPromotionMessageXref
     * @return
     */
    Long getId();

    /**
     * Sets the id of this AdvancedOfferPromotionMessageXref
     * @param id
     */
    void setId(Long id);

    /**
     * Gets the Offer
     * @return
     */
    Offer getOffer();

    /**
     * Sets the Offer
     * @param offer
     */
    void setOffer(Offer offer);

    /**
     * Gets the PromotionMessage
     * @return
     */
    PromotionMessage getPromotionMessage();

    /**
     * Sets the PromotionMessage
     * @param promotionMessage
     */
    void setPromotionMessage(PromotionMessage promotionMessage);

    /**
     * Gets the PromotionMessage type
     * @return
     */
    public String getMessageType();

    /**
     * Sets the PromotionMessage type
     * @param messageType
     */
    public void setMessageType(String messageType);
}
