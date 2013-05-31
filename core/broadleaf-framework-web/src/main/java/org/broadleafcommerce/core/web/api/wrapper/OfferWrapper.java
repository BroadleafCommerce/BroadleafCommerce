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

import org.broadleafcommerce.core.offer.domain.Offer;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This is a JAXB wrapper around OfferWrapper.
 * <p/>
 * User: Priyesh Patel
 */
@XmlRootElement(name = "paymentInfo")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class OfferWrapper extends BaseWrapper implements APIWrapper<Offer> {

    @XmlElement
    protected String marketingMessage;;

    @XmlElement
    protected BroadleafEnumerationTypeWrapper offerType;;

    @XmlElement
    protected BroadleafEnumerationTypeWrapper discountType;;

    @Override
    public void wrap(Offer model, HttpServletRequest request) {
        this.marketingMessage = model.getMarketingMessage();
        this.offerType = (BroadleafEnumerationTypeWrapper) context.getBean(BroadleafEnumerationTypeWrapper.class.getName());
        this.offerType.wrap(model.getType(), request);
        this.discountType = (BroadleafEnumerationTypeWrapper) context.getBean(BroadleafEnumerationTypeWrapper.class.getName());
        this.discountType.wrap(model.getDiscountType(), request);

    }

}
