/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.cms.web.file;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.View;

/**
 * Created by jfischer
 */
public class StaticAssetView implements View {

    private static final Log LOG = LogFactory.getLog(StaticAssetView.class);

    protected boolean browserAssetCachingEnabled = true;

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String cacheFilePath = (String) model.get("cacheFilePath");
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(cacheFilePath));
        try {
            String mimeType = (String) model.get("mimeType");
            response.setContentType(mimeType);
            if (!browserAssetCachingEnabled) {
			    response.setHeader("Cache-Control","no-cache");
	            response.setHeader("Pragma","no-cache");
	            response.setDateHeader ("Expires", 0);
            }
            OutputStream os = response.getOutputStream();
            boolean eof = false;
            while (!eof) {
                int temp = bis.read();
                if (temp < 0) {
                    eof = true;
                } else {
                    os.write(temp);
                }
            }
            os.flush();
		} catch (Exception e) {
            if (e.getCause() instanceof SocketException) {
			    if (LOG.isDebugEnabled()) {
                    LOG.debug("Unable to stream asset", e);
                }
            } else {
                LOG.error("Unable to stream asset", e);
                throw e;
            }
		} finally {
            try {
                bis.close();
            } catch (Throwable e) {
                //do nothing
            }
        }
    }

    public boolean isBrowserAssetCachingEnabled() {
        return browserAssetCachingEnabled;
    }

    public void setBrowserAssetCachingEnabled(boolean browserAssetCachingEnabled) {
        this.browserAssetCachingEnabled = browserAssetCachingEnabled;
    }
}
