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

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.offer.domain.Adjustment;
import org.broadleafcommerce.core.offer.domain.Offer;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This is a JAXB wrapper around OrderAdjustmentWrapper.
 * <p/>
 * Author: ppatel, bpolster
 */
@XmlRootElement(name = "adjustment")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class AdjustmentWrapper extends BaseWrapper implements APIWrapper<Adjustment> {

    @XmlElement
    protected Long id;

    @XmlElement
    protected Long offerid;
    
    @XmlElement
    protected String reason;    

    @XmlElement
    protected String marketingMessage;

    @XmlElement
    protected Money adjustmentValue;

    @XmlElement
    protected String discountType;

    @XmlElement
    protected BigDecimal discountAmount;
    

    public void wrapDetails(Adjustment model, HttpServletRequest request) {
        if (model == null) {
            return;
        }
        this.id = model.getId();
        this.reason = model.getReason();

        Offer offer = model.getOffer();
        if (offer != null) {
            if (model.getReason() == null) {
                this.reason = "OFFER";
            }
            this.offerid = offer.getId();
            this.marketingMessage = offer.getMarketingMessage();
            this.discountType = offer.getDiscountType().getType();
            this.discountAmount = offer.getValue();
        }

        this.adjustmentValue = model.getValue();
    }

    @Override
    public void wrapSummary(Adjustment model, HttpServletRequest request) {
        wrapDetails(model, request);
    }
}
