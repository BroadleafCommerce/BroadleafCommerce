/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.pricing.domain;

import java.io.Serializable;
import java.math.BigDecimal;

@Deprecated
public interface ShippingRate extends Serializable {

    public Long  getId();
    public void setId(Long id);
    public String getFeeType();
    public void setFeeType(String feeType);
    public String getFeeSubType();
    public void setFeeSubType(String feeSubType);
    public Integer getFeeBand();
    public void setFeeBand(Integer feeBand);
    public BigDecimal getBandUnitQuantity();
    public void setBandUnitQuantity(BigDecimal bandUnitQuantity);
    public BigDecimal getBandResultQuantity();
    public void setBandResultQuantity(BigDecimal bandResultQuantity);
    public Integer getBandResultPercent();
    public void setBandResultPercent(Integer bandResultPersent);

}
