/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * NOTICE:  All information contained herein is, and remains
 * the property of Broadleaf Commerce, LLC
 * The intellectual and technical concepts contained
 * herein are proprietary to Broadleaf Commerce, LLC
 * and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Broadleaf Commerce, LLC.
 * #L%
 */
package org.broadleafcommerce.common.cache;

/**
 * @author Jeff Fischer
 */
public enum CacheStatType {
    PAGE_CACHE_HIT_RATE, STRUCTURED_CONTENT_CACHE_HIT_RATE, URL_HANDLER_CACHE_HIT_RATE,
    PRODUCT_URL_MISSING_CACHE_HIT_RATE, CATEGORY_URL_MISSING_CACHE_HIT_RATE
}
