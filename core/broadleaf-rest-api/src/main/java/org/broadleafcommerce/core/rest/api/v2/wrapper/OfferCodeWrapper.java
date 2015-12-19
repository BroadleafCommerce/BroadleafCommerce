/*
 * #%L
 * BroadleafCommerce Rest Api
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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
package org.broadleafcommerce.core.rest.api.v2.wrapper;

import org.springframework.context.ApplicationContext;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "offerCode")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class OfferCodeWrapper extends BaseWrapper implements APIWrapper<OfferCodeWrapper>, APIUnwrapper<OfferCodeWrapper> {

    @XmlElement
    protected Long id;

    @XmlElement
    protected String firstName;

    @XmlElement
    protected String lastName;

    @Override
    public OfferCodeWrapper unwrap(HttpServletRequest request, ApplicationContext context) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void wrapDetails(OfferCodeWrapper model, HttpServletRequest request) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void wrapSummary(OfferCodeWrapper model, HttpServletRequest request) {
        // TODO Auto-generated method stub
        
    }
}
