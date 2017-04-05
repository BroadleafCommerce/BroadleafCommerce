/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.promotionMessage.domain;

import org.broadleafcommerce.common.copy.MultiTenantCloneable;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.media.domain.Media;
import org.broadleafcommerce.common.persistence.Status;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Chris Kittrell (ckittrell)
 */
public interface PromotionMessage extends Status, Serializable,MultiTenantCloneable<PromotionMessage> {

    public void setId(Long id);

    public Long getId();

    public String getName();

    public void setName(String name);

    public String getMessage();

    public void setMessage(String message);

    public Media getMedia();

    public void setMedia(Media media);

    public int getPriority();

    public void setPriority(Integer priority);

    public Date getStartDate();

    public void setStartDate(Date startDate);

    public Date getEndDate();

    public void setEndDate(Date endDate);

    public String getMessagePlacement();

    public void setMessagePlacement(String messagePlacement);

    public Locale getLocale();

    public void setLocale(Locale locale);

}
