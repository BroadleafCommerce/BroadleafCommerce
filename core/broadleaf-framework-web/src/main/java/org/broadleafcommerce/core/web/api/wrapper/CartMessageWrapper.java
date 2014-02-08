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

}
