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
package org.broadleafcommerce.core.payment.domain;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * This class is useful if you wish to delineate in the paymentinfo
 * the various components that make up the total being charged. This
 * is optional and not all payment modules support.
 * 
 * @author jfischer
 *
 */
public interface AmountItem extends Serializable {

    public abstract Long getId();

    public abstract void setId(Long id);

    public abstract String getShortDescription();

    public abstract void setShortDescription(String shortDescription);

    public abstract String getDescription();

    public abstract void setDescription(String description);

    public abstract BigDecimal getUnitPrice();

    public abstract void setUnitPrice(BigDecimal unitPrice);

    public abstract Long getQuantity();

    public abstract void setQuantity(Long quantity);

    public abstract PaymentInfo getPaymentInfo();

    public abstract void setPaymentInfo(PaymentInfo paymentInfo);

    public abstract String getSystemId();

    public abstract void setSystemId(String systemId);
    
}