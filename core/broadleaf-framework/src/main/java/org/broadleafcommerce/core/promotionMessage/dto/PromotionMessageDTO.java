/*
 * #%L
 * BroadleafCommerce Menu
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

package org.broadleafcommerce.core.promotionMessage.dto;

import org.broadleafcommerce.common.media.domain.Media;
import org.broadleafcommerce.core.promotionMessage.domain.PromotionMessage;
import org.broadleafcommerce.profile.core.dto.CustomerRuleHolder;

import java.io.Serializable;
import java.util.Date;

/**
 * A Generic DTO object that represents the information to display a {@link PromotionMessage}.
 *
 * @author Chris Kittrell (ckittrell)
 */
public class PromotionMessageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    protected String message;
    protected String messagePlacement;
    protected String localeCode;
    protected Integer priority;
    protected Date endDate;

    protected Media media;
    protected CustomerRuleHolder customerRuleHolder;

    public PromotionMessageDTO(PromotionMessage promotionMessage) {
        this.message = promotionMessage.getMessage();
        this.messagePlacement = promotionMessage.getMessagePlacement();
        this.localeCode = promotionMessage.getLocale() == null ? null : promotionMessage.getLocale().getLocaleCode();
        this.priority = promotionMessage.getPriority();
        this.endDate = promotionMessage.getEndDate();
        this.media = promotionMessage.getMedia();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getMessagePlacement() {
        return messagePlacement;
    }

    public void setMessagePlacement(String messagePlacement) {
        this.messagePlacement = messagePlacement;
    }

    public String getLocaleCode() {
        return localeCode;
    }

    public void setLocaleCode(String localeCode) {
        this.localeCode = localeCode;
    }

    public CustomerRuleHolder getCustomerRuleHolder() {
        return customerRuleHolder;
    }

    public void setCustomerRuleHolder(CustomerRuleHolder customerRuleHolder) {
        this.customerRuleHolder = customerRuleHolder;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
