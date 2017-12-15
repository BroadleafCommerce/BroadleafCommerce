/*
 * #%L
 * BroadleafCommerce CMS Module
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
package org.broadleafcommerce.cms.web.file;


import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Created by jfischer
 */
public class StaticAssetView implements View {

    private static final Log LOG = LogFactory.getLog(StaticAssetView.class);

    protected boolean browserAssetCachingEnabled = true;
    protected long cacheSeconds = 60 * 60 * 24;

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
            } else {
                response.setHeader("Cache-Control","public");
                response.setHeader("Pragma","cache");
                if (!StringUtils.isEmpty(request.getHeader("If-Modified-Since"))) {
                    long lastModified = request.getDateHeader("If-Modified-Since");
                    Calendar last = Calendar.getInstance();
                    last.setTime(new Date(lastModified));
                    Calendar check = Calendar.getInstance();
                    check.add(Calendar.SECOND, -2 * new Long(cacheSeconds).intValue());
                    if (check.compareTo(last) < 0) {
                        response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                        return;
                    }
                } else {
                    Calendar check = Calendar.getInstance();
                    check.add(Calendar.SECOND, -1 * new Long(cacheSeconds).intValue());
                    response.setDateHeader ("Last-Modified", check.getTimeInMillis());
                }
                Calendar cal = Calendar.getInstance();
                long year = cacheSeconds * 365;
                cal.add(Calendar.SECOND, new Long(year).intValue());
                response.setDateHeader ("Expires", cal.getTimeInMillis());
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

    public long getCacheSeconds() {
        return cacheSeconds;
    }

    public void setCacheSeconds(long cacheSeconds) {
        this.cacheSeconds = cacheSeconds;
    }
}
