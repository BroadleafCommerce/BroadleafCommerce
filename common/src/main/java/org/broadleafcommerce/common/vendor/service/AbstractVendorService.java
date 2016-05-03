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
package org.broadleafcommerce.common.vendor.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public abstract class AbstractVendorService {

    private static final Log LOG = LogFactory.getLog(AbstractVendorService.class);
    private static final String POST_METHOD = "POST";

    protected InputStream postMessage(Map<String, String>content, URL destination, String encodeCharset) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) destination.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod(POST_METHOD);

        OutputStreamWriter osw = null;
        try {
            osw = new OutputStreamWriter(connection.getOutputStream());
            boolean isFirst = true;
            for (String key : content.keySet()) {
                if (!isFirst) {
                    osw.write("&");
                }
                isFirst = false;
                String value = content.get(key);
                osw.write(URLEncoder.encode(key, encodeCharset));
                osw.write("=");
                osw.write(URLEncoder.encode(value, encodeCharset));
            }
            osw.flush();
            osw.close();
        } catch (IOException e) {
            // We'll try to avoid stopping processing and just log the error if the OutputStream doesn't close
            LOG.error("Problem closing the OuputStream to destination: " + destination.toExternalForm(), e);
        } finally {
            if (osw != null) {
                try { osw.close(); } catch (Throwable e) {}
            }
        }

        return new BufferedInputStream(connection.getInputStream());
    }
}
