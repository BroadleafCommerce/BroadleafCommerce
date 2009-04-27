package org.broadleafcommerce.email.service;

import java.util.Map;

import org.broadleafcommerce.profile.domain.Customer;


/**
 * @author jfischer
 *
 */
public interface EmailTrackingManager {

    public Long createTrackedEmail(String emailAddress, String type, String extraValue);
    public void recordOpen (Long emailId, Map<String, String> extraValues);
    public void recordClick(Long emailId , Map<String, String> parameterMap, Customer customer, Map<String, String> extraValues);

}
