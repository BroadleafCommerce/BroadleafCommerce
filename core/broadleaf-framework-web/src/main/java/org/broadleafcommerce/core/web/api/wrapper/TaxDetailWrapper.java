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

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.order.domain.TaxDetail;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "taxDetail")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class TaxDetailWrapper extends BaseWrapper implements APIWrapper<TaxDetail> {

    @XmlElement
    protected Long id;

    @XmlElement
    protected BroadleafEnumerationTypeWrapper taxType;

    @XmlElement
    protected Money amount;

    @XmlElement
    protected BigDecimal rate;

    @XmlElement
    protected String currency;

    @XmlElement
    protected String jurisdictionName;

    @XmlElement
    protected String taxName;

    @XmlElement
    protected String region;

    @XmlElement
    protected String country;

    @Override
    public void wrapDetails(TaxDetail model, HttpServletRequest request) {
        this.id = model.getId();
        if (model.getType() != null) {
            this.taxType = (BroadleafEnumerationTypeWrapper) context.getBean(BroadleafEnumerationTypeWrapper.class.getName());
            this.taxType.wrapDetails(model.getType(), request);
        }
        this.amount = model.getAmount();
        this.rate = model.getRate();
        if (model.getCurrency() != null) {
            this.currency = model.getCurrency().getCurrencyCode();
        }
        this.jurisdictionName = model.getJurisdictionName();
        this.taxName = model.getTaxName();
        this.region = model.getRegion();
        this.country = model.getCountry();
    }

    @Override
    public void wrapSummary(TaxDetail model, HttpServletRequest request) {
        wrapDetails(model, request);
    }

}
