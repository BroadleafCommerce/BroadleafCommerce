package org.broadleafcommerce.email.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.email.dao.EmailReportingDao;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.security.CustomerState;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author jfischer
 *
 */
@Service("emailTrackingManagerBLC")
public class EmailTrackingManagerImpl implements EmailTrackingManager {

    private static final Log LOG = LogFactory.getLog(EmailTrackingManagerImpl.class);

    @Resource
    protected EmailReportingDao emailReportingDao;

    @Resource
    protected CustomerState customerState;

    /* (non-Javadoc)
     * @see com.containerstore.web.task.service.EmailTrackingManager#createTrackedEmail(java.lang.String, java.lang.String, java.lang.String)
     */
    @Transactional
    public Long createTrackedEmail(String emailAddress, String type, String extraValue) {
        return emailReportingDao.createTracking(emailAddress, type, extraValue);
    }

    /* (non-Javadoc)
     * @see com.containerstore.web.task.service.EmailTrackingManager#recordClick(java.lang.String, javax.servlet.http.HttpServletRequest)
     */
    @SuppressWarnings("unchecked")
    @Transactional
    public void recordClick(Long emailId, HttpServletRequest request) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("recordClick() => Click detected for Email["+emailId+"]");
        }

        Map parameterMap = request.getParameterMap();
        Iterator keys = parameterMap.keySet().iterator();
        // clean up and normalize the query string
        ArrayList queryParms = new ArrayList();
        while ( keys.hasNext() ) {
            String p = (String)keys.next();
            // exclude email_id from the parms list
            if ( !p.equals("email_id") ) {
                queryParms.add( p );
            }
        }

        String newQuery = null;

        if ( queryParms.size() > 0 ) {

            String[] p = ( String[] ) queryParms.toArray( new String[ queryParms.size() ] );
            Arrays.sort( p );

            StringBuffer newQueryParms = new StringBuffer();
            for ( int cnt = 0; cnt < p.length; cnt++ ) {
                newQueryParms.append( p[ cnt ] );
                newQueryParms.append( "=" );
                newQueryParms.append( request.getParameter( p[ cnt ] ) );
                if ( cnt != p.length - 1 ) {
                    newQueryParms.append("&");
                }
            }
            newQuery = newQueryParms.toString();
        }
        String uri = request.getRequestURI();

        Customer customer = customerState.getCustomer(request);

        emailReportingDao.recordClick(emailId, customer, uri, newQuery);
    }

    /* (non-Javadoc)
     * @see com.containerstore.web.task.service.EmailTrackingManager#recordOpen(java.lang.String, javax.servlet.http.HttpServletRequest)
     */
    @Transactional
    public void recordOpen(Long emailId, HttpServletRequest request) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Recording open for email id: " + emailId);
        }
        // extract necessary information from the request and record the open
        emailReportingDao.recordOpen(emailId, request.getHeader("USER-AGENT" ));
    }

}
