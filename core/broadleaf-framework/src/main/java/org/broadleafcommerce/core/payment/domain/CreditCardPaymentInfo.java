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

package org.broadleafcommerce.core.payment.domain;

public interface CreditCardPaymentInfo extends Referenced {

    /**
     * @return the id
     */
    public Long getId();

    /**
     * @param id the id to set
     */
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
