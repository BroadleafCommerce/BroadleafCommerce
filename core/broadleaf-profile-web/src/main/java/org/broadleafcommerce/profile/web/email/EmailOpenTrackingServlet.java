/*
 * #%L
 * BroadleafCommerce Profile Web
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
package org.broadleafcommerce.profile.web.email;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.email.service.EmailTrackingManager;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author jfischer
 *
 */
public class EmailOpenTrackingServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final Log LOG = LogFactory.getLog(EmailOpenTrackingServlet.class);

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest , javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String url = request.getPathInfo();
        Long emailId = null;

        // Parse the URL for the Email ID and Image URL
        if (url != null) {
            String[] items = url.split("/");
            emailId = Long.valueOf(items[1]);
        }

        // Record the open
        if (emailId != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("service() => Recording Open for Email[" + emailId + "]");
            }
            WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
            EmailTrackingManager emailTrackingManager = (EmailTrackingManager) context.getBean("blEmailTrackingManager");
            String userAgent = request.getHeader("USER-AGENT");
            Map<String, String> extraValues = new HashMap<String, String>();
            extraValues.put("userAgent", userAgent);
            emailTrackingManager.recordOpen(emailId, extraValues);
        }

        response.setContentType("image/gif");
        BufferedInputStream bis = null;
        OutputStream out = response.getOutputStream();
        try {
            bis = new BufferedInputStream(EmailOpenTrackingServlet.class.getResourceAsStream("clear_dot.gif"));
            boolean eof = false;
            while (!eof) {
                int temp = bis.read();
                if (temp == -1) {
                    eof = true;
                } else {
                    out.write(temp);
                }
            }
        } finally {
            if (bis != null) {
                try{ bis.close(); } catch (Throwable e) {
                    LOG.error("Unable to close output stream in EmailOpenTrackingServlet", e);
                }
            }
            //Don't close the output stream controlled by the container. The container will
            //handle this.
        }
    }
}
