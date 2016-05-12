/*
 * #%L
 * BroadleafCommerce Framework Web
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
package org.broadleafcommerce.core.web.api.wrapper;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @deprecated - use {@link com.broadleafcommerce.core.rest.api.v2.wrapper.ErrorWrapper}
 *
 */

@Deprecated
@XmlRootElement(name = "error")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class ErrorWrapper extends BaseWrapper {

    /*
     * This is in case the client prefers to return a 200 with with this error, but 
     * wants to represent the error code, say a 500, here.
     */
    @XmlElement
    protected Integer httpStatusCode;

    @XmlElementWrapper(name = "messages")
    @XmlElement(name = "message")
    protected List<ErrorMessageWrapper> messages;

    public Integer getHttpStatusCode() {
        return this.httpStatusCode;
    }

    public void setHttpStatusCode(Integer httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public List<ErrorMessageWrapper> getMessages() {
        if (this.messages == null) {
            this.messages = new ArrayList<ErrorMessageWrapper>();
        }
        return this.messages;
    }

    public void setMessages(List<ErrorMessageWrapper> messages) {
        this.messages = messages;
    }

}
