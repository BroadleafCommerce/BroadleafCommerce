/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
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
