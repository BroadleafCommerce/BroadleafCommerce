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
package org.broadleafcommerce.core.order.domain;

import org.broadleafcommerce.common.copy.MultiTenantCloneable;

import java.io.Serializable;

public interface PersonalMessage extends Serializable, MultiTenantCloneable<PersonalMessage> {

    public Long getId();

    public void setId(Long id);

    public String getMessageTo();

    public void setMessageTo(String messageTo);

    public String getMessageFrom();

    public void setMessageFrom(String messageFrom);

    public String getMessage();

    public void setMessage(String message);

    public String getOccasion();

    public void setOccasion(String occasion);
}
