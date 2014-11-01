/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.email.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * @author jfischer
 *
 */
public interface EmailTracking extends Serializable {

    public abstract Long getId();

    public abstract void setId(Long id);

    /**
     * @return the emailAddress
     */
    public abstract String getEmailAddress();

    /**
     * @param emailAddress the emailAddress to set
     */
    public abstract void setEmailAddress(String emailAddress);

    /**
     * @return the dateSent
     */
    public abstract Date getDateSent();

    /**
     * @param dateSent the dateSent to set
     */
    public abstract void setDateSent(Date dateSent);

    /**
     * @return the type
     */
    public abstract String getType();

    /**
     * @param type the type to set
     */
    public abstract void setType(String type);

}
