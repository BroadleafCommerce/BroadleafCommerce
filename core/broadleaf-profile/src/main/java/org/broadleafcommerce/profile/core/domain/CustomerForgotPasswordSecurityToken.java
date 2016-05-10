/*
 * #%L
 * BroadleafCommerce Profile
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
package org.broadleafcommerce.profile.core.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author bpolster
 *
 */
public interface CustomerForgotPasswordSecurityToken extends Serializable {
    
    /**
     * Returns the security token.
     * @return
     */
    public String getToken();
    
    /**
     * Sets the security token.
     * @return
     */
    public void setToken(String token);

    /**
     * Date the token was created
     * @return
     */
    public Date getCreateDate();

    /**
     * Set the generation date for the token.
     * @return
     */
    public void setCreateDate(Date date);

    /**
     * Date the token was used to reset the password.
     * @return
     */
    public Date getTokenUsedDate();

    /**
     * Set the date the token was used to reset the password.
     * @return
     */
    public void setTokenUsedDate(Date date);

    /**
     * Id associated with this forgot password token.
     * @return
     */
    public Long getCustomerId();

    /**
     * Id associated with this forgot password token.
     * @return
     */
    public void setCustomerId(Long customerId);

    /**
     * Returns true if the token has already been used.
     */
    public boolean isTokenUsedFlag();

    /**
     * Sets the token used flag. 
     */
    public void setTokenUsedFlag(boolean tokenUsed);
}
