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
package org.broadleafcommerce.core.order.domain;

import org.broadleafcommerce.common.money.Money;

import java.io.Serializable;

public interface DiscreteOrderItemFeePrice extends Serializable {

    public abstract Long getId();

    public abstract void setId(Long id);

    public DiscreteOrderItem getDiscreteOrderItem();

    public void setDiscreteOrderItem(DiscreteOrderItem discreteOrderItem);

    public abstract Money getAmount();

    public abstract void setAmount(Money amount);

    public abstract String getName();

    public abstract void setName(String name);

    public abstract String getReportingCode();

    public abstract void setReportingCode(String reportingCode);

    public DiscreteOrderItemFeePrice clone();

}
