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
import org.broadleafcommerce.common.io.AbstractRegisteringURLStreamHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Handler class to register a merged EhCache XML configuration file as a byte array so that 
 * URIs
 * 
 * @author kellytisdell
 *
 */
public class Handler extends AbstractRegisteringURLStreamHandler {

    private static byte[] mergedEhCacheXmlBytes = null;

    static {
        registerHandler(Handler.class);
    }
    
    public synchronized static void setMergedEhCacheXml(InputStream inputStream) throws IOException {
        if (mergedEhCacheXmlBytes != null) {
            throw new IllegalStateException("The merged EhCache XML file was already set and cannot be set again.");
        }
        
        if (inputStream == null) {
            throw new IllegalArgumentException("The provided InputStream was null.");
        }
        
        mergedEhCacheXmlBytes = IOUtils.toByteArray(inputStream);
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
        getMergedEhCacheXml();
        return new EhCacheUrlConnection(u);
    }
}
