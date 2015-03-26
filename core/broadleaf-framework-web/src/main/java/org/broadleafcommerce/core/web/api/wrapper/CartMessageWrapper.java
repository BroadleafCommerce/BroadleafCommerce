/*
 * #%L
 * BroadleafCommerce Framework Web
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
package org.broadleafcommerce.core.web.api.wrapper;

import org.broadleafcommerce.core.order.service.call.ActivityMessageDTO;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "message")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class CartMessageWrapper extends BaseWrapper implements APIWrapper<ActivityMessageDTO> {

    @XmlElement
    protected String message;
    @XmlElement
    protected String messageType;
    @XmlElement
    protected Integer priority;
    @XmlElement
    protected String errorCode;
    @Override
    public void wrapDetails(ActivityMessageDTO model, HttpServletRequest request) {
        this.message = model.getMessage();
        this.priority = model.getPriority();
        this.messageType = model.getType();
        this.errorCode = model.getErrorCode();
    }

    @Override
    public void wrapSummary(ActivityMessageDTO model, HttpServletRequest request) {
        wrapDetails(model, request);
    }

    
    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    
    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    
    /**
     * @return the messageType
     */
    public String getMessageType() {
        return messageType;
    }

    
    /**
     * @param messageType the messageType to set
     */
    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    
    /**
     * @return the priority
     */
    public Integer getPriority() {
        return priority;
    }

    
    /**
     * @param priority the priority to set
     */
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    
    /**
     * @return the errorCode
     */
    public String getErrorCode() {
        return errorCode;
    }

    
    /**
     * @param errorCode the errorCode to set
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

}
