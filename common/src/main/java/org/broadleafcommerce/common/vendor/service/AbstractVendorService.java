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
