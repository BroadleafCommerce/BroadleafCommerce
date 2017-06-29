package org.broadleafcommerce.core.linked.data;

import org.codehaus.jettison.json.JSONException;

/**
 * Service for getting the linked data for category pages. Implemented in
 * {@link HomepageLinkedDataServiceImpl} which can be extended to modify
 * or add additional metadata.
 *
 * @author Jacob Mitash
 */
public interface HomepageLinkedDataService {

    /**
     * Adds linked data to the homepage
     *
     * @param url the URL of the home page
     * @return JSON string of linked data
     */
    String getLinkedData(String url) throws JSONException;
}
