/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.common.payment.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Elbert Bautista (elbertbautista)
 */
public class GiftCardDTO<T> {

    protected T parent;

    protected Map<String, Object> additionalFields;
    protected String giftCardNum;
    protected String giftCardMasked;

    public GiftCardDTO() {
        this.additionalFields = new HashMap<String, Object>();
    }

    public GiftCardDTO(T parent) {
        this.additionalFields = new HashMap<String, Object>();
        this.parent = parent;
    }

    public T done() {
        return parent;
    }

    public GiftCardDTO<T> additionalFields(String key, Object value) {
        additionalFields.put(key, value);
        return this;
    }

    public GiftCardDTO<T> giftCardNum(String giftCardNum) {
        this.giftCardNum = giftCardNum;
        return this;
    }

    public GiftCardDTO<T> giftCardMasked(String giftCardMasked) {
        this.giftCardMasked = giftCardMasked;
        return this;
    }
}
