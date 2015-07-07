/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
package org.broadleafcommerce.core.web.checkout.model;

import java.io.Serializable;

/**
 * @author Jerry Ocanas (jocanas)
 */
public class GiftCardInfoForm implements Serializable {

    private static final long serialVersionUID = 1L;

    private String giftCardNumber;
    private String giftCardEmailAddress;

    public String getGiftCardNumber() {
        return giftCardNumber;
    }

    public void setGiftCardNumber(String giftCardNumber) {
        this.giftCardNumber = giftCardNumber;
    }

    public String getGiftCardEmailAddress() {
        return giftCardEmailAddress;
    }

    public void setGiftCardEmailAddress(String giftCardEmailAddress) {
        this.giftCardEmailAddress = giftCardEmailAddress;
    }

}
