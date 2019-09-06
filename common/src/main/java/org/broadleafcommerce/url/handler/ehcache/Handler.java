/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2019 Broadleaf Commerce
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
package org.broadleafcommerce.url.handler.ehcache;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.extensibility.cache.ehcache.DefaultEhCacheUtil;
import org.broadleafcommerce.common.io.AbstractRegisteringURLStreamHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Handler class to register a merged EhCache XML configuration file as a byte array so that 
 * URIs
 * 
 * @author Kelly Tisdell
 *
 */
public class Handler extends AbstractRegisteringURLStreamHandler {

    private static final Log LOG = LogFactory.getLog(Handler.class);
    private static byte[] mergedEhCacheXmlBytes = null;

    static {
        registerHandler(Handler.class);
    }
    
    public synchronized static void setMergedEhCacheXml(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            throw new IllegalArgumentException("The provided InputStream was null.");
        }
        
        if (mergedEhCacheXmlBytes != null) {
            LOG.warn("A merged EhCache XML resource has been passed to the URLStreamHandler multiple times. "
                    + "This is expected to be a static, immutable resource once merged."
                    + "The URLStreamHandler is responsible for "
                    + "providing that merged resource to the JCache Provider via the 'dummy' URI, '" 
                    + DefaultEhCacheUtil.EH_CACHE_MERGED_XML_RESOUCE_URI 
                    + "'. If the JCache provider has initialized the CacheManager for that "
                    + "URI it will not be refreshed or updated, even if the XML byte stream has changed.");
        } else {
            mergedEhCacheXmlBytes = IOUtils.toByteArray(inputStream);
        }
    }
    
    public synchronized static byte[] getMergedEhCacheXml() throws IOException {
        if (mergedEhCacheXmlBytes == null) {
            throw new IOException("The merged EhCache XML file was not set.  Please call "
                    + Handler.class.getName() + "#setMergedEhCacheXml(InputStream) prior to opening a connection.");
        }
        return mergedEhCacheXmlBytes;
    }

    @Override
    protected URLConnection openConnection(URL u) throws IOException {
        synchronized (Handler.class) {
            getMergedEhCacheXml();
            return new EhCacheUrlConnection(u);
        }
    }
}
