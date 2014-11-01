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
package org.broadleafcommerce.core.payment.domain.secure;

import org.broadleafcommerce.core.payment.service.SecureOrderPaymentService;


/**
 * Entity associated with sensitive, secured credit card data. This data is stored specifically in the blSecurePU persistence.
 * All fetches and creates should go through {@link SecureOrderPaymentService} in order to properly decrypt/encrypt the data
 * from/to the database.
 *
 * @see {@link Referenced}
 * @author Phillip Verheyden (phillipuniverse)
 */
public interface CreditCardPayment extends Referenced {

    /**
     * @return the id
     */
    @Override
    public Long getId();

    /**
     * @param id the id to set
     */
    @Override
    public void setId(Long id);

    /**
     * @return the pan
     */
    public String getPan();

    /**
     * @param pan the pan to set
     */
    public void setPan(String pan);

    /**
     * @return the expirationMonth
     */
    public Integer getExpirationMonth();

    /**
     * @param expirationMonth the expirationMonth to set
     */
    public void setExpirationMonth(Integer expirationMonth);

    /**
     * @return the expirationYear
     */
    public Integer getExpirationYear();

    /**
     * @param expirationYear the expirationYear to set
     */
    public void setExpirationYear(Integer expirationYear);

    /**
     * @return the nameOnCard
     */
    public String getNameOnCard();

    /**
     * @param nameOnCard the name on the card to set
     */
    public void setNameOnCard(String nameOnCard);

    public String getCvvCode();

    public void setCvvCode(String cvvCode);
}
