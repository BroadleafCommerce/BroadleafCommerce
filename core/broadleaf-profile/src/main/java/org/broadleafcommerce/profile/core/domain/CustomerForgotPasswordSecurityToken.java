/*
 * #%L
 * BroadleafCommerce Profile
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
