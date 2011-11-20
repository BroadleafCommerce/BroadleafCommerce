package org.broadleafcommerce.cms.web;

/**
 * Enum that indicates the action to take when processing a URL.
 * <ul>
 *     <li>PAGE - indicates that the URL will be handled as a CMS managed page</li>
 *     <li>PRODUCT - indicates that the URL is an SEO manged product page</li>
 *     <li>PROCEED - indicates that the URL should be passed through and is not handled by BLC custom filters</li>
 *     <li>REDIRECT - indicates that the URL should be redirected to another URL</li>
 *     <li>UNKNOWN - indicates that it has not yet been determined how to process the URL</li>
 * </ul>
 *
 *   PAGE indicates that this URL has been designated to be handled as a custom CMS managed page
 * Created by bpolster.
 */
public enum ProcessURLAction {
    PAGE,
    PRODUCT,
    PROCEED,
    REDIRECT,
    UNKNOWN
}
