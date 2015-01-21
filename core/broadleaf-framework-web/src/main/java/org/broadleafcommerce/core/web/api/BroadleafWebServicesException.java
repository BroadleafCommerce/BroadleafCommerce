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
package org.broadleafcommerce.core.web.api;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * The purpose of this is to allow REST endpoints to build an exception with 
 * @author Kelly Tisdell
 *
 */
public class BroadleafWebServicesException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public static final String UNKNOWN_ERROR = "org.broadleafcommerce.core.web.api.BroadleafWebServicesException.unknownError";
    public static final String NOT_FOUND = "org.broadleafcommerce.core.web.api.BroadleafWebServicesException.notFound";
    public static final String PRODUCT_NOT_FOUND = "org.broadleafcommerce.core.web.api.BroadleafWebServicesException.productNotFound";
    public static final String CATEGORY_NOT_FOUND = "org.broadleafcommerce.core.web.api.BroadleafWebServicesException.categoryNotFound";
    public static final String SKU_NOT_FOUND = "org.broadleafcommerce.core.web.api.BroadleafWebServicesException.skuNotFound";
    public static final String SEARCH_ERROR = "org.broadleafcommerce.core.web.api.BroadleafWebServicesException.errorExecutingSearch";
    public static final String SEARCH_QUERY_EMPTY = "org.broadleafcommerce.core.web.api.BroadleafWebServicesException.searchQueryEmpty";
    public static final String SEARCH_QUERY_MALFORMED = "org.broadleafcommerce.core.web.api.BroadleafWebServicesException.searchQueryMalformed";
    public static final String INVALID_CATEGORY_ID = "org.broadleafcommerce.core.web.api.BroadleafWebServicesException.invalidCategoryId";
    public static final String CART_NOT_FOUND = "org.broadleafcommerce.core.web.api.BroadleafWebServicesException.cartNotFound";
    public static final String CART_ITEM_NOT_FOUND = "org.broadleafcommerce.core.web.api.BroadleafWebServicesException.cartItemNotFound";
    public static final String CART_PRICING_ERROR = "org.broadleafcommerce.core.web.api.BroadleafWebServicesException.cartPricingError";
    public static final String UPDATE_CART_ERROR = "org.broadleafcommerce.core.web.api.BroadleafWebServicesException.updateCartError";
    public static final String PROMO_CODE_MAX_USAGES = "org.broadleafcommerce.core.web.api.BroadleafWebServicesException.promoCodeMaxUsages";
    public static final String PROMO_CODE_INVALID = "org.broadleafcommerce.core.web.api.BroadleafWebServicesException.promoCodeInvalid";
    public static final String FULFILLMENT_GROUP_NOT_FOUND = "org.broadleafcommerce.core.web.api.BroadleafWebServicesException.fulfillmentGroupNotFound";
    public static final String FULFILLMENT_OPTION_NOT_FOUND = "org.broadleafcommerce.core.web.api.BroadleafWebServicesException.fulfillmentOptionNotFound";
    public static final String CUSTOMER_NOT_FOUND = "org.broadleafcommerce.core.web.api.BroadleafWebServicesException.customerNotFound";
    public static final String CHECKOUT_PROCESSING_ERROR = "org.broadleafcommerce.core.web.api.BroadleafWebServicesException.checkoutProcessingError";
    public static final String CONTENT_TYPE_NOT_SUPPORTED = "org.broadleafcommerce.core.web.api.BroadleafWebServicesException.contentTypeNotSupported";

    protected int httpStatusCode = 500;

    protected Map<String, Object[]> messages;

    protected Map<String, String> translatedMessages;

    protected Locale locale;

    public BroadleafWebServicesException(int httpStatusCode, Locale locale, Map<String, Object[]> messages, Throwable cause) {
        super(cause);
        this.httpStatusCode = httpStatusCode;
        this.locale = locale;
        this.messages = messages;
    }

    public static BroadleafWebServicesException build(int httpStatusCode) {
        return build(httpStatusCode, null, null, null);
    }

    public static BroadleafWebServicesException build(int httpStatusCode, Throwable t) {
        return build(httpStatusCode, null, null, t);
    }

    public static BroadleafWebServicesException build(int httpStatusCode, Locale locale) {
        return build(httpStatusCode, locale, null, null);
    }

    public static BroadleafWebServicesException build(int httpStatusCode, Locale locale, Throwable t) {
        return build(httpStatusCode, locale, null, t);
    }

    public static BroadleafWebServicesException build(int httpStatusCode, Locale locale, Map<String, Object[]> messages) {
        return build(httpStatusCode, locale, messages, null);
    }

    public static BroadleafWebServicesException build(int httpStatusCode, Locale locale, Map<String, Object[]> messages, Throwable cause) {
        return new BroadleafWebServicesException(httpStatusCode, locale, messages, cause);
    }

    /**
     * Returns the intended HTTP status code (e.g. 400, 403, 404, 500, etc...). Default is 500.
     * @return
     */
    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    /**
     * Gets a map of message keys and object arrays. These will generally be used to build a 
     * an internationalized message in the response. The map key is typically going to be a 
     * message bundle key, and the object array provides parameters for the message.  For more information, 
     * see Spring's <code>org.springframework.context.MessageSource</code>.
     * @return
     */
    public Map<String, Object[]> getMessages() {
        if (this.messages == null) {
            this.messages = new HashMap<String, Object[]>();
        }
        return this.messages;
    }

    /**
     * Gets a map of messages that have already been translated. BLC provides a number of translation mechanisms 
     * for entities in the DB.
     * 
     * @return
     */
    public Map<String,String> getTranslatedMessages() {
        if (this.translatedMessages == null) {
            this.translatedMessages = new HashMap<String, String>();
        }
        return this.translatedMessages;
    }

    /**
     * Adds a translated message. The assumption is that the message added to this map does not need to be 
     * translated or internationalized any further. It is already translated according to the user's Locale.
     * @param key
     * @param message
     * @return
     */
    public BroadleafWebServicesException addTranslatedMessage(String key, String message) {
        getTranslatedMessages().put(key, message);
        return this;
    }

    /**
     * Convenience method for adding a message with no parameters
     * @param key
     * @return
     */
    public BroadleafWebServicesException addMessage(String key) {
        return addMessage(key, null);
    }

    /**
     * Convenience method for adding a message with a single parameter
     * @param key
     * @param param
     * @return
     */
    public BroadleafWebServicesException addMessage(String key, Object param) {
        if (param != null) {
            return addMessage(key, new Object[] { param });
        } else {
            return addMessage(key, new Object[0]);
        }
    }

    /**
     * Convenience mentod for adding a message with  multiple parameters
     * @param key
     * @param params
     * @return
     */
    public BroadleafWebServicesException addMessage(String key, Object[] params) {
        getMessages().put(key, params);
        return this;
    }

    /**
     * Returns the <code>java.util.Locale</code> that messages should be formatted in.
     * @return
     */
    public Locale getLocale() {
        return this.locale;
    }
}
