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
package org.broadleafcommerce.common.email.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.email.dao.EmailReportingDao;
import org.broadleafcommerce.common.util.TransactionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.Resource;

/**
 * @author jfischer
 */
@Service("blEmailTrackingManager")
public class EmailTrackingManagerImpl implements EmailTrackingManager {

    private static final Log LOG = LogFactory.getLog(EmailTrackingManagerImpl.class);

    @Resource(name = "blEmailReportingDao")
    protected EmailReportingDao emailReportingDao;

    @Override
    @Transactional(TransactionUtils.DEFAULT_TRANSACTION_MANAGER)
    public Long createTrackedEmail(String emailAddress, String type, String extraValue) {
        return emailReportingDao.createTracking(emailAddress, type, extraValue);
    }

    @Override
    @Transactional(TransactionUtils.DEFAULT_TRANSACTION_MANAGER)
    public void recordClick(Long emailId, Map<String, String> parameterMap, String customerId, Map<String, String> extraValues) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("recordClick() => Click detected for Email[" + emailId + "]");
        }

        Iterator<String> keys = parameterMap.keySet().iterator();
        // clean up and normalize the query string
        ArrayList<String> queryParms = new ArrayList<String>();
        while (keys.hasNext()) {
            String p = keys.next();
            // exclude email_id from the parms list
            if (!p.equals("email_id")) {
                queryParms.add(p);
            }
        }

        String newQuery = null;

        if (!queryParms.isEmpty()) {

            String[] p = queryParms.toArray(new String[queryParms.size()]);
            Arrays.sort(p);

            StringBuffer newQueryParms = new StringBuffer();
            for (int cnt = 0; cnt < p.length; cnt++) {
                newQueryParms.append(p[cnt]);
                newQueryParms.append("=");
                newQueryParms.append(parameterMap.get(p[cnt]));
                if (cnt != p.length - 1) {
                    newQueryParms.append("&");
                }
            }
            newQuery = newQueryParms.toString();
        }

        emailReportingDao.recordClick(emailId, customerId, extraValues.get("requestUri"), newQuery);
    }

    /*
     * (non-Javadoc)
     * @see
     * com.containerstore.web.task.service.EmailTrackingManager#recordOpen(java
     * .lang.String, javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void recordOpen(Long emailId, Map<String, String> extraValues) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Recording open for email id: " + emailId);
        }
        // extract necessary information from the request and record the open
        emailReportingDao.recordOpen(emailId, extraValues.get("userAgent"));
    }

}
