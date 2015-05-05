package org.broadleafcommerce.common.web.resource.resolver;


public class BroadleafResourceResolverOrder {

    // Negative values should occur before any custom resolvers
    public static int THEME_FILE_URL_RESOLVER = -1000;
    public static int BLC_JS_PATH_RESOLVER = -2000;

    // Implementors typically want dynamic URL before the cache resolver (e.g. BLC_CACHE_RESOURCE_RESOLVER -1) 
    // and anything else after the version resolver (e.g. BLC_VERSION_RESOURCE_RESOLVER + 1)
    public static int BLC_CACHE_RESOURCE_RESOLVER = 1000;
    public static int BLC_VERSION_RESOURCE_RESOLVER = 2000;

    // Custom resolvers (various lookup and file modification scenarios)
    public static int BLC_BUNDLE_RESOURCE_RESOLVER = 10000;
    public static int BLC_JS_RESOURCE_RESOLVER = 11000;
    public static int BLC_SYSTEM_PROPERTY_RESOURCE_RESOLVER = 12000;
    public static int BLC_THEME_FILE_RESOLVER = 13000;

    // Path Resolvers should always be last
    public static int BLC_PATH_RESOURCE_RESOLVER = 99000;

}
