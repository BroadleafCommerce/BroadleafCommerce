/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.common.pricelist.domain;

import java.io.Serializable;

import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;

/**
 * 
 */
public interface PriceList extends Serializable {

    BroadleafCurrency getCurrencyCode();

    void setCurrencyCode(BroadleafCurrency currencyCode);

    public String getFriendlyName();

    public void setFriendlyName(String friendlyName);

    public Boolean getDefaultFlag();

    public void setDefaultFlag(Boolean defaultFlag);

    public String getPriceKey();

    public void setPriceKey(String priceKey);

    void setId(Long id);

    Long getId();
}
