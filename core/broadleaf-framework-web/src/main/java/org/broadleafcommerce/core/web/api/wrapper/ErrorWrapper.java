/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.web.api.wrapper;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

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
