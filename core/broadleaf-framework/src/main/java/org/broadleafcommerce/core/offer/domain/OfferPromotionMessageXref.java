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
package org.broadleafcommerce.core.offer.domain;

import org.broadleafcommerce.core.promotionMessage.domain.PromotionMessage;

import java.io.Serializable;

/**
 * @author Chris Kittrell (ckittrell)
 */
public interface OfferPromotionMessageXref extends Serializable {

    /**
     * Id of this OfferPromotionMessageXref
     * @return
     */
    Long getId();

    /**
     * Sets the id of this OfferPromotionMessageXref
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
